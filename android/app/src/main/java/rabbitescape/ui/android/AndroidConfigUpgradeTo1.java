package rabbitescape.ui.android;

import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.ConfigKeys;
import rabbitescape.engine.config.ConfigTools;
import rabbitescape.engine.config.IConfigStorage;
import rabbitescape.engine.config.IConfigUpgrade;

public class AndroidConfigUpgradeTo1 implements IConfigUpgrade
{
    @Override
    public void run( IConfigStorage storage )
    {
        if ( storage instanceof IAndroidConfigStorage )
        {
            upgradeAndroid( (IAndroidConfigStorage)storage );
            storage.set( Config.CFG_VERSION, "1" );
        }
        else
        {
            throw new RuntimeException(
                "Unexpected config storage type: " + storage.getClass().getSimpleName() );
        }
    }

    private void upgradeAndroid( IAndroidConfigStorage storage )
    {
        /*
            AndroidPreferencesBasedLevelsCompleted held your progress like this:
            levels.completed.01_easy = 13
            levels.completed.02_medium = 3

            and we must transform that to something like what
            ByNumberConfigBasedLevelsCompleted does:
            levels.completed = '{"easy":13,"medium":3}'

            Note also that in Android we never properly dealt with the
            transition from e.g. "easy" to "01_easy" so we'll fix that
            up here too.
        */

        SharedPreferences prefs = storage.getPrefs();
        SharedPreferences.Editor editor = prefs.edit();

        boolean doneSomething = false;

        doneSomething = upgradeLevelsCompleted( prefs, editor ) || doneSomething;
        doneSomething = upgradeMuted( prefs, editor ) || doneSomething;

        if ( doneSomething )
        {
            editor.commit();
        }
    }

    private static boolean upgradeLevelsCompleted(
        SharedPreferences prefs, SharedPreferences.Editor editor )
    {
        boolean doneSomething = false;

        Map<String, Integer> newMap = new HashMap<String, Integer>();

        final String LEVELS_COMPLETED = "levels.completed.";

        for ( Map.Entry<String, ?> entry : prefs.getAll().entrySet() )
        {
            if ( entry.getKey().startsWith( LEVELS_COMPLETED ) )
            {
                newMap.put(
                    stripNumber_(
                        entry.getKey().substring( LEVELS_COMPLETED.length() )
                    ),
                    (Integer)entry.getValue()
                );

                editor.remove( entry.getKey() );

                doneSomething = true;
            }
        }

        if ( doneSomething )
        {
            editor.putString( ConfigKeys.CFG_LEVELS_COMPLETED, ConfigTools.mapToString( newMap ) );
        }

        return doneSomething;
    }

    private static String stripNumber_( String levelsDir )
    {
        return levelsDir.replaceAll( "[0-9_]", "" );
    }

    private static boolean upgradeMuted( SharedPreferences prefs, SharedPreferences.Editor editor )
    {
        boolean doneSomething = false;

        final String RABBITESCAPE_MUTED = "rabbitescape.muted";
        final String MUTED = "muted";

        if ( prefs.contains( RABBITESCAPE_MUTED ) )
        {
            boolean muted = prefs.getBoolean( RABBITESCAPE_MUTED, false );
            editor.remove( RABBITESCAPE_MUTED );
            editor.putString( MUTED, String.valueOf( muted ) );
            doneSomething = true;
        }

        return doneSomething;
    }
}

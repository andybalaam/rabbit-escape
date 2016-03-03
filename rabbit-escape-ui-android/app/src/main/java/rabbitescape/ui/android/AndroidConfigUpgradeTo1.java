package rabbitescape.ui.android;

import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

import rabbitescape.engine.config.ConfigKeys;
import rabbitescape.engine.config.ConfigTools;
import rabbitescape.engine.config.IConfigStorage;
import rabbitescape.engine.config.IConfigUpgrade;
import rabbitescape.engine.menu.ByNumberConfigBasedLevelsCompleted;

public class AndroidConfigUpgradeTo1 implements IConfigUpgrade
{
    @Override
    public void run( IConfigStorage storage )
    {
        if ( storage instanceof IAndroidConfigStorage )
        {
            runAndroid( (IAndroidConfigStorage)storage );
            storage.set( "config.version", "1" );
        }
        else
        {
            throw new RuntimeException(
                "Unexpected config storage type: " + storage.getClass().getSimpleName() );
        }
    }

    private void runAndroid( IAndroidConfigStorage storage )
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

        final String LEVELS_COMPLETED = "levels.completed.";

        SharedPreferences prefs = storage.getPrefs();
        Map<String, Integer> newMap = new HashMap<String, Integer>();
        SharedPreferences.Editor editor = prefs.edit();
        boolean doneSomething = false;

        for ( Map.Entry<String, ?> entry : prefs.getAll().entrySet() )
        {
            if ( entry.getKey().startsWith( LEVELS_COMPLETED ) )
            {
                newMap.put(
                    ByNumberConfigBasedLevelsCompleted.stripNumber_(
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
            editor.commit();
        }
    }
}

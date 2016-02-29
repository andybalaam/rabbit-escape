package rabbitescape.ui.android;

import android.app.Activity;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.Set;

import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.IConfigStorage;

public class AndroidConfigStorage implements IAndroidConfigStorage
{
    private final SharedPreferences prefs;

    public AndroidConfigStorage( Activity activity )
    {
        prefs = activity.getSharedPreferences( "rabbitescape", activity.MODE_PRIVATE );
    }

    @Override
    public void set( String key, String value )
    {
        prefs.edit().putString( key, value ).commit();
    }

    @Override
    public String get( String key )
    {
        return prefs.getString( key, null );
    }

    @Override
    public void save( Config config )
    {
    }

    @Override
    public SharedPreferences getPrefs()
    {
        return prefs;
    }
}

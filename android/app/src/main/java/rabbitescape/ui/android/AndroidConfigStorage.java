package rabbitescape.ui.android;

import android.content.SharedPreferences;

import rabbitescape.engine.config.Config;

public class AndroidConfigStorage implements IAndroidConfigStorage
{
    private final SharedPreferences prefs;

    public AndroidConfigStorage( SharedPreferences prefs )
    {
        this.prefs = prefs;
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

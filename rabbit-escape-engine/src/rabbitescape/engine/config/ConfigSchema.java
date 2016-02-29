package rabbitescape.engine.config;

import java.util.HashMap;
import java.util.Map;

public class ConfigSchema
{
    public final Map<String, String> defaults = new HashMap<>();

    private final Map<String, String> descriptions = new HashMap<>();

    public void set( String key, String defaultValue, String description )
    {
        defaults.put( key, defaultValue );
        descriptions.put( key, description );
    }

    public String getDefault( String key )
    {
        String ret = defaults.get( key );
        if ( ret == null )
        {
            throw new Config.UnknownKey( key );
        }
        return ret;
    }

    public void checkKeyExists( String key )
    {
        getDefault( key );
    }

    public String getDescription( String key )
    {
        String ret = descriptions.get( key );
        if ( ret == null )
        {
            throw new Config.UnknownKey( key );
        }
        return ret;
    }
}
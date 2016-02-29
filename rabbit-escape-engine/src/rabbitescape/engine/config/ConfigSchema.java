package rabbitescape.engine.config;

import java.util.HashMap;
import java.util.Map;

import rabbitescape.engine.err.RabbitEscapeException;

public class ConfigSchema
{
    public class KeyNotAllowed extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;

        public final String key;

        public KeyNotAllowed( String key )
        {
            this.key = key;
        }
    }

    // ---

    public final Map<String, String> defaults = new HashMap<>();

    private final Map<String, String> descriptions = new HashMap<>();

    public void set( String key, String defaultValue, String description )
    {
        if ( key.equals( "config.version" ) )
        {
            throw new KeyNotAllowed( "config.version" );
        }

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
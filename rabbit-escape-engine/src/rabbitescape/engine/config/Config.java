package rabbitescape.engine.config;

import java.util.SortedSet;
import java.util.TreeSet;

import rabbitescape.engine.err.RabbitEscapeException;

/**
 * Holds and retrieves configuration information that is saved in some
 * underlying storage.
 *
 * Handles a schema of which keys are allowed, and automatically upgrades
 * old config from previous versions.
 */
public class Config
{
    public static class UnknownKey extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;

        public final String key;

        public UnknownKey( String key )
        {
            this.key = key;
        }
    }

    // ---

    public final ConfigSchema schema;
    private final IConfigStorage storage;

    public Config( ConfigSchema schema, IConfigStorage storage )
    {
        this.schema = schema;
        this.storage = storage;
    }

    public void set( String key, String value )
    {
        schema.checkKeyExists( key ); // Check the key exists
        if ( ! get( key ).equals( value ) )
        {
            storage.set( key, value );
        }
    }

    public String get( String key )
    {
        String ret = storage.get( key );

        if ( ret == null )
        {
            return schema.getDefault( key );
        }
        else
        {
            return ret;
        }
    }

    public void save()
    {
        storage.save( this );
    }

    public SortedSet<String> keys()
    {
        return new TreeSet<String>( schema.defaults.keySet() );
    }
}

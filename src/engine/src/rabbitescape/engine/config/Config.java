package rabbitescape.engine.config;

import java.util.SortedSet;
import java.util.TreeSet;

import rabbitescape.engine.err.RabbitEscapeException;
import rabbitescape.engine.util.Util;

/**
 * Holds and retrieves configuration information that is saved in some
 * underlying storage.
 *
 * Handles a schema of which keys are allowed, and automatically upgrades
 * old config from previous versions.
 */
public class Config
{
    public static String CFG_VERSION = "config.version";

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

    public Config(
        ConfigSchema schema,
        IConfigStorage storage,
        IConfigUpgrade... upgrades
    )
    {
        this.schema = schema;
        this.storage = storage;

        for( int i = version(); i < upgrades.length; ++i )
        {
            upgrades[i].run( storage );
            Util.reAssert(
                version() == i + 1,
                "Config upgrade to version " + ( i + 1 )
                + " did not update the version correctly - version is: "
                + version()
            );
            storage.save( this );
        }
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

    public int version()
    {
        String ret = storage.get( Config.CFG_VERSION );

        if ( ret == null )
        {
            return 0;
        }
        else
        {
            return Integer.parseInt( ret );
        }
    }
}

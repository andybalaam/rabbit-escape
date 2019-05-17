package rabbitescape.engine.config;

import static rabbitescape.engine.util.Util.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;

import rabbitescape.engine.err.RabbitEscapeException;
import rabbitescape.engine.util.FileSystem;

/**
 * Don't use this directly - wrap it in a Config object.
 *
 * Stores and retrieves config values to/from a file.
 */
public class ConfigFile implements IConfigStorage
{
    public static class UnableToLoad extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;

        public final String filePath;

        public UnableToLoad( String filePath, Exception cause )
        {
            super( cause );
            this.filePath = filePath;
        }
    }

    public static class UnableToSave extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;

        public final String filePath;

        public UnableToSave( String filePath, Exception cause )
        {
            super( cause );
            this.filePath = filePath;
        }
    }

    private final Map<String, String> values;
    private final FileSystem fs;
    private final String filePath;

    public ConfigFile( FileSystem fs, String filePath )
    {
        this.fs = fs;
        this.filePath = filePath;
        this.values = new HashMap<>();
        load();
    }

    private void load()
    {
        if ( fs == null || filePath == null || !fs.exists( filePath ) )
        {
            return;
        }

        Properties props = new Properties();
        try
        {
            props.load(
                new ByteArrayInputStream( fs.read( filePath ).getBytes() ) );
        }
        catch ( Exception e )
        {
            throw new UnableToLoad( filePath, e );
        }

        for ( String name : stringPropertyNames( props ) )
        {
            set( name, props.getProperty( name ) );
        }
    }

    @Override
    public void set( String key, String value )
    {
        values.put( key, value );
    }

    @Override
    public String get( String key )
    {
        return values.get( key );
    }

    @Override
    public void save( Config config )
    {
        try
        {
            fs.mkdirs( fs.parent( filePath ) );
            fs.write( filePath, saveToString( config ) );
        }
        catch ( IOException e )
        {
            throw new UnableToSave( filePath, e );
        }
    }

    private String saveToString( Config config )
    {
        StringBuilder ret = new StringBuilder();

        // Tree set for sorting
        SortedSet<String> keys = config.keys();

        boolean first = true;
        for ( String key : keys )
        {
            if ( first )
            {
                first = false;
            }
            else
            {
                ret.append( "\n" );
            }

            ret.append( "# " );
            ret.append( config.schema.getDescription( key ) );
            ret.append( "\n" );
            if ( ! values.containsKey( key ) )
            {
                ret.append( '#' );
            }
            ret.append( propertyLine( config, key ) );
            ret.append( "\n" );
        }

        return ret.toString();
    }

    private String propertyLine( Config config, String key )
    {
        Properties props = new Properties();
        props.setProperty( key, config.get( key ) );
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try
        {
            props.store( bytes, null );
        }
        catch ( IOException e )
        {
            // Should never happen
            throw new RuntimeException( e );
        }

        try
        {
            return bytes.toString("UTF-8").toString().split( "\n" )[1];
        }
        catch ( UnsupportedEncodingException e )
        {
            // Should not happen since everyone knows UTF-8
            throw new RuntimeException(e);
        }
    }
}

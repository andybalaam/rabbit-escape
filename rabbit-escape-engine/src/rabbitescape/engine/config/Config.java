package rabbitescape.engine.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;

import rabbitescape.engine.err.RabbitEscapeException;
import rabbitescape.engine.util.FileSystem;

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

    public static class Definition
    {
        private final Map<String, String> defaults =
            new HashMap<String, String>();

        private final Map<String, String> descriptions =
            new HashMap<String, String>();

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
                throw new UnknownKey( key );
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
                throw new UnknownKey( key );
            }
            return ret;
        }
    }

    private final Definition definition;
    private final Map<String, String> values;
    private final FileSystem fs;
    private final String filePath;

    public Config( Definition definition, FileSystem fs, String filePath )
    {
        this.definition = definition;
        this.fs = fs;
        this.filePath = filePath;
        this.values = new HashMap<String, String>();
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

        for ( String name : props.stringPropertyNames() )
        {
            set( name, props.getProperty( name ) );
        }
    }

    public void set( String key, String value )
    {
        definition.checkKeyExists( key ); // Check the key exists
        values.put( key, value );
    }

    public String get( String key )
    {
        String ret = values.get( key );

        if ( ret == null )
        {
            return definition.getDefault( key );
        }
        else
        {
            return ret;
        }
    }

    public void save()
    {
        try
        {
            fs.mkdirs( fs.parent( filePath ) );
            fs.write( filePath, toString() );
        }
        catch ( IOException e )
        {
            throw new UnableToSave( filePath, e );
        }
    }

    @Override
    public String toString()
    {
        StringBuilder ret = new StringBuilder();

        // Tree set for sorting
        TreeSet<String> keys = new TreeSet<String>(
            definition.defaults.keySet() );

        boolean first = true;
        for ( String key: keys )
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
            ret.append( definition.getDescription( key ) );
            ret.append( "\n" );
            if ( ! values.containsKey( key ) )
            {
                ret.append( '#' );
            }
            ret.append( propertyLine( key ) );
            ret.append( "\n" );
        }

        return ret.toString();
    }

    private String propertyLine( String key )
    {
        Properties props = new Properties();
        props.setProperty( key, get( key ) );
        StringWriter writer = new StringWriter();
        try
        {
            props.store( writer, null );
        }
        catch ( IOException e )
        {
            // Should never happen
            throw new RuntimeException( e );
        }

        return writer.toString().split( "\n" )[1];
    }
}

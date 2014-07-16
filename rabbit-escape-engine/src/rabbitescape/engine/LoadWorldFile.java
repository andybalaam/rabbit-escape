package rabbitescape.engine;

import java.io.FileNotFoundException;
import java.io.IOException;

import rabbitescape.engine.err.RabbitEscapeException;
import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.engine.util.FileSystem;

public class LoadWorldFile
{
    public static class Failed extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;

        public final String fileName;

        public Failed( String fileName, RabbitEscapeException cause )
        {
            super( cause );
            this.fileName = fileName;
        }
    }

    public static class MissingFile extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;

        public final String fileName;

        public MissingFile( String fileName )
        {
            this.fileName = fileName;
        }
    }

    public static class ReadingFailed extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;

        public final String fileName;
        public final String iocause;

        public ReadingFailed( String fileName, IOException iocause )
        {
            super( iocause );
            this.fileName = fileName;
            this.iocause = iocause.getMessage();
        }
    }

    private final FileSystem fs;

    public LoadWorldFile( FileSystem fs )
    {
        this.fs = fs;
    }

    public World load( String fileName )
    {
        try
        {
            return TextWorldManip.createWorld( loadLines( fileName ) );
        }
        catch( RabbitEscapeException e )
        {
            throw new Failed( fileName, e );
        }
    }

    private String[] loadLines( String fileName )
    {
        if( !fs.exists( fileName ) )
        {
            throw new MissingFile( fileName );
        }

        try
        {
            return fs.readLines( fileName );
        }
        catch( FileNotFoundException e )
        {
            throw new MissingFile( fileName );
        }
        catch( IOException e )
        {
            throw new ReadingFailed( fileName, e );
        }
    }
}

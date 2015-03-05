package rabbitescape.engine;

import java.io.FileNotFoundException;
import java.io.IOException;

import static rabbitescape.engine.util.Util.*;

import rabbitescape.engine.err.RabbitEscapeException;
import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.engine.util.FileSystem;
import rabbitescape.engine.util.Util.MissingResource;

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

        public MissingFile( Throwable cause, String fileName )
        {
            super( cause );
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

    public World load( WorldStatsListener statsListener, String fileName )
    {
        try
        {
            return TextWorldManip.createWorldWithName(
                levelName( fileName ), statsListener, loadLines( fileName ) );
        }
        catch( RabbitEscapeException e )
        {
            throw new Failed( fileName, e );
        }
    }

    public static String levelName( String fileName )
    {
        return fileName
            .replaceAll( "\\.rel$", "" )
            .replaceAll( "[/_]", " " )
            .replaceAll( "0+(\\d+)", "$1" );
    }

    private String[] loadLines( String fileName )
    {
        if( !fs.exists( fileName ) )
        {
            return readLinesFromResource( fileName );
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

    public static String[] readLinesFromResource( String fileName )
    {
        try
        {
            return stringArray(
                resourceLines( "/rabbitescape/levels/" + fileName ) );
        }
        catch ( MissingResource e )
        {
            throw new MissingFile( e, fileName );
        }
    }
}

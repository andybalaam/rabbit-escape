package rabbitescape.render;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import rabbitescape.engine.err.RabbitEscapeException;

public class AnimationLoader
{
    public static final String NONE = "<none>";

    public static class AnimationNotFound extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;

        public final String name;

        public AnimationNotFound( String name )
        {
            this.name = name;
        }
    }

    public static class BadAnimationLine extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;

        public final String line;

        public BadAnimationLine( String line )
        {
            this.line = line;
        }

        public BadAnimationLine( String line, Throwable cause )
        {
            super( cause );
            this.line = line;
        }
    }

    public static class ErrorLoadingAnimationNames extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;

        public ErrorLoadingAnimationNames( Throwable cause )
        {
            super( cause );
        }
    }

    public String[] listAll()
    {
        try
        {
            List<String> ret = new ArrayList<String>();
            ret.add( NONE );

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                    getClass().getResource(
                        "/rabbitescape/render/animations/ls.txt"
                    ).openStream()
                )
            );

            String line;
            while ( ( line = reader.readLine() ) != null )
            {
                if ( line.endsWith( ".rea" ) )
                {
                    ret.add( line.substring( 0, line.length() - 4 ) );
                }
            }

            return ret.toArray( new String[ret.size() ] );
        }
        catch ( IOException e )
        {
            throw new ErrorLoadingAnimationNames( e );
        }
    }

    public FrameNameAndOffset[] load( String name )
    {
        try
        {
            String key = "/rabbitescape/render/animations/" + name + ".rea";

            URL url = AnimationLoader.class.getResource( key );
            if ( url == null )
            {
                throw new AnimationNotFound( name );
            }
            InputStream stream = url.openStream();

            return readAnimation( stream );
        }
        catch ( IOException e )
        {
            throw new AnimationNotFound( name );
        }
    }

    public static FrameNameAndOffset[] readAnimation( InputStream stream )
        throws IOException
    {
        BufferedReader reader = new BufferedReader(
            new InputStreamReader( stream ) );

        List<FrameNameAndOffset> ret = new ArrayList<>();
        String ln;
        while ( ( ln = reader.readLine() ) != null )
        {
            String trimmedLn = ln.trim();
            if ( !trimmedLn.isEmpty() )
            {
                ret.add( frameNameAndOffset( trimmedLn ) );
            }
        }

        return ret.toArray( new FrameNameAndOffset[ ret.size() ] );
    }

    private static FrameNameAndOffset frameNameAndOffset( String animLine )
    {
        String[] parts = animLine.split( " " );

        try
        {
            switch ( parts.length )
            {
                case 1:
                {
                    return new FrameNameAndOffset( parts[0] );
                }
                case 2:
                {
                    return new FrameNameAndOffset(
                        parts[0], Integer.valueOf( parts[1] ) );
                }
                case 3:
                {
                    return new FrameNameAndOffset(
                        parts[0],
                        Integer.valueOf( parts[1] ),
                        Integer.valueOf( parts[2] )
                    );
                }
                default:
                {
                    throw new BadAnimationLine( animLine );
                }
            }
        }
        catch ( NumberFormatException e )
        {
            throw new BadAnimationLine( animLine, e );
        }
    }
}

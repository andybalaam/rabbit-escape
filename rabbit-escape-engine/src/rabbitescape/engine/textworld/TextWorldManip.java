package rabbitescape.engine.textworld;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rabbitescape.engine.Block;
import rabbitescape.engine.ChangeDescription;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.Thing;
import rabbitescape.engine.World;

public class TextWorldManip
{
    private static final String num_rabbits  = "num_rabbits";
    private static final String rabbit_delay = "rabbit_delay";
    private static final String name         = "name";

    public static final List<String> META_INTS =
        Arrays.asList( new String[] {
            num_rabbits,
            rabbit_delay
        } );

    public static final List<String> META_STRINGS =
        Arrays.asList( new String[] {
            name
        } );

    public static World createWorld( String... lines )
        throws WrongLineLength, UnknownCharacter
    {
        List<Block> blocks = new ArrayList<Block>();
        List<Rabbit> rabbits = new ArrayList<Rabbit>();
        List<Thing> things = new ArrayList<Thing>();

        LineProcessor processor = new LineProcessor(
            blocks, rabbits, things, lines );

        return new World(
            processor.size(),
            blocks,
            rabbits,
            things,
            processor.metaString( "name",        "Untitled" ),
            processor.metaInt(    "num_rabbits",  10 ),
            processor.metaInt(    "rabbit_delay", 4 )
        );
    }

    public static World createEmptyWorld( int width, int height )
    {
        return new World(
            new Dimension( width, height ),
            new ArrayList<Block>(),
            new ArrayList<Rabbit>(),
            new ArrayList<Thing>(),
            "Empty World",
            0,
            1
        );
    }

    public static String[] renderWorld( World world, boolean showChanges )
    {
        char[][] chars = emptyWorldChars( world );

        BlockRenderer.render( chars, world.blocks );
        RabbitRenderer.render( chars, world.rabbits );
        ThingRenderer.render( chars, world.things );

        if ( showChanges )
        {
            ChangeRenderer.render( chars, world.describeChanges() );
        }

        return charsToStrings( chars );
    }

    public static String[] renderChangeDescription(
        World world, ChangeDescription desc )
    {
        char[][] chars = emptyWorldChars( world );

        ChangeRenderer.render( chars, desc );

        return charsToStrings( chars );
    }

    private static String[] charsToStrings( char[][] chars )
    {
        String[] ret = new String[chars.length];

        for ( int lineNum = 0; lineNum < chars.length; ++lineNum )
        {
            ret[lineNum] = new String( chars[lineNum] );
        }

        return ret;
    }

    private static char[][] emptyWorldChars( World world )
    {
        return filledChars(
            world.size.height, world.size.width, ' ' );
    }

    private static char[][] filledChars( int height, int width, char c )
    {
        char[][] ret = new char[height][width];

        for( int i = 0; i < height; ++i )
        {
            Arrays.fill( ret[i], c );
        }

        return ret;
    }
}

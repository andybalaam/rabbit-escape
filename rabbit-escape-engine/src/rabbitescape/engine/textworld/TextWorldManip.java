package rabbitescape.engine.textworld;

import java.util.*;

import rabbitescape.engine.*;
import rabbitescape.engine.util.Dimension;

public class TextWorldManip
{
    private static final String num_rabbits  = "num_rabbits";
    private static final String rabbit_delay = "rabbit_delay";
    private static final String name         = "name";

    public static final List<String> META_INTS = Arrays.asList(
        num_rabbits,
        rabbit_delay
    );

    public static final List<String> META_STRINGS = Arrays.asList(
        name
    );

    public static final List<String> ABILITIES = abilitiesList();

    private static List<String> abilitiesList()
    {
        List<String> ret = new ArrayList<>();
        for ( Token.Type type : Token.Type.values() )
        {
            ret.add( type.name() );
        }
        return ret;
    }

    public static World createWorld( String... lines )
        throws WrongLineLength, UnknownCharacter
    {
        List<Block> blocks = new ArrayList<>();
        List<Rabbit> rabbits = new ArrayList<>();
        List<Thing> things = new ArrayList<>();
        Map<Token.Type, Integer> abilities = new HashMap<>();

        LineProcessor processor = new LineProcessor(
            blocks, rabbits, things, abilities, lines );

        return new World(
            processor.size(),
            blocks,
            rabbits,
            things,
            abilities,
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
            new HashMap<Token.Type, Integer>(),
            "Empty World",
            0,
            1
        );
    }

    public static String[] renderWorld(
        World world, boolean showChanges, boolean coordinates )
    {
        Chars chars = new Chars( world );

        BlockRenderer.render( chars, world.blocks );
        RabbitRenderer.render( chars, world.rabbits );
        ThingRenderer.render( chars, world.things );

        if ( showChanges )
        {
            ChangeRenderer.render( chars, world.describeChanges() );
        }

        return charsToStrings( chars, coordinates );
    }

    public static String[] renderChangeDescription(
        World world, ChangeDescription desc, boolean coordinates )
    {
        Chars chars = new Chars( world );

        ChangeRenderer.render( chars, desc );

        return charsToStrings( chars, coordinates );
    }

    private static String[] charsToStrings(
        Chars chars, boolean coordinates )
    {
        int len = chars.numRows();

        if ( coordinates )
        {
            len += 2;
        }

        String[] ret = new String[len];

        for ( int lineNum = 0; lineNum < chars.numRows(); ++lineNum )
        {
            String ans = new String( chars.line( lineNum ) );

            if ( coordinates)
            {
                ans = formatRowNum( lineNum ) + " " + ans;
            }
            ret[lineNum] = ans;
        }

        if ( coordinates )
        {
            addColumnCoords( ret, chars.numCols(), chars.numRows() );
        }

        return ret;
    }

    private static void addColumnCoords( String[] ret, int width, int height )
    {
        StringBuilder tensRow = new StringBuilder();
        StringBuilder unitsRow = new StringBuilder();

        tensRow.append( "   " );
        unitsRow.append( "   " );
        for ( int i = 0; i < width; ++i )
        {
            tensRow.append( ( i / 10 ) % 10 );
            unitsRow.append( i % 10 );
        }

        ret[height]     = tensRow.toString();
        ret[height + 1] = unitsRow.toString();
    }

    private static String formatRowNum( int lineNum )
    {
        return String.format( "%02d", lineNum ).substring( 0, 2 );
    }
}

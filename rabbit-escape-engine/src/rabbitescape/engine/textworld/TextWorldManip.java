package rabbitescape.engine.textworld;

import java.util.*;

import static rabbitescape.engine.util.Util.*;

import rabbitescape.engine.*;
import rabbitescape.engine.util.Dimension;
import rabbitescape.engine.util.VariantGenerator;

public class TextWorldManip
{
    private static final String name                 = "name";
    private static final String description          = "description";
    private static final String num_rabbits          = "num_rabbits";
    private static final String num_to_save          = "num_to_save";
    private static final String rabbit_delay         = "rabbit_delay";
    private static final String music                = "music";
    private static final String num_saved            = "num_saved";
    private static final String num_killed           = "num_killed";
    private static final String num_waiting          = "num_waiting";
    private static final String intro                = "intro";
    private static final String paused               = "paused";
    private static final String ready_to_explode_all = "ready_to_explode_all";

    public static final List<String> META_INTS = Arrays.asList(
        num_rabbits,
        num_to_save,
        rabbit_delay,
        num_saved,
        num_killed,
        num_waiting
    );

    public static final List<String> META_STRINGS = Arrays.asList(
        name,
        description,
        music
    );

    public static final List<String> META_BOOLS = Arrays.asList(
        intro,               // Deprecated - leave for backward compatibility
        paused,
        ready_to_explode_all // Deprecated - leave for backward compatibility
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
        return createWorld( new IgnoreWorldStatsListener(), lines );
    }

    public static World createWorld(
        WorldStatsListener statsListener, String... lines )
        throws WrongLineLength, UnknownCharacter
    {
        return createWorldWithName( "", statsListener, lines );
    }

    public static World createWorldWithName(
        String nameIfNoneSupplied,
        WorldStatsListener statsListener,
        String... lines
    )
    {
        List<Block> blocks = new ArrayList<>();
        List<Rabbit> rabbits = new ArrayList<>();
        List<Thing> things = new ArrayList<>();
        Map<Token.Type, Integer> abilities = new HashMap<>();

        int variantSeed = 0; // TODO: world property for the seed?

        LineProcessor processor = new LineProcessor(
            blocks,
            rabbits,
            things,
            abilities,
            lines,
            new VariantGenerator( variantSeed )
        );

        int num_rabs = processor.metaInt( num_rabbits,  10 );

        return new World(
            processor.size(),
            blocks,
            rabbits,
            things,
            abilities,
            processor.metaString( name, nameIfNoneSupplied ),
            processor.metaString( description, "" ),
            num_rabs,
            processor.metaInt( num_to_save,  1 ),
            processor.metaInt( rabbit_delay, 4 ),
            processor.metaString( music, null ),
            processor.metaInt( num_saved, 0 ),
            processor.metaInt( num_killed, 0 ),
            processor.metaInt( num_waiting, num_rabs ),
            processor.metaBool( paused, false ),
            statsListener
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
            "",
            0,
            1,
            4,
            null,
            0,
            0,
            0,
            false,
            new IgnoreWorldStatsListener()
        );
    }

    public static String[] renderWorld(
        World world, boolean showChanges, boolean coordinates )
    {
        Chars chars = new Chars( world, false );

        BlockRenderer.render( chars, world.blocks );
        RabbitRenderer.render( chars, world.rabbits );
        ThingRenderer.render( chars, world.things );

        if ( showChanges )
        {
            ChangeRenderer.render( chars, world.describeChanges() );
        }

        return charsToStrings( chars, coordinates );
    }

    public static String[] renderCompleteWorld( World world, boolean meta )
    {
        Chars chars = new Chars( world, true );

        BlockRenderer.render( chars, world.blocks );
        RabbitRenderer.render( chars, world.rabbits );
        ThingRenderer.render( chars, world.things );

        String[] things = charsToComplete( chars );

        if ( meta )
        {
            return concat( metaLines( world ), things );
        }
        else
        {
            return things;
        }
    }

    private static String[] metaLines( World world )
    {
        List<String> ret = new ArrayList<String>();

        ret.add( metaLine( name,         world.name ) );
        ret.add( metaLine( description,  world.description ) );
        ret.add( metaLine( num_rabbits,  world.num_rabbits ) );
        ret.add( metaLine( num_to_save,  world.num_to_save ) );
        ret.add( metaLine( rabbit_delay, world.rabbit_delay ) );
        ret.add( metaLine( num_saved,    world.num_saved ) );
        ret.add( metaLine( num_killed,   world.num_killed ) );
        ret.add( metaLine( num_waiting,  world.num_waiting ) );
        ret.add( metaLine( paused,       world.paused ) );

        abilityMetaLines( world, ret );

        return ret.toArray( new String[ret.size()] );
    }

    private static void abilityMetaLines( World world, List<String> ret )
    {
        List<Token.Type> abilityNames = new ArrayList<Token.Type>(
            world.abilities.keySet() );

        Comparator<Token.Type> alphabetical = new Comparator<Token.Type>()
        {
            @Override
            public int compare( Token.Type o1, Token.Type o2 )
            {
                return o1.name().compareTo( o2.name() );
            }
        };
        Collections.sort( abilityNames, alphabetical );

        for ( Token.Type t : abilityNames )
        {
            ret.add( metaLine( t.name(), world.abilities.get( t ) ) );
        }
    }

    private static String metaLine( String name, int value )
    {
        return metaLine( name, Integer.toString( value ) );
    }

    private static String metaLine( String name, String value )
    {
        return ":" + name + "=" + value;
    }

    private static String metaLine( String name, boolean value )
    {
        return ":" + name + "=" + Boolean.toString( value );
    }

    public static String[] renderChangeDescription(
        World world, ChangeDescription desc, boolean coordinates )
    {
        Chars chars = new Chars( world, false );

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

            if ( coordinates )
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

    private static String[] charsToComplete( Chars chars )
    {
        List<String> ret = new ArrayList<String>();

        for ( int lineNum = 0; lineNum < chars.numRows(); ++lineNum )
        {
            ret.add( new String( chars.line( lineNum ) ) );
        }

        for ( String starLine : chars.starLines() )
        {
            ret.add( ":*=" + starLine );
        }

        return ret.toArray( new String[ ret.size() ] );
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

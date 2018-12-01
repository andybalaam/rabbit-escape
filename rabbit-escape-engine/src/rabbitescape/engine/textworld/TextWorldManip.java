package rabbitescape.engine.textworld;

import static rabbitescape.engine.util.Util.concat;
import static rabbitescape.engine.util.Util.enumerate1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rabbitescape.engine.Block;
import rabbitescape.engine.ChangeDescription;
import rabbitescape.engine.IgnoreWorldStatsListener;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.Thing;
import rabbitescape.engine.Token;
import rabbitescape.engine.VoidMarkerStyle;
import rabbitescape.engine.World;
import rabbitescape.engine.WorldStatsListener;
import rabbitescape.engine.util.Dimension;
import rabbitescape.engine.util.Position;
import rabbitescape.engine.util.Util;
import rabbitescape.engine.util.Util.Function;
import rabbitescape.engine.util.Util.IdxObj;
import rabbitescape.engine.util.VariantGenerator;

public class TextWorldManip
{
    public  static final String name                 = "name";
    private static final String description          = "description";
    private static final String author_name          = "author_name";
    private static final String author_url           = "author_url";
    public  static final String hint                 = "hint";
    public  static final String solution             = "solution";
    private static final String num_rabbits          = "num_rabbits";
    private static final String num_to_save          = "num_to_save";
    private static final String rabbit_delay         = "rabbit_delay";
    private static final String music                = "music";
    public  static final String void_marker_style    = "void_marker_style";
    private static final String num_saved            = "num_saved";
    private static final String num_killed           = "num_killed";
    private static final String num_waiting          = "num_waiting";
    private static final String rabbit_index_count   = "rabbit_index_count";
    private static final String intro                = "intro";
    private static final String paused               = "paused";
    private static final String ready_to_explode_all = "ready_to_explode_all";
    public  static final String water_definition     = "n";

    public static final List<String> META_INTS = Arrays.asList(
        num_rabbits,
        num_to_save,
        num_saved,
        num_killed,
        num_waiting,
        rabbit_index_count
    );

    public static final List<String> META_INT_ARRAYS = Arrays.asList(
        rabbit_delay
    );

    public static final List<String> META_STRINGS = Arrays.asList(
        name,
        description,
        author_name,
        author_url,
        music,
        void_marker_style
    );

    public static final List<String> META_STRING_ARRAYS_BY_KEY = Arrays.asList(
        solution,
        hint
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
        WorldStatsListener statsListener, String... lines 
    ) throws WrongLineLength, UnknownCharacter
    {
        return createWorldWithName( "", statsListener, lines );
    }

    /**
     * @param encode if true, create a world with obfuscated hints and solutions
     */
    public static World createWorldWithName(
        String nameIfNoneSupplied,
        WorldStatsListener statsListener,
        String... lines
    )
    {
        List<Block> blocks = new ArrayList<>();
        List<Rabbit> rabbits = new ArrayList<>();
        List<Thing> things = new ArrayList<>();
        Map<Position, Integer> waterAmounts = new HashMap<>();
        Map<Token.Type, Integer> abilities = new HashMap<>();

        int variantSeed = 0; // TODO: world property for the seed?

        LineProcessor processor = new LineProcessor(
            blocks,
            rabbits,
            things,
            waterAmounts,
            abilities,
            lines,
            new VariantGenerator( variantSeed )
        );

        int num_rabs = processor.metaInt( num_rabbits, 10 );

        World world = createWorldFromLineProcessor(
            nameIfNoneSupplied, 
            statsListener, 
            blocks, 
            rabbits, 
            things,
            waterAmounts, 
            abilities, 
            processor, 
            num_rabs 
        );

        world.countRabbitsForIndex();

        return world;
    }

    private static World createWorldFromLineProcessor(
        String nameIfNoneSupplied,
        WorldStatsListener statsListener,
        List<Block> blocks,
        List<Rabbit> rabbits,
        List<Thing> things,
        Map<Position, Integer> waterAmounts,
        Map<Token.Type, Integer> abilities,
        LineProcessor processor,
        int num_rabs 
    )
    {

        return new World(
            processor.size(),
            blocks,
            rabbits,
            things,
            waterAmounts,
            abilities,
            processor.metaString( name, nameIfNoneSupplied ),
            processor.metaString( description, "" ),
            processor.metaString( author_name, "" ),
            processor.metaString( author_url, "" ),
            processor.metaStringArrayByKey( hint, new String[] {} ),
            processor.metaStringArrayByKey( solution, new String[] {} ),
            num_rabs,
            processor.metaInt( num_to_save,  1 ),
            processor.metaIntArray( rabbit_delay, new int[]{4} ),
            processor.metaString( music, "" ),
            processor.metaInt( num_saved, 0 ),
            processor.metaInt( num_killed, 0 ),
            processor.metaInt( num_waiting, num_rabs ),
            processor.metaInt( rabbit_index_count, 0 ),
            processor.metaBool( paused, false ),
            processor.getComments(),
            statsListener,
            processor.generateVoidMarkerStyle()
        );
    }

    public static World createEmptyWorld( int width, int height )
    {
        return new World(
            new Dimension( width, height ),
            new ArrayList<Block>(),
            new ArrayList<Rabbit>(),
            new ArrayList<Thing>(),
            new HashMap<Position, Integer>(),
            new HashMap<Token.Type, Integer>(),
            "Empty World",   //name
            "",              //description
            "",              //author_name
            "",              //author_url
            new String[] {}, //hints
            new String[] {}, //solutions
            0,               //num_rabs
            1,               //num_to_save
            new int[]{4},    //rabbit_delay
            "",              //music
            0,               //num_saved
            0,               //num_killed
            0,               //num_waiting
            0,               //rabbit_index_count
            false,
            new Comment[] {},
            new IgnoreWorldStatsListener(),
            VoidMarkerStyle.Style.HIGHLIGHTER
        );
    }

    public static String[] renderWorld(
        World world, boolean showChanges, boolean coordinates )
    {
        // Call renderCompleteWorld if you want runtime info
        boolean runtimeMeta = false;

        Chars chars = new Chars( world, false );

        BlockRenderer.render( chars, world.blockTable );
        RabbitRenderer.render( chars, world.rabbits, runtimeMeta );
        ThingRenderer.render( chars, world.things, runtimeMeta );

        if ( showChanges )
        {
            ChangeRenderer.render( chars, world.describeChanges() );
        }

        return charsToStrings( chars, coordinates );
    }

    public static String[] renderCompleteWorld( World world, boolean meta )
    {
        return renderCompleteWorld( world, meta, true );
    }

    /**
     * @param world        The World to render.
     * @param meta         If true the metadata is included.
     * @param runtimeMeta  If true the runtime metadata is included.
     * @return             The lines as an array.
     */
    public static String[] renderCompleteWorld(
        World world, boolean meta, boolean runtimeMeta )
    {
        Chars chars = new Chars( world, true );

        BlockRenderer.render( chars, world.blockTable );
        WaterRenderer.render( chars, world.waterTable );
        RabbitRenderer.render( chars, world.rabbits, runtimeMeta );
        ThingRenderer.render( chars, world.things, runtimeMeta );

        String[] things = charsToComplete( chars, world.comments );

        if ( meta )
        {
            List<String> worldComments = new ArrayList<String>();
            addMeta( 
                worldComments, 
                Comment.WORLD_ASCII_ART,
                null, 
                world.comments
            );
            List<String> endComments = new ArrayList<String>();
            // Comments after all substantive meta.
            addMeta( endComments, null, null, world.comments);
            return concat( 
                metaLines( world, runtimeMeta ),
                worldComments.toArray( new String[]{} ),
                things,
                endComments.toArray( new String[]{}) 
            );
        }
        else
        {
            return things;
        }
    }

    private static Function<String, String> escapeBackslashes =
        new Function<String, String>()
    {
        @Override
        public String apply( String t )
        {
            return t.replaceAll( "\\\\", "\\\\\\\\" );
        }
    };

    public static String renderWorldForTest( World world )
    {
        String[] lines = Util.stringArray( Util.map(
            escapeBackslashes, 
            renderWorld( world, true, false ) 
        ) );

        String glue = "\" + \"\\n\" +\n            \"";
        return "            \"" + Util.join( glue, lines ) + "\",\n";
    }

    /**
     * Adds a line of metadata to the supplied List. If there are any comments
     * for this key comment lines will be added first.
     * @param   value may be null, in which case only comments will
     *          be considered.
     */
    private static void addMeta( 
        List<String> lines,
        String key, 
        String value,
        Comment[] comments 
    )
    {
        for( Comment c: comments)
        {
            if( c.isFollowedBy( key ) )
            {
                lines.add( c.text );
            }
        }
        if ( null == value )
        { // We were only here to consider adding comments.
            return;
        }
        else
        {
            lines.add( ":" + key + "=" + value );
        }
    }

    private static String[] metaLines( World world, boolean runtimeMeta )
    {
        List<String> ret = new ArrayList<String>();

        addMeta( ret, name,        world.name,        world.comments );
        addMeta( ret, description, world.description, world.comments );
        addMeta( ret, author_name, world.author_name, world.comments );
        addMeta( ret, author_url,  world.author_url,  world.comments );

        addMetaKeyArrayLines( ret, hint, world.hints, world );
        addMetaKeyArrayLines( ret, solution, world.solutions, world );

        addMeta(
            ret, 
            num_rabbits,  
            Integer.toString( world.num_rabbits ),
            world.comments 
        );
        addMeta( 
            ret, 
            num_to_save,  
            Integer.toString( world.num_to_save ),
            world.comments 
        );
        addMeta( 
            ret, 
            rabbit_delay, 
            renderIntArray( world.rabbit_delay ),
            world.comments 
        );
        addMeta( ret, music, world.music, world.comments );

        if ( runtimeMeta )
        {
            addMeta( 
                ret, 
                num_saved,   
                Integer.toString( world.num_saved ),
                world.comments 
            );
            addMeta( 
                ret, 
                num_killed,  
                Integer.toString( world.num_killed ),
                world.comments 
            );
            addMeta( 
                ret, 
                num_waiting, 
                Integer.toString( world.num_waiting ),
                world.comments 
            );
            addMeta( 
                ret, 
                rabbit_index_count,
                Integer.toString( world.getRabbitIndexCount() ),
                world.comments 
            );
            addMeta( 
                ret, 
                paused,      
                Boolean.toString( world.paused ),
                world.comments 
            );
        }
        abilityMetaLines( world, ret );

        return ret.toArray( new String[ret.size()] );
    }

    private static void addMetaKeyArrayLines(
        List<String> ret, String name, String[] values, World w )
    {
        for ( IdxObj<String> value : enumerate1( values ) )
        {
            String keyWithIndex = name + "." + value.index;
            addMeta( ret, keyWithIndex, value.object, w.comments );
        }
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
            addMeta( ret, t.name(),
                     Integer.toString( world.abilities.get( t ) ),
                     world.comments );
        }
    }

    private static String renderIntArray( int[] value )
    {
        String ret = Integer.toString( value[0] );
        for ( int i = 1; i< value.length; i++)
        {
            ret = ret + "," + Integer.toString( value[i] ) ;
        }
        return ret;
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

    private static String[] charsToComplete( Chars chars, Comment[] comments )
    {
        List<String> ret = new ArrayList<String>();

        for ( int lineNum = 0; lineNum < chars.numRows(); ++lineNum )
        {
            ret.add( new String( chars.line( lineNum ) ) );
        }

        addMeta( ret, "*", null, comments );

        for ( String starLine : chars.starLines() )
        {
            ret.add( ":*=" + starLine );
        }

        for ( String waterAmountLine : chars.waterAmountLines() )
        {
            ret.add( ":n=" + waterAmountLine );
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

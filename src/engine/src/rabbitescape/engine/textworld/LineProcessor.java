package rabbitescape.engine.textworld;

import static rabbitescape.engine.Block.Material.EARTH;
import static rabbitescape.engine.Block.Material.METAL;
import static rabbitescape.engine.Block.Shape.BRIDGE_UP_LEFT;
import static rabbitescape.engine.Block.Shape.BRIDGE_UP_RIGHT;
import static rabbitescape.engine.Block.Shape.FLAT;
import static rabbitescape.engine.Block.Shape.UP_LEFT;
import static rabbitescape.engine.Block.Shape.UP_RIGHT;
import static rabbitescape.engine.Direction.LEFT;
import static rabbitescape.engine.Direction.RIGHT;
import static rabbitescape.engine.util.Util.asChars;
import static rabbitescape.engine.util.Util.split;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rabbitescape.engine.Block;
import rabbitescape.engine.Entrance;
import rabbitescape.engine.Exit;
import rabbitescape.engine.Fire;
import rabbitescape.engine.Pipe;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.Thing;
import rabbitescape.engine.Token;
import rabbitescape.engine.VoidMarkerStyle;
import rabbitescape.engine.util.Dimension;
import rabbitescape.engine.util.MegaCoder;
import rabbitescape.engine.util.Position;
import rabbitescape.engine.util.VariantGenerator;
import rabbitescape.engine.util.WaterUtil;

public class LineProcessor
{
    public static final String CODE_SUFFIX = ".code";

    public static class KeyListKey
    {
        public final String prefix;
        public final int number;

        public KeyListKey( String prefix, int number )
        {
            this.prefix = prefix;
            this.number = number;
        }

        @Override
        public String toString()
        {
            return "KeyListKey( " + prefix + ", " + number + " )";
        }

        @Override
        public int hashCode()
        {
            return number + 31 * ( number + prefix.hashCode() );
        }

        @Override
        public boolean equals( Object otherObj )
        {
            if ( ! ( otherObj instanceof KeyListKey ) )
            {
                return false;
            }
            KeyListKey other = (KeyListKey)otherObj;

            return (
                   number == other.number
                && prefix.equals( other.prefix )
            );
        }
    }

    static final Pattern keyListKeyRegex = Pattern.compile(
        "(.*)\\.(\\d{1,3})" );

    private final List<Block> blocks;
    private final List<Rabbit> rabbits;
    private final List<Thing> things;
    private final Map<Position, Integer> waterAmounts;
    private final Map<Token.Type, Integer> abilities;
    public  final String[] lines;
    private final Map<String, String>  m_metaStrings;
    private final Map<String, Map<Integer, String>> m_metaStringArraysByKey;
    private final Map<String, Integer> m_metaInts;
    private final Map<String, Boolean> m_metaBools;
    private final Map<String, ArrayList<Integer>> m_metaIntArrays;
    private final List<Position> starPoints;
    private final List<Comment> comments;

    private int width;
    private int height;
    public  int lineNum;
    private int currentStarPoint;

    public LineProcessor(
        List<Block> blocks,
        List<Rabbit> rabbits,
        List<Thing> things,
        Map<Position, Integer> waterAmounts,
        Map<Token.Type, Integer> abilities,
        String[] lines,
        VariantGenerator variantGen
    )
    {
        this.blocks = blocks;
        this.rabbits = rabbits;
        this.things = things;
        this.waterAmounts = waterAmounts;
        this.abilities = abilities;
        this.lines = lines;
        this.m_metaStrings           = new HashMap<>();
        this.m_metaStringArraysByKey = new HashMap<>();
        this.m_metaInts              = new HashMap<>();
        this.m_metaBools             = new HashMap<>();
        this.m_metaIntArrays         = new HashMap<>();
        starPoints = new ArrayList<Position>();
        this.comments = new ArrayList<Comment>();

        width = -1;
        height = 0;
        lineNum = 0;
        currentStarPoint = 0;

        process( variantGen );
    }

    public Comment[] getComments()
    {
        return comments.toArray( new Comment[comments.size()] );
    }

    public String metaString( String key, String def )
    {
        String ret = m_metaStrings.get( key );
        if ( ret == null )
        {
            return def;
        }
        else
        {
            return ret;
        }
    }

    public String[] metaStringArrayByKey( String key, String[] def )
    {
        Map<Integer, String> temp = m_metaStringArraysByKey.get( key );
        if ( temp == null )
        {
            return def;
        }
        else
        {

            String[] ret = new String[temp.size()];
            for ( int i = 1 ; i <= temp.size() ; i++ )
            {
                String v = temp.get( i );
                if ( null == v )
                {
                    throw new RuntimeException(
                        "temp should have 1, 2, ..., temp.size() members." );
                }
                ret[i - 1] = v;
            }
            return ret;
        }
    }

    public int metaInt( String key, int def )
    {
        Integer ret = m_metaInts.get( key );
        if ( ret == null )
        {
            return def;
        }
        else
        {
            return ret;
        }
    }

    public int[] metaIntArray( String key, int[] def )
    {
        ArrayList<Integer> temp = m_metaIntArrays.get( key );
        if ( temp == null )
        {
            return def;
        }
        int[] ret = new int[temp.size()];
        for ( int i = 0; i < temp.size(); i++ )
        {
            ret[i] = temp.get( i );
        }
        return ret;
    }

    public boolean metaBool( String key, boolean def )
    {
        Boolean ret = m_metaBools.get( key );
        if ( ret == null )
        {
            return def;
        }
        else
        {
            return ret;
        }
    }

    public Dimension size()
    {
        return new Dimension( width, height );
    }

    private void process( VariantGenerator variantGen )
    {
        for ( String line : lines )
        {
            if ( line.startsWith( ":" ) )
            {
                processMetaLine( line, variantGen );
            }
            else if ( line.startsWith( "%" ) )
            {
                processCommentLine( line );
            }
            else
            {
                processItemsLine( line, variantGen );
            }
            ++lineNum;
        }

        if ( starPoints.size() > currentStarPoint )
        {
            throw new TooManyStars( lines );
        }
    }

    private void duplicateMetaCheck( Set<String> set, String key )
    {
        if ( set.contains( key ) )
        {
            throw new DuplicateMetaKey( lines, lineNum );
        }
    }

    private void processCommentLine( String line )
    {
        // Create temporary comment, until we know the line following,
        // to create the association.
        Comment c = Comment.createUnlinkedComment( line );
        comments.add( c );
    }

    private void maybeLinkToLastComment( String key )
    {
        if ( comments.size() == 0 )
        {
            return; // No comments to link.
        }

        // Iterate backwards linking all comments in a block
        // until we hit one that it already linked with the
        // previous non-comment line
        for ( int i = comments.size() - 1; i >= 0 ; i--)
        {
            Comment c = comments.get( i );
            if( c.isUnlinked() )
            {
                comments.set( i, c.link( key) );
            }
            else
            {
                return;
            }
        }
    }

    /**
     * Strips the code suffix, if it is present, or returns the key unchanged.
     */
    public static String stripCodeSuffix( String key )
    {
        if ( key.endsWith( CODE_SUFFIX ) )
        {
            key = key.substring( 0, key.length() - CODE_SUFFIX.length() );
        }
        return key;
    }

    private void processMetaLine( String line, VariantGenerator variantGen )
    {
        String[] splitLine = split( line.substring( 1 ), "=", 1 );
        if ( splitLine.length != 2 )
        {
            throw new InvalidMetaLine( lines, lineNum );
        }

        String key   = splitLine[0];
        String value = splitLine[1];

        if ( !key.equals( key = stripCodeSuffix( key ) ) )
        {
            value = MegaCoder.decode( value );
        }

        maybeLinkToLastComment( key );

        if ( TextWorldManip.META_INTS.contains( key ) )
        {
            duplicateMetaCheck( m_metaInts.keySet(), key );
            m_metaInts.put( key, toInt( value ) );
        }
        else if ( TextWorldManip.META_STRINGS.contains( key ) )
        {
            duplicateMetaCheck( m_metaStrings.keySet(), key );
            m_metaStrings.put( key, value );
        }
        else if ( TextWorldManip.META_BOOLS.contains( key ) )
        {
            duplicateMetaCheck( m_metaBools.keySet(), key );
            m_metaBools.put( key, toBool( value ) );
        }
        else if ( TextWorldManip.META_INT_ARRAYS.contains( key ) )
        {
            duplicateMetaCheck( m_metaIntArrays.keySet(), key );
            m_metaIntArrays.put( key, toIntArray( value ) );
        }
        else if (
            matchesKeyList( TextWorldManip.META_STRING_ARRAYS_BY_KEY, key ) )
        {
            KeyListKey listKey = parseKeyListKey( key );

            Map<Integer, String> list = m_metaStringArraysByKey.get(
                listKey.prefix );

            if ( list == null )
            {
                list = new HashMap<Integer, String>();
                m_metaStringArraysByKey.put( listKey.prefix, list );
            }
            else if ( list.containsKey( listKey.number ) )
            {
                throw new DuplicateMetaKey( lines, lineNum );
            }

            if ( list.size() != listKey.number - 1 )
            {
                throw new ArrayByKeyElementMissing( lines, lineNum );
            }
            list.put( listKey.number, value );
        }
        else if ( TextWorldManip.ABILITIES.contains( key ) )
        {
            if ( abilities.keySet().contains( Token.Type.valueOf( key ) ) )
            {
                throw new DuplicateMetaKey( lines, lineNum );
            }
            abilities.put( Token.Type.valueOf( key ), toInt( value ) );
        }
        else if ( key.equals( TextWorldManip.water_definition ) )
        {
            String[] valueParts = split( value, "," );
            if ( valueParts.length != 3 )
            {
                throw new InvalidWaterDescription( lines, lineNum );
            }
            int x = toInt( valueParts[0] );
            int y = toInt( valueParts[1] );
            int contents = toInt( valueParts[2] );
            waterAmounts.put( new Position( x, y ), contents );
        }
        else if ( key.equals( "*" ) )
        {
            if ( currentStarPoint >= starPoints.size() )
            {
                throw new NotEnoughStars( lines, lineNum );
            }

            Position p = starPoints.get( currentStarPoint );

            new ItemsLineProcessor( this, p.x, p.y, value )
                .process( variantGen );

            ++currentStarPoint;
        }
        else
        {
            throw new UnknownMetaKey( lines, lineNum );
        }

    }

    private boolean matchesKeyList( List<String> keyList, String key )
    {
        return keyList.contains( parseKeyListKey( key ).prefix );
    }

    public static KeyListKey parseKeyListKey( String key )
    {
        Matcher m = keyListKeyRegex.matcher( key );
        if ( m.matches() )
        {
            return new KeyListKey(
                m.group( 1 ), Integer.parseInt( m.group( 2 ) ) );
        }
        else
        {
            return new KeyListKey( "NO KEY LIST MATCH", -1 );
        }
    }

    private int toInt( String value )
    {
        try
        {
            return Integer.valueOf( value );
        }
        catch( NumberFormatException e )
        {
            throw new NonIntegerMetaValue( lines, lineNum );
        }
    }

    private ArrayList<Integer> toIntArray( String value )
    {
        try
        {
            String[] items = value.split(",");
            ArrayList<Integer> ret = new ArrayList<Integer> ( items.length );
            for ( int i=0; i<items.length; i++ )
            {
                ret.add( i, new Integer( items[i] ) );
            }
            return ret;
        }
        catch( NumberFormatException e )
        {
            throw new NonIntegerMetaValue ( lines, lineNum );
        }
    }

    private boolean toBool( String value )
    {
        if ( value == null )
        {
            throw new NullBooleanMetaValue( lines, lineNum );
        }
        else if ( value.equals( "true" ) )
        {
            return true;
        }
        else if ( value.equals( "false" ) )
        {
            return false;
        }
        else
        {
            throw new NonBooleanMetaValue( lines, lineNum );
        }
    }

    private void processItemsLine( String line, VariantGenerator variantGen )
    {
        maybeLinkToLastComment( Comment.WORLD_ASCII_ART );
        // Treat empty lines as blank lines (Github converts blank lines
        // to empty lines, so it seems sensible to reverse the process).
        if ( line.length() != 0 )
        {
            if ( width == -1 )
            {
                width = line.length();
            }
            else if ( line.length() != width )
            {
                throw new WrongLineLength( lines, lineNum );
            }

            int i = 0;
            for ( char ch : asChars( line ) )
            {
                processChar( ch, i, height, variantGen );
                ++i;
            }
        }
        ++height;
    }

    public Thing processChar(
        char c, int x, int y, VariantGenerator variantGen )
    {
        Thing ret = null;

        switch( c )
        {
            case ' ':
            {
                break;
            }
            case '#':
            {
                blocks.add(
                    new Block( x, y, EARTH, FLAT, variantGen.next( 4 ) ) );
                break;
            }
            case 'M':
            {
                blocks.add(
                    new Block( x, y, METAL, FLAT, variantGen.next( 4 ) ) );
                break;
            }
            case '/':
            {
                blocks.add(
                    new Block( x, y, EARTH, UP_RIGHT, variantGen.next( 4 ) ) );
                break;
            }
            case '\\':
            {
                blocks.add(
                    new Block( x, y, EARTH, UP_LEFT, variantGen.next( 4 ) ) );
                break;
            }
            case '(':
            {
                blocks.add(
                    new Block( x, y, EARTH, BRIDGE_UP_RIGHT, 0 ) );
                break;
            }
            case ')':
            {
                blocks.add(
                    new Block( x, y, EARTH, BRIDGE_UP_LEFT, 0 ) );
                break;
            }
            case 'r':
            {
                Rabbit r = new Rabbit( x, y, RIGHT, Rabbit.Type.RABBIT );
                ret = r;
                rabbits.add( r );
                break;
            }
            case 'j':
            {
                Rabbit r = new Rabbit( x, y, LEFT, Rabbit.Type.RABBIT );
                ret = r;
                rabbits.add( r );
                break;
            }
            case 't':
            {
                Rabbit r = new Rabbit( x, y, RIGHT, Rabbit.Type.RABBOT );
                ret = r;
                rabbits.add( r );
                break;
            }
            case 'y':
            {
                Rabbit r = new Rabbit( x, y, LEFT, Rabbit.Type.RABBOT );
                ret = r;
                rabbits.add( r );
                break;
            }
            case 'Q':
            {
                ret = new Entrance( x, y );
                things.add( ret );
                break;
            }
            case 'O':
            {
                ret = new Exit( x, y );
                things.add( ret );
                break;
            }
            case 'A':
            {
                ret = new Fire( x, y, variantGen.next( 4 ) );
                things.add( ret );
                break;
            }
            case 'P':
            {
                ret = new Pipe( x, y );
                things.add( ret );
                break;
            }
            case 'b':
            {
                ret = new Token( x, y, Token.Type.bash );
                things.add( ret );
                break;
            }
            case 'd':
            {
                ret = new Token( x, y, Token.Type.dig );
                things.add( ret );
                break;
            }
            case 'i':
            {
                ret = new Token( x, y, Token.Type.bridge );
                things.add( ret );
                break;
            }
            case 'k':
            {
                ret = new Token( x, y, Token.Type.block );
                things.add( ret );
                break;
            }
            case 'c':
            {
                ret = new Token( x, y, Token.Type.climb );
                things.add( ret );
                break;
            }
            case 'p':
            {
                ret = new Token( x, y, Token.Type.explode );
                things.add( ret );
                break;
            }
            case 'l':
            {
                ret = new Token( x, y, Token.Type.brolly );
                things.add( ret );
                break;
            }
            case 'N':
            {
                // Default amount for a full water region, but may be
                // overwritten by an explicit water definition line.
                waterAmounts.put( new Position( x, y ),
                                  WaterUtil.MAX_CAPACITY );
                break;
            }
            case 'n':
            {
                // Default amount for a half water region, but may be
                // overwritten by an explicit water definition line.
                waterAmounts.put( new Position( x, y ),
                                  WaterUtil.HALF_CAPACITY );
                break;
            }
            case '*':
            {
                starPoints.add( new Position( x, y ) );
                break;
            }
            default:
            {
                throw new UnknownCharacter( lines, lineNum, x );
            }
        }
        return ret;
    }

    public VoidMarkerStyle.Style generateVoidMarkerStyle()
    {
        String marker = m_metaStrings.get( TextWorldManip.void_marker_style );
        if ( marker == null )
        {
            String name = m_metaStrings.get( TextWorldManip.name );
            if ( name == null )
            { // It is not a proper level, so this does not matter
                return VoidMarkerStyle.Style.HIGHLIGHTER;
            }
            // Generate a reproducible style from the level name
            int i = stringHash( name ) % VoidMarkerStyle.Style.values().length;
            return VoidMarkerStyle.Style.values()[i];
        }
        try
        {
            VoidMarkerStyle.Style s =
                VoidMarkerStyle.Style.valueOf( marker.toUpperCase() );
            return s;
        }
        catch ( IllegalArgumentException e )
        {
            throw new UnknownVoidMarkerStyle( marker );
        }
    }

    /**
     * Not a fancy hash, but the same string will always yield the
     * same number. Note that Object.hashCode results may vary each
     * time the JVM is started.
     */

    public static int stringHash( String s )
    {
        int hash = 0;
        for ( char c: s.toCharArray() )
        {
            hash += (int)c;
        }
        return hash;
    }

}

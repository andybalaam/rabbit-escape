package rabbitescape.engine.textworld;

import static rabbitescape.engine.Direction.*;
import static rabbitescape.engine.Block.Type.*;
import static rabbitescape.engine.util.Util.*;

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
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.Thing;
import rabbitescape.engine.Token;
import rabbitescape.engine.util.Dimension;
import rabbitescape.engine.util.MegaCoder;
import rabbitescape.engine.util.VariantGenerator;

public class LineProcessor
{
    private static final String CODE_SUFFIX = ".code";

    private static class Point
    {
        public final int x;
        public final int y;

        public Point( int x, int y )
        {
            this.x = x;
            this.y = y;
        }
    }

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
    private final Map<Token.Type, Integer> abilities;
    public  final String[] lines;
    private final Map<String, String>  m_metaStrings;
    private final Map<String, Map<Integer, String>> m_metaStringArraysByKey;
    private final Map<String, Integer> m_metaInts;
    private final Map<String, Boolean> m_metaBools;
    private final Map<String, ArrayList<Integer>> m_metaIntArrays;
    private final List<Point> starPoints;

    private int width;
    private int height;
    public  int lineNum;
    private int currentStarPoint;
    
    public LineProcessor(
        List<Block> blocks,
        List<Rabbit> rabbits,
        List<Thing> things,
        Map<Token.Type, Integer> abilities,
        String[] lines,
        VariantGenerator variantGen
    )
    {
        this.blocks = blocks;
        this.rabbits = rabbits;
        this.things = things;
        this.abilities = abilities;
        this.lines = lines;
        this.m_metaStrings           = new HashMap<>();
        this.m_metaStringArraysByKey = new HashMap<>();
        this.m_metaInts              = new HashMap<>();
        this.m_metaBools             = new HashMap<>();
        this.m_metaIntArrays         = new HashMap<>();
        starPoints = new ArrayList<Point>();

        width = -1;
        height = 0;
        lineNum = 0;
        currentStarPoint = 0;

        process( variantGen );
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
            ArrayList<String> ret = new ArrayList<String>( temp.size() );

            for ( Map.Entry<Integer, String> entry : temp.entrySet() )
            {
                while ( ret.size() < entry.getKey() - 1 )
                {
                    ret.add( "" );
                }

                ret.add( entry.getKey() - 1, entry.getValue() );
            }
            return ret.toArray( new String[ ret.size() ] );
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
                // Ignore comment
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

    private void processMetaLine( String line, VariantGenerator variantGen )
    {
        String[] splitLine = split( line.substring( 1 ), "=", 1 );
        if ( splitLine.length != 2 )
        {
            throw new InvalidMetaLine( lines, lineNum );
        }

        String key   = splitLine[0];
        String value = splitLine[1];

        if ( key.endsWith( CODE_SUFFIX ) )
        {
            key = key.substring( 0, key.length() - CODE_SUFFIX.length() );
            value = MegaCoder.decode( value );
        }

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
        else if ( key.equals( "*" ) )
        {
            if ( currentStarPoint >= starPoints.size() )
            {
                throw new NotEnoughStars( lines, lineNum );
            }

            Point p = starPoints.get( currentStarPoint );

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
        // Treat empty lines as blank lines (Github converts blank lines to empty lines, so it seems sensible to reverse the process).
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
                    new Block( x, y, solid_flat, variantGen.next( 4 ) ) );
                break;
            }
            case '/':
            {
                blocks.add(
                    new Block( x, y, solid_up_right, variantGen.next( 4 ) ) );
                break;
            }
            case '\\':
            {
                blocks.add(
                    new Block( x, y, solid_up_left, variantGen.next( 4 ) ) );
                break;
            }
            case '(':
            {
                blocks.add(
                    new Block( x, y, bridge_up_right, 0 ) );
                break;
            }
            case ')':
            {
                blocks.add(
                    new Block( x, y, bridge_up_left, 0 ) );
                break;
            }
            case 'r':
            {
                Rabbit r = new Rabbit( x, y, RIGHT );
                ret = r;
                rabbits.add( r );
                break;
            }
            case 'j':
            {
                Rabbit r = new Rabbit( x, y, LEFT );;
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
            case '*':
            {
                starPoints.add( new Point( x, y ) );
                break;
            }
            default:
            {
                throw new UnknownCharacter( lines, lineNum, x );
            }
        }
        return ret;
    }
}

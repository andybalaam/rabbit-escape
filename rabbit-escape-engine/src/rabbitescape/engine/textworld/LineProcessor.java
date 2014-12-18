package rabbitescape.engine.textworld;

import static rabbitescape.engine.Direction.*;
import static rabbitescape.engine.Block.Type.*;
import static rabbitescape.engine.util.Util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rabbitescape.engine.Block;
import rabbitescape.engine.Entrance;
import rabbitescape.engine.Exit;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.Thing;
import rabbitescape.engine.Token;
import rabbitescape.engine.util.Dimension;

class LineProcessor
{
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

    private final List<Block> blocks;
    private final List<Rabbit> rabbits;
    private final List<Thing> things;
    private final Map<Token.Type, Integer> abilities;
    public  final String[] lines;
    private final Map<String, String>  m_metaStrings;
    private final Map<String, Integer> m_metaInts;
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
        String[] lines
    )
    {
        this.blocks = blocks;
        this.rabbits = rabbits;
        this.things = things;
        this.abilities = abilities;
        this.lines = lines;
        this.m_metaStrings = new HashMap<>();
        this.m_metaInts    = new HashMap<>();
        starPoints = new ArrayList<Point>();

        width = -1;
        height = 0;
        lineNum = 0;
        currentStarPoint = 0;

        process();
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

    public Dimension size()
    {
        return new Dimension( width, height );
    }

    private void process()
    {
        for ( String line : lines )
        {
            if ( line.startsWith( ":" ) )
            {
                processMetaLine( line );
            }
            else
            {
                processItemsLine( line );
            }
            ++lineNum;
        }

        if ( starPoints.size() > currentStarPoint )
        {
            throw new TooManyStars( lines );
        }
    }

    private void processMetaLine( String line )
    {
        String[] splitLine = line.substring( 1 ).split( "=" );
        if ( splitLine.length != 2 )
        {
            throw new InvalidMetaLine( lines, lineNum );
        }

        String key   = splitLine[0];
        String value = splitLine[1];

        if ( TextWorldManip.META_INTS.contains( key ) )
        {
            m_metaInts.put( key, toInt( value ) );
        }
        else if ( TextWorldManip.META_STRINGS.contains( key ) )
        {
            m_metaStrings.put( key, value );
        }
        else if ( TextWorldManip.ABILITIES.contains( key ) )
        {
            abilities.put( Token.Type.valueOf( key ), toInt( value ) );
        }
        else if ( key.equals( "*" ) )
        {
            if ( currentStarPoint >= starPoints.size() )
            {
                throw new NotEnoughStars( lines, lineNum );
            }

            Point p = starPoints.get( currentStarPoint );

            new ItemsLineProcessor( this, p.x, p.y, value ).process();

            ++currentStarPoint;
        }
        else
        {
            throw new UnknownMetaKey( lines, lineNum );
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

    private void processItemsLine( String line )
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
            processChar( ch, i, height );
            ++i;
        }
        ++height;
    }

    public Thing processChar( char c, int x, int y )
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
                blocks.add( new Block( x, y, solid_flat ) );
                break;
            }
            case '/':
            {
                blocks.add( new Block( x, y, solid_up_right ) );
                break;
            }
            case '\\':
            {
                blocks.add( new Block( x, y, solid_up_left ) );
                break;
            }
            case '(':
            {
                blocks.add(
                    new Block( x, y, bridge_up_right ) );
                break;
            }
            case ')':
            {
                blocks.add(
                    new Block( x, y, bridge_up_left ) );
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

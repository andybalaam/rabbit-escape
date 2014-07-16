package rabbitescape.engine.textworld;

import static rabbitescape.engine.Direction.*;
import static rabbitescape.engine.util.Util.*;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rabbitescape.engine.Block;
import rabbitescape.engine.Entrance;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.Thing;

class LineProcessor
{
    private static final List<String> META_INTS =
        Arrays.asList( new String[] {
            "num_rabbits"
        } );

    private static final List<String> META_STRINGS =
        Arrays.asList( new String[] {
            "name"
        } );

    private final List<Block> blocks;
    private final List<Thing> things;
    private final String[] lines;
    private final Map<String, String>  m_metaStrings;
    private final Map<String, Integer> m_metaInts;

    private int width;
    private int height;
    private int lineNum;

    public LineProcessor(
        List<Block> blocks, List<Thing> things, String[] lines )
    {
        this.blocks = blocks;
        this.things = things;
        this.lines = lines;
        this.m_metaStrings = new HashMap<String, String>();
        this.m_metaInts    = new HashMap<String, Integer>();

        width = -1;
        height = 0;
        lineNum = 0;

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
            return ret.intValue();
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

        if ( META_INTS.contains( key ) )
        {
            try
            {
                m_metaInts.put( key, Integer.valueOf( value ) );
            }
            catch( NumberFormatException e )
            {
                throw new NonIntegerMetaValue( lines, lineNum );
            }
        }
        else if ( META_STRINGS.contains( key ) )
        {
            m_metaStrings.put( key, value );
        }
        else
        {
            throw new UnknownMetaKey( lines, lineNum );
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

        int charNum = 0;
        for ( char c : asChars( line ) )
        {
            processChar( charNum, c );
            ++charNum;
        }
        ++height;
    }

    private void processChar( int charNum, char c )
    {
        switch( c )
        {
            case ' ':
            {
                break;
            }
            case '#':
            {
                blocks.add( new Block( charNum, height, DOWN ) );
                break;
            }
            case '/':
            {
                blocks.add( new Block( charNum, height, RIGHT ) );
                break;
            }
            case '\\':
            {
                blocks.add( new Block( charNum, height, LEFT ) );
                break;
            }
            case 'r':
            {
                things.add( new Rabbit( charNum, height, RIGHT ) );
                break;
            }
            case 'j':
            {
                things.add( new Rabbit( charNum, height, LEFT ) );
                break;
            }
            case 'Q':
            {
                things.add( new Entrance( charNum, height ) );
                break;
            }
            default:
            {
                throw new UnknownCharacter( lines, height, charNum );
            }
        }
    }
}

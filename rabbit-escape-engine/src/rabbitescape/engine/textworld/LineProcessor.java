package rabbitescape.engine.textworld;

import static rabbitescape.engine.Direction.*;
import static rabbitescape.engine.Block.Type.*;
import static rabbitescape.engine.util.Util.*;

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
    private final List<Block> blocks;
    private final List<Rabbit> rabbits;
    private final List<Thing> things;
    private final Map<Token.Type, Integer> abilities;
    private final String[] lines;
    private final Map<String, String>  m_metaStrings;
    private final Map<String, Integer> m_metaInts;

    private int width;
    private int height;
    private int lineNum;

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
                blocks.add( new Block( charNum, height, solid_flat ) );
                break;
            }
            case '/':
            {
                blocks.add( new Block( charNum, height, solid_up_right ) );
                break;
            }
            case '\\':
            {
                blocks.add( new Block( charNum, height, solid_up_left ) );
                break;
            }
            case '(':
            {
                blocks.add(
                    new Block( charNum, height, bridge_up_right ) );
                break;
            }
            case ')':
            {
                blocks.add(
                    new Block( charNum, height, bridge_up_left ) );
                break;
            }
            case 'r':
            {
                rabbits.add( new Rabbit( charNum, height, RIGHT ) );
                break;
            }
            case 'j':
            {
                rabbits.add( new Rabbit( charNum, height, LEFT ) );
                break;
            }
            case 'Q':
            {
                things.add( new Entrance( charNum, height ) );
                break;
            }
            case 'O':
            {
                things.add( new Exit( charNum, height ) );
                break;
            }
            case 'b':
            {
                things.add( new Token( charNum, height, Token.Type.bash ) );
                break;
            }
            case 'd':
            {
                things.add( new Token( charNum, height, Token.Type.dig ) );
                break;
            }
            case 'i':
            {
                things.add( new Token( charNum, height, Token.Type.bridge ) );
                break;
            }
            default:
            {
                throw new UnknownCharacter( lines, height, charNum );
            }
        }
    }
}

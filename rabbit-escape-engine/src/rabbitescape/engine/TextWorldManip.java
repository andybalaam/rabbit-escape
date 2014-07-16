package rabbitescape.engine;

import static rabbitescape.engine.Direction.*;
import static rabbitescape.engine.util.Util.*;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rabbitescape.engine.ChangeDescription.Change;
import rabbitescape.engine.err.RabbitEscapeException;

public class TextWorldManip
{
    public static class IncorrectLine extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;

        public final String lines;
        public final int lineNum;
        public final String line;

        public IncorrectLine( String[] lines, int lineNum )
        {
            this.lines = join( "\n", lines );
            this.lineNum = lineNum + 1; // Human-readable line number
            this.line = lines[lineNum];
        }
    }

    public static class WrongLineLength extends IncorrectLine
    {
        private static final long serialVersionUID = 1L;

        public WrongLineLength( String[] lines, int lineNum )
        {
            super( lines, lineNum );
        }
    }

    public static class InvalidMetaLine extends IncorrectLine
    {
        private static final long serialVersionUID = 1L;

        public InvalidMetaLine( String[] lines, int lineNum )
        {
            super( lines, lineNum );
        }
    }

    public static class UnknownMetaKey extends IncorrectLine
    {
        private static final long serialVersionUID = 1L;

        public UnknownMetaKey( String[] lines, int lineNum )
        {
            super( lines, lineNum );
        }
    }

    public static class NonIntegerMetaValue extends IncorrectLine
    {
        private static final long serialVersionUID = 1L;

        public NonIntegerMetaValue( String[] lines, int lineNum )
        {
            super( lines, lineNum );
        }
    }

    public static class UnknownCharacter extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;

        public final String lines;
        public final int lineNum;
        public final char character;

        public UnknownCharacter( String[] lines, int lineNum, int charNum )
        {
            this.lines = join( "\n", lines );
            this.lineNum = lineNum + 1; // Human-readable line number
            this.character = getNth( asChars( lines[lineNum] ), charNum );
        }
    }



    public static World createWorld( String... lines )
        throws WrongLineLength, UnknownCharacter
    {
        List<Block> blocks = new ArrayList<Block>();
        List<Thing> things = new ArrayList<Thing>();

        LineProcessor processor = new LineProcessor( blocks, things, lines );

        return new World(
            processor.size(),
            blocks,
            things,
            processor.metaString( "name",        "Untitled" ),
            processor.metaInt(    "num_rabbits", 10 )
        );
    }

    public static World createEmptyWorld( int width, int height )
    {
        return new World(
            new Dimension( width, height ),
            new ArrayList<Block>(),
            new ArrayList<Thing>(),
            "Empty World",
            0
        );
    }

    private static class LineProcessor
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
                ++charNum;
            }
            ++height;
        }
    }

    public static String[] renderWorld( World world, boolean showChanges )
    {
        char[][] chars = filledChars(
            world.size.height, world.size.width, ' ' );

        for ( Block block : world.blocks )
        {
            chars[ block.y ][ block.x ] = charForBlock( block );
        }

        for ( Thing thing : world.things )
        {
            if ( thing.alive )
            {
                chars[ thing.y ][ thing.x ] = charForThing( thing );
            }
        }

        if ( showChanges )
        {
            ChangeDescription desc = world.describeChanges();
            for ( ChangeDescription.Change change : desc.changes )
            {
                charForChange( change, chars );
            }
        }

        return charsToStrings( chars );
    }

    private static char charForBlock( Block block )
    {
        if ( block.riseDir == DOWN )
        {
            return '#';
        }
        if ( block.riseDir == RIGHT )
        {
            return '/';
        }
        if ( block.riseDir == LEFT )
        {
            return '\\';
        }
        else
        {
            throw new AssertionError(
                "Unknown Block type: " + block.getClass() );
        }
    }

    private static char charForThing( Thing thing )
    {
        if ( thing instanceof Rabbit )
        {
            return ( (Rabbit)thing ).dir == RIGHT ? 'r' : 'j';
        }
        else if ( thing instanceof Entrance )
        {
            return 'Q';
        }
        else
        {
            throw new AssertionError(
                "Unknown Thing type: " + thing.getClass() );
        }
    }

    public static String[] renderChangeDescription(
        World world, ChangeDescription desc )
    {
        char[][] chars = filledChars(
            world.size.height, world.size.width, ' ' );

        for ( Change change : desc.changes )
        {
            charForChange( change, chars );
        }

        return charsToStrings( chars );
    }


    private static void charForChange( Change change, char[][] chars )
    {
        switch( change.state )
        {
            case RABBIT_WALKING_LEFT:
                chars[change.y][change.x-1] = '<';
                break;
            case RABBIT_TURNING_LEFT_TO_RIGHT:
                chars[change.y][change.x] = '|';
                break;
            case RABBIT_TURNING_LEFT_TO_RIGHT_RISING:
                chars[change.y][change.x] = '{';
                break;
            case RABBIT_TURNING_LEFT_TO_RIGHT_LOWERING:
                chars[change.y][change.x] = '[';
                break;
            case RABBIT_WALKING_RIGHT:
                chars[change.y][change.x + 1] = '>';
                break;
            case RABBIT_TURNING_RIGHT_TO_LEFT:
                chars[change.y][change.x] = '?';
                break;
            case RABBIT_TURNING_RIGHT_TO_LEFT_RISING:
                chars[change.y][change.x] = '}';
                break;
            case RABBIT_TURNING_RIGHT_TO_LEFT_LOWERING:
                chars[change.y][change.x] = ']';
                break;
            case RABBIT_RISING_RIGHT_START:
                chars[change.y][change.x + 1] = '~';
                break;
            case RABBIT_RISING_RIGHT_CONTINUE:
                chars[change.y - 1][change.x + 1] = '$';
                break;
            case RABBIT_RISING_RIGHT_END:
                chars[change.y - 1][change.x + 1] = '\'';
                break;
            case RABBIT_RISING_LEFT_START:
                chars[change.y][change.x - 1] = '`';
                break;
            case RABBIT_RISING_LEFT_CONTINUE:
                chars[change.y - 1][change.x - 1] = '^';
                break;
            case RABBIT_RISING_LEFT_END:
                chars[change.y - 1][change.x - 1] = '!';
                break;
            case RABBIT_LOWERING_RIGHT_START:
                chars[change.y + 1][change.x + 1] = '-';
                break;
            case RABBIT_LOWERING_RIGHT_CONTINUE:
                chars[change.y + 1][change.x + 1] = '@';
                break;
            case RABBIT_LOWERING_RIGHT_END:
                chars[change.y][change.x + 1] = '_';
                break;
            case RABBIT_LOWERING_LEFT_START:
                chars[change.y + 1][change.x - 1] = '=';
                break;
            case RABBIT_LOWERING_LEFT_CONTINUE:
                chars[change.y + 1][change.x - 1] = '%';
                break;
            case RABBIT_LOWERING_LEFT_END:
                chars[change.y][change.x - 1] = '+';
                break;
            case RABBIT_LOWERING_AND_RISING_RIGHT:
                chars[change.y][change.x + 1] = ',';
                break;
            case RABBIT_LOWERING_AND_RISING_LEFT:
                chars[change.y][change.x - 1] = '.';
                break;
            case RABBIT_RISING_AND_LOWERING_RIGHT:
                chars[change.y][change.x + 1] = '&';
                break;
            case RABBIT_RISING_AND_LOWERING_LEFT:
                chars[change.y][change.x - 1] = '*';
                break;
            case RABBIT_FALLING:
                chars[change.y + 1][change.x] = 'f';
                chars[change.y + 2][change.x] = 'f';
                break;
            case RABBIT_FALLING_ONTO_LOWER_RIGHT:
                chars[change.y + 1][change.x] = 'f';
                chars[change.y + 2][change.x] = 'e';
                break;
            case RABBIT_FALLING_ONTO_LOWER_LEFT:
                chars[change.y + 1][change.x] = 'f';
                chars[change.y + 2][change.x] = 's';
                break;
            case RABBIT_FALLING_ONTO_RISE_RIGHT:
                chars[change.y + 1][change.x] = 'f';
                chars[change.y + 2][change.x] = 'd';
                break;
            case RABBIT_FALLING_ONTO_RISE_LEFT:
                chars[change.y + 1][change.x] = 'f';
                chars[change.y + 2][change.x] = 'a';
                break;
            case RABBIT_FALLING_1:
                chars[change.y + 1][change.x] = 'f';
                break;
            case RABBIT_FALLING_1_ONTO_LOWER_RIGHT:
                chars[change.y + 1][change.x] = 'e';
                break;
            case RABBIT_FALLING_1_ONTO_LOWER_LEFT:
                chars[change.y + 1][change.x] = 's';
                break;
            case RABBIT_FALLING_1_ONTO_RISE_RIGHT:
                chars[change.y + 1][change.x] = 'd';
                break;
            case RABBIT_FALLING_1_ONTO_RISE_LEFT:
                chars[change.y + 1][change.x] = 'a';
                break;
            case RABBIT_FALLING_1_TO_DEATH:
                chars[change.y + 1][change.x] = 'x';
                break;
            case RABBIT_DYING_OF_FALLING_2:
                chars[change.y][change.x] = 'y';
                break;
            case RABBIT_DYING_OF_FALLING:
                chars[change.y][change.x] = 'X';
                break;
            default:
                throw new AssertionError(
                    "Unknown Change state: " + change.state.name() );
        }
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

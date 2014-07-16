package rabbitescape.engine;

import static rabbitescape.engine.Direction.*;
import static rabbitescape.engine.util.Util.*;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rabbitescape.engine.ChangeDescription.Change;
import rabbitescape.engine.err.RabbitEscapeException;

public class TextWorldManip
{
    public static class WrongLineLength extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;

        public final String lines;
        public final int lineNum;
        public final String line;

        public WrongLineLength( String[] lines, int lineNum )
        {
            this.lines = join( "\n", lines );
            this.lineNum = lineNum + 1; // Human-readable line number
            this.line = lines[lineNum];
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

        Dimension size = processLines( lines, blocks, things );

        return new World( size, blocks, things );
    }

    public static World createEmptyWorld( int width, int height )
    {
        return new World(
            new Dimension( width, height ),
            new ArrayList<Block>(),
            new ArrayList<Thing>()
        );
    }

    private static Dimension processLines(
        String[] lines,
        List<Block> blocks,
        List<Thing> things
    )
        throws WrongLineLength, UnknownCharacter
    {
        if ( lines.length == 0 )
        {
            return new Dimension( 3, 3 );
        }

        int width = lines[0].length();

        int lineNum = 0;
        for ( String line : lines )
        {
            if ( line.length() != width )
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
                        blocks.add( new Block( charNum, lineNum, DOWN ) );
                        break;
                    }
                    case '/':
                    {
                        blocks.add( new Block( charNum, lineNum, RIGHT ) );
                        break;
                    }
                    case '\\':
                    {
                        blocks.add( new Block( charNum, lineNum, LEFT ) );
                        break;
                    }
                    case 'r':
                    {
                        things.add( new Rabbit( charNum, lineNum, RIGHT ) );
                        break;
                    }
                    case 'j':
                    {
                        things.add( new Rabbit( charNum, lineNum, LEFT ) );
                        break;
                    }
                    default:
                    {
                        throw new UnknownCharacter( lines, lineNum, charNum );
                    }
                }
                ++charNum;
            }
            ++lineNum;
        }

        return new Dimension( width, lines.length );
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
            case RABBIT_WALKING_RIGHT:
                chars[change.y][change.x + 1] = '>';
                break;
            case RABBIT_TURNING_RIGHT_TO_LEFT:
                chars[change.y][change.x] = '?';
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
            case RABBIT_FALLING:
                chars[change.y + 1][change.x] = 'f';
                chars[change.y + 2][change.x] = 'f';
                break;
            case RABBIT_FALLING_1:
                chars[change.y + 1][change.x] = 'f';
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

package rabbitescape.engine;

import static rabbitescape.engine.ChangeDescription.State.*;
import static rabbitescape.engine.Direction.*;
import rabbitescape.engine.ChangeDescription.State;

public class Walking implements Behaviour
{
    @Override
    public State newState( Rabbit rabbit, World world )
    {
        if ( continueRise( rabbit, world ) )
        {
            return rl(
                rabbit,
                RABBIT_RISING_RIGHT_CONTINUE,
                RABBIT_RISING_LEFT_CONTINUE
            );
        }
        else if ( startRise( rabbit, world ) )
        {
            return rl(
                rabbit,
                RABBIT_RISING_RIGHT_START,
                RABBIT_RISING_LEFT_START
            );
        }
        else if ( finishRise( rabbit, world ) )
        {
            return rl(
                rabbit,
                RABBIT_RISING_RIGHT_END,
                RABBIT_RISING_LEFT_END
            );
        }
        else if ( continueLower( rabbit, world ) )
        {
            return rl(
                rabbit,
                RABBIT_LOWERING_RIGHT_CONTINUE,
                RABBIT_LOWERING_LEFT_CONTINUE
            );
        }
        else if ( startLower( rabbit, world ) )
        {
            return rl(
                rabbit,
                RABBIT_LOWERING_RIGHT_START,
                RABBIT_LOWERING_LEFT_START
            );
        }
        else if ( finishLower( rabbit, world ) )
        {
            return rl(
                rabbit,
                RABBIT_LOWERING_RIGHT_END,
                RABBIT_LOWERING_LEFT_END
            );
        }
        else if ( turn( rabbit, world ) )
        {
            return rl(
                rabbit,
                RABBIT_TURNING_RIGHT_TO_LEFT,
                RABBIT_TURNING_LEFT_TO_RIGHT
            );
        }
        else
        {
            return rl( rabbit, RABBIT_WALKING_RIGHT, RABBIT_WALKING_LEFT );
        }
    }

    private State rl( Rabbit rabbit, State rightState, State leftState )
    {
        return rabbit.dir == RIGHT ? rightState : leftState;
    }

    @Override
    public boolean behave( Rabbit rabbit, State state )
    {
        switch ( state )
        {
            case RABBIT_RISING_LEFT_START:
            case RABBIT_LOWERING_LEFT_END:
            case RABBIT_WALKING_LEFT:
            {
                --rabbit.x;
                return true;
            }
            case RABBIT_RISING_RIGHT_START:
            case RABBIT_LOWERING_RIGHT_END:
            case RABBIT_WALKING_RIGHT:
            {
                ++rabbit.x;
                return true;
            }
            case RABBIT_RISING_LEFT_CONTINUE:
            case RABBIT_RISING_LEFT_END:
            {
                --rabbit.y;
                --rabbit.x;
                return true;
            }
            case RABBIT_RISING_RIGHT_CONTINUE:
            case RABBIT_RISING_RIGHT_END:
            {
                --rabbit.y;
                ++rabbit.x;
                return true;
            }
            case RABBIT_LOWERING_LEFT_CONTINUE:
            case RABBIT_LOWERING_LEFT_START:
            {
                ++rabbit.y;
                --rabbit.x;
                return true;
            }
            case RABBIT_LOWERING_RIGHT_CONTINUE:
            case RABBIT_LOWERING_RIGHT_START:
            {
                ++rabbit.y;
                ++rabbit.x;
                return true;
            }
            case RABBIT_TURNING_LEFT_TO_RIGHT:
            {
                rabbit.dir = RIGHT;
                return true;
            }
            case RABBIT_TURNING_RIGHT_TO_LEFT:
            {
                rabbit.dir = LEFT;
                return true;
            }
            default:
            {
                throw new AssertionError(
                    "Should have handled all states in Walking or before,"
                    + " but we are in state " + state.name()
                );
            }
        }
    }

    private static boolean turn( Rabbit rabbit, World world )
    {
        return world.flatBlockAt( dest( rabbit ), rabbit.y );
    }

    private static int dest( Rabbit rabbit )
    {
        return ( rabbit.dir == RIGHT ) ? rabbit.x + 1 : rabbit.x - 1;
    }

    private boolean startRise( Rabbit rabbit, World world )
    {
        return riseBlockAt( dest( rabbit ), rabbit.y, rabbit, world );
    }

    private boolean continueRise( Rabbit rabbit, World world )
    {
        return (
               riseBlockAt( rabbit.x,       rabbit.y,     rabbit, world )
            && riseBlockAt( dest( rabbit ), rabbit.y - 1, rabbit, world )
        );
    }

    private boolean finishRise( Rabbit rabbit, World world )
    {
        return riseBlockAt( rabbit.x, rabbit.y, rabbit, world );
    }

    private boolean continueLower( Rabbit rabbit, World world )
    {
        return (
               lowerBlockAt( rabbit.x, rabbit.y, rabbit, world )
            && lowerBlockAt( dest( rabbit ), rabbit.y + 1, rabbit, world )
        );
    }

    private boolean startLower( Rabbit rabbit, World world )
    {
        return lowerBlockAt( dest( rabbit ), rabbit.y + 1, rabbit, world );
    }

    private boolean finishLower( Rabbit rabbit, World world )
    {
        return lowerBlockAt( rabbit.x, rabbit.y, rabbit, world );
    }

    private boolean riseBlockAt( int x, int y, Rabbit rabbit, World world )
    {
        Block block = world.getBlockAt( x, y );
        return ( block != null && block.riseDir == rabbit.dir );
    }

    private boolean lowerBlockAt( int x, int y, Rabbit rabbit, World world )
    {
        Block block = world.getBlockAt( x, y );
        return (
            block != null
            && block.riseDir == Direction.opposite( rabbit.dir )
        );
    }
}

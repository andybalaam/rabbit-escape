package rabbitescape.engine;

import static rabbitescape.engine.ChangeDescription.State.*;
import static rabbitescape.engine.Direction.*;
import static rabbitescape.engine.Block.Type.*;

import java.util.Map;

import rabbitescape.engine.ChangeDescription.State;

public class Walking implements Behaviour
{
    private static class StateCalc
    {
        private final Rabbit rabbit;
        private final World world;

        StateCalc( Rabbit rabbit, World world )
        {
            this.rabbit = rabbit;
            this.world = world;
        }

        public State newState()
        {
            if ( rising() )
            {
                int nextX = destX();
                int nextY = rabbit.y - 1;

                if ( riseBlockAt( nextX, nextY ) )
                {
                    return rl(
                        RABBIT_RISING_RIGHT_CONTINUE,
                        RABBIT_RISING_LEFT_CONTINUE
                    );
                }
                else if ( lowerBlockAt( nextX, rabbit.y ) )
                {
                    return rl(
                        RABBIT_RISING_AND_LOWERING_RIGHT,
                        RABBIT_RISING_AND_LOWERING_LEFT
                    );
                }
                else if
                (
                       world.flatBlockAt( nextX, nextY )
                    || lowerSlopeAt( nextX, nextY )
                )
                {
                    return rl(
                        RABBIT_TURNING_RIGHT_TO_LEFT_RISING,
                        RABBIT_TURNING_LEFT_TO_RIGHT_RISING
                    );
                }
                else
                {
                    return rl(
                        RABBIT_RISING_RIGHT_END,
                        RABBIT_RISING_LEFT_END
                    );
                }
            }
            else if( lowering() )
            {
                int nextX = destX();
                int nextY = rabbit.y + 1;

                if ( riseBlockAt( nextX, rabbit.y ) )
                {
                    return rl(
                        RABBIT_LOWERING_AND_RISING_RIGHT,
                        RABBIT_LOWERING_AND_RISING_LEFT
                    );
                }
                else if(
                        world.flatBlockAt( nextX, rabbit.y )
                     || lowerSlopeAt( nextX, rabbit.y )
                 )
                 {
                     return rl(
                         RABBIT_TURNING_RIGHT_TO_LEFT_LOWERING,
                         RABBIT_TURNING_LEFT_TO_RIGHT_LOWERING
                     );
                 }
                else if ( lowerBlockAt( nextX, nextY ) )
                {
                    return rl(
                        RABBIT_LOWERING_RIGHT_CONTINUE,
                        RABBIT_LOWERING_LEFT_CONTINUE
                    );
                }
                else
                {
                    return rl(
                        RABBIT_LOWERING_RIGHT_END,
                        RABBIT_LOWERING_LEFT_END
                    );
                }
            }
            else  // On flat ground now
            {
                int nextX = destX();
                int nextY = rabbit.y;

                if ( riseBlockAt( nextX, nextY ) )
                {
                    return rl(
                        RABBIT_RISING_RIGHT_START,
                        RABBIT_RISING_LEFT_START
                    );
                }
                else if ( lowerBlockAt( nextX, rabbit.y + 1 ) )
                {
                    return rl(
                        RABBIT_LOWERING_RIGHT_START,
                        RABBIT_LOWERING_LEFT_START
                    );
                }
                else if
                (
                       world.flatBlockAt( nextX, nextY )
                    || lowerSlopeAt( nextX, nextY )
                )
                {
                    return rl(
                        RABBIT_TURNING_RIGHT_TO_LEFT,
                        RABBIT_TURNING_LEFT_TO_RIGHT
                    );
                }
                else
                {
                    return rl(
                        RABBIT_WALKING_RIGHT,
                        RABBIT_WALKING_LEFT
                    );
                }
            }
        }

        private int destX()
        {
            return ( rabbit.dir == RIGHT ) ? rabbit.x + 1 : rabbit.x - 1;
        }

        private State rl( State rightState, State leftState )
        {
            return BehaviourTools.rl( rabbit, rightState, leftState );
        }

        private boolean rising()
        {
            return riseBlockAt( rabbit.x, rabbit.y );
        }

        private boolean lowering()
        {
            return (
                   !rabbit.underBridge
                && lowerBlockAt( rabbit.x, rabbit.y )
            );
        }

        private boolean riseBlockAt( int x, int y )
        {
            Block block = world.getBlockAt( x, y );
            return ( block != null && block.riseDir() == rabbit.dir );
        }

        private boolean lowerBlockAt( int x, int y )
        {
            Block block = world.getBlockAt( x, y );
            return (
                   block != null
                && block.riseDir() == Direction.opposite( rabbit.dir )
            );
        }

        private boolean lowerSlopeAt( int x, int y )
        {
            Block block = world.getBlockAt( x, y );
            return (
                   block != null
                && block.riseDir() == Direction.opposite( rabbit.dir )
                && (
                          block.type == solid_up_right
                       || block.type == solid_up_left
               )
            );
        }
    }

    @Override
    public State newState( Rabbit rabbit, World world )
    {
        return new StateCalc( rabbit, world ).newState();
    }

    @Override
    public boolean behave( World world, Rabbit rabbit, State state )
    {
        rabbit.underBridge = false;

        switch ( state )
        {
            case RABBIT_WALKING_LEFT:
            case RABBIT_LOWERING_LEFT_END:
            {
                --rabbit.x;
                checkUnderBridge( world, rabbit );
                return true;
            }
            case RABBIT_WALKING_RIGHT:
            case RABBIT_LOWERING_RIGHT_END:
            {
                ++rabbit.x;
                checkUnderBridge( world, rabbit );
                return true;
            }
            case RABBIT_RISING_LEFT_START:
            case RABBIT_LOWERING_AND_RISING_LEFT:
            case RABBIT_RISING_AND_LOWERING_LEFT:
            {
                --rabbit.x;
                return true;
            }
            case RABBIT_RISING_RIGHT_START:
            case RABBIT_LOWERING_AND_RISING_RIGHT:
            case RABBIT_RISING_AND_LOWERING_RIGHT:
            {
                ++rabbit.x;
                return true;
            }
            case RABBIT_RISING_LEFT_END:
            {
                --rabbit.y;
                --rabbit.x;
                checkUnderBridge( world, rabbit );
                return true;
            }
            case RABBIT_RISING_LEFT_CONTINUE:
            {
                --rabbit.y;
                --rabbit.x;
                return true;
            }
            case RABBIT_RISING_RIGHT_END:
            {
                --rabbit.y;
                ++rabbit.x;
                checkUnderBridge( world, rabbit );
                return true;
            }
            case RABBIT_RISING_RIGHT_CONTINUE:
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
            case RABBIT_TURNING_LEFT_TO_RIGHT_RISING:
            case RABBIT_TURNING_LEFT_TO_RIGHT_LOWERING:
            {
                rabbit.dir = RIGHT;
                return true;
            }
            case RABBIT_TURNING_RIGHT_TO_LEFT:
            case RABBIT_TURNING_RIGHT_TO_LEFT_RISING:
            case RABBIT_TURNING_RIGHT_TO_LEFT_LOWERING:
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

    private void checkUnderBridge( World world, Rabbit rabbit )
    {
        Block thisBlock = world.getBlockAt( rabbit.x, rabbit.y );
        if (
               thisBlock != null
            && (
                   thisBlock.type == bridge_up_right
                || thisBlock.type == bridge_up_left
            )
        )
        {
            rabbit.underBridge = true;
        }
    }

    @Override
    public void saveState( Map<String, String> saveState )
    {
    }

    @Override
    public void restoreFromState( Map<String, String> saveState )
    {
    }
}

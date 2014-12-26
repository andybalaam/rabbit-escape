package rabbitescape.engine.behaviours;

import static rabbitescape.engine.ChangeDescription.State.*;
import static rabbitescape.engine.Direction.*;
import static rabbitescape.engine.Block.Type.*;

import rabbitescape.engine.*;
import rabbitescape.engine.ChangeDescription.State;

public class Walking extends Behaviour
{
    @Override
    public void cancel()
    {
    }

    private static class StateCalc
    {
        private final BehaviourTools t;
        private final Rabbit rabbit;
        private final World world;

        StateCalc( Rabbit rabbit, World world )
        {
            this.t = new BehaviourTools( rabbit, world );
            this.rabbit = rabbit;
            this.world = world;
        }

        public State newState()
        {
            if ( rising() )
            {
                int nextX = destX();
                int nextY = rabbit.y - 1;

                if
                (
                       world.flatBlockAt( nextX, nextY )
                    || lowerSlopeAt( nextX, nextY )
                    || blockerAt( nextX, nextY )
                )
                {
                    return rl(
                        RABBIT_TURNING_RIGHT_TO_LEFT_RISING,
                        RABBIT_TURNING_LEFT_TO_RIGHT_RISING
                    );
                }
                else if ( riseBlockAt( nextX, nextY ) )
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

                if(
                       world.flatBlockAt( nextX, rabbit.y )
                    || lowerSlopeAt( nextX, rabbit.y )
                    || blockerAt( nextX, nextY )
                )
                {
                    return rl(
                        RABBIT_TURNING_RIGHT_TO_LEFT_LOWERING,
                        RABBIT_TURNING_LEFT_TO_RIGHT_LOWERING
                    );
                }
                else if ( riseBlockAt( nextX, rabbit.y ) )
                {
                    return rl(
                        RABBIT_LOWERING_AND_RISING_RIGHT,
                        RABBIT_LOWERING_AND_RISING_LEFT
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
                    if ( blockerAt( nextX, rabbit.y ) )
                    {
                        return rl(
                            RABBIT_TURNING_RIGHT_TO_LEFT_LOWERING,
                            RABBIT_TURNING_LEFT_TO_RIGHT_LOWERING
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
            }
            else  // On flat ground now
            {
                int nextX = destX();
                int nextY = rabbit.y;

                if
                (
                       world.flatBlockAt( nextX, nextY )
                    || lowerSlopeAt( nextX, nextY )
                    || blockerAt( nextX, nextY )
                )
                {
                    return rl(
                        RABBIT_TURNING_RIGHT_TO_LEFT,
                        RABBIT_TURNING_LEFT_TO_RIGHT
                    );
                }
                else if ( riseBlockAt( nextX, nextY ) )
                {
                    return rl(
                        RABBIT_RISING_RIGHT_START,
                        RABBIT_RISING_LEFT_START
                    );
                }
                else if ( lowerBlockAt( nextX, rabbit.y + 1 ) )
                {
                    if ( blockerAt( nextX, rabbit.y + 1 ) )
                    {
                        return rl(
                            RABBIT_TURNING_RIGHT_TO_LEFT,
                            RABBIT_TURNING_LEFT_TO_RIGHT
                        );
                    }
                    else
                    {
                        return rl(
                            RABBIT_LOWERING_RIGHT_START,
                            RABBIT_LOWERING_LEFT_START
                        );
                    }
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

        private boolean blockerAt( int nextX, int nextY )
        {
            Rabbit[] rabbits = world.getRabbitsAt( nextX, nextY );
            for ( Rabbit r : rabbits )
            {
                if ( r.state == RABBIT_BLOCKING )
                {
                    return true;
                }
            }
            return false;
        }

        private int destX()
        {
            return ( rabbit.dir == RIGHT ) ? rabbit.x + 1 : rabbit.x - 1;
        }

        private State rl( State rightState, State leftState )
        {
            return t.rl( rightState, leftState );
        }

        private boolean rising()
        {
            return (
                rabbit.onSlope
                && riseBlockAt( rabbit.x, rabbit.y )
            );
        }

        private boolean lowering()
        {
            return (
                   rabbit.onSlope
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
    public boolean checkTriggered( Rabbit rabbit, World world )
    {
        return false; // To avoid cancelling other behaviours, return false
    }

    @Override
    public State newState( Rabbit rabbit, World world, boolean triggered )
    {
        return new StateCalc( rabbit, world ).newState();
    }

    @Override
    public boolean behave( World world, Rabbit rabbit, State state )
    {
        switch ( state )
        {
            case RABBIT_WALKING_LEFT:
            {
                --rabbit.x;
                rabbit.onSlope = false;
                return true;
            }
            case RABBIT_WALKING_RIGHT:
            {
                ++rabbit.x;
                rabbit.onSlope = false;
                return true;
            }
            case RABBIT_LOWERING_LEFT_END:
            {
                --rabbit.x;
                rabbit.onSlope = false;
                return true;
            }
            case RABBIT_RISING_LEFT_START:
            case RABBIT_LOWERING_AND_RISING_LEFT:
            case RABBIT_RISING_AND_LOWERING_LEFT:
            {
                --rabbit.x;
                rabbit.onSlope = true;
                return true;
            }
            case RABBIT_LOWERING_RIGHT_END:
            {
                ++rabbit.x;
                rabbit.onSlope = false;
                return true;
            }
            case RABBIT_RISING_RIGHT_START:
            case RABBIT_LOWERING_AND_RISING_RIGHT:
            case RABBIT_RISING_AND_LOWERING_RIGHT:
            {
                ++rabbit.x;
                rabbit.onSlope = true;
                return true;
            }
            case RABBIT_RISING_LEFT_END:
            {
                --rabbit.y;
                --rabbit.x;
                rabbit.onSlope = false;
                return true;
            }
            case RABBIT_RISING_LEFT_CONTINUE:
            {
                --rabbit.y;
                --rabbit.x;
                rabbit.onSlope = true;
                return true;
            }
            case RABBIT_RISING_RIGHT_END:
            {
                --rabbit.y;
                ++rabbit.x;
                rabbit.onSlope = false;
                return true;
            }
            case RABBIT_RISING_RIGHT_CONTINUE:
            {
                --rabbit.y;
                ++rabbit.x;
                rabbit.onSlope = true;
                return true;
            }
            case RABBIT_LOWERING_LEFT_CONTINUE:
            case RABBIT_LOWERING_LEFT_START:
            {
                ++rabbit.y;
                --rabbit.x;
                rabbit.onSlope = true;
                return true;
            }
            case RABBIT_LOWERING_RIGHT_CONTINUE:
            case RABBIT_LOWERING_RIGHT_START:
            {
                ++rabbit.y;
                ++rabbit.x;
                rabbit.onSlope = true;
                return true;
            }
            case RABBIT_TURNING_LEFT_TO_RIGHT:
            case RABBIT_TURNING_LEFT_TO_RIGHT_RISING:
            case RABBIT_TURNING_LEFT_TO_RIGHT_LOWERING:
            {
                rabbit.dir = RIGHT;
                checkJumpOntoSlope( world, rabbit );
                return true;
            }
            case RABBIT_TURNING_RIGHT_TO_LEFT:
            case RABBIT_TURNING_RIGHT_TO_LEFT_RISING:
            case RABBIT_TURNING_RIGHT_TO_LEFT_LOWERING:
            {
                rabbit.dir = LEFT;
                checkJumpOntoSlope( world, rabbit );
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

    /**
     * If we turn around near a slope, we jump onto it
     */
    private void checkJumpOntoSlope( World world, Rabbit rabbit )
    {
        Block thisBlock = world.getBlockAt( rabbit.x, rabbit.y );
        if ( isBridge( thisBlock ) )
        {
            Block aboveBlock = world.getBlockAt( rabbit.x, rabbit.y - 1 );
            if ( rabbit.onSlope && isBridge( aboveBlock ) )
            {
                rabbit.y--;
            }
            else
            {
                rabbit.onSlope = true;
            }
        }
    }

    private boolean isBridge( Block block )
    {
        return (
               block != null
            && (
                   block.type == bridge_up_left
                || block.type == bridge_up_right
            )
        );
    }
}

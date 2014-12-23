package rabbitescape.engine;

import static rabbitescape.engine.ChangeDescription.State.*;
import static rabbitescape.engine.Token.Type.*;
import static rabbitescape.engine.Block.Type.*;

import java.util.Map;

import rabbitescape.engine.Block.Type;
import rabbitescape.engine.ChangeDescription.State;

public class Bridging implements Behaviour
{
    enum BridgeType
    {
        ALONG,
        UP,
        DOWN_UP
    }

    private int smallSteps = 0;
    private int bigSteps = 0;
    private BridgeType bridgeType = BridgeType.ALONG;

    private final Climbing climbing;

    public Bridging( Climbing climbing )
    {
        this.climbing = climbing;
    }

    @Override
    public State newState( Rabbit rabbit, World world )
    {
        if ( climbing.abilityActive )
        {
            return null;
        }

        nextStep();

        if ( bigSteps <= 0 )
            // Only pick up a token if we've finished, and we can bridge
        {
            State possibleState = bridgingState(
                rabbit, world, 3, 3, bridgeType );

            if ( possibleState != null )
            {
                checkForToken( rabbit, world );
            }
        }

        State ret = bridgingState(
            rabbit, world, bigSteps, smallSteps, bridgeType );

        if ( ret == null )
        {
            bigSteps = 0;
        }

        if ( bigSteps <= 0 )
        {
            smallSteps = 0;
            return null;   // Finished bridging
        }

        return ret;
    }

    private static State bridgingState(
        Rabbit rabbit, World world, int bs, int ss, BridgeType bt )
    {
        Block hereBlock = world.getBlockAt( rabbit.x, rabbit.y );

        if ( startingIntoToWall( world, rabbit, bs ) )
        {
            return stateIntoWall( rabbit, world, ss );
        }

        boolean slopeUp = isSlopeUp( rabbit, hereBlock );
        int nx = nextX( rabbit );
        int ny = nextY( rabbit, slopeUp );

        Block nextBlock = world.getBlockAt( nx, ny );

        Block belowNextBlock = world.getBlockAt( nx, rabbit.y );
        Block twoAboveHereBlock = world.getBlockAt( rabbit.x, rabbit.y - 2 );
        Block aboveNextBlock = world.getBlockAt( nx, ny - 1 );

        if (
            (
                   // Something in the way
                   nextBlock != null
                && nextBlock.riseDir() != rabbit.dir
            ) || (
                   // Clip land
                   belowNextBlock != null
                && belowNextBlock.riseDir() != rabbit.dir
            ) || (
                    // Bang head next
                    aboveNextBlock != null
                 && isSolid( aboveNextBlock )
            ) || (
                    // Bang head here, mid-build
                    bs < 3
                 && twoAboveHereBlock != null
                 && twoAboveHereBlock.type == Block.Type.solid_flat
            )
        )
        {
            return null; // Stop bridging
        }

        boolean slopeDown = (
               ( hereBlock != null )
            && ( hereBlock.riseDir() == Direction.opposite( rabbit.dir ) )
        );

        switch( ss )
        {
            case 3:
            {
                if ( slopeUp )
                {
                    return BehaviourTools.rl(
                        rabbit,
                        RABBIT_BRIDGING_UP_RIGHT_1,
                        RABBIT_BRIDGING_UP_LEFT_1
                    );
                }
                else if ( slopeDown )
                {
                    return BehaviourTools.rl(
                        rabbit,
                        RABBIT_BRIDGING_DOWN_UP_RIGHT_1,
                        RABBIT_BRIDGING_DOWN_UP_LEFT_1
                    );
                }
                else
                {
                    return BehaviourTools.rl(
                        rabbit,
                        RABBIT_BRIDGING_RIGHT_1,
                        RABBIT_BRIDGING_LEFT_1
                    );
                }
            }
            case 2:
            {
                switch( bt )
                {
                    case ALONG:
                    {
                        return BehaviourTools.rl(
                            rabbit,
                            RABBIT_BRIDGING_RIGHT_2,
                            RABBIT_BRIDGING_LEFT_2
                        );
                    }
                    case UP:
                    {
                        return BehaviourTools.rl(
                            rabbit,
                            RABBIT_BRIDGING_UP_RIGHT_2,
                            RABBIT_BRIDGING_UP_LEFT_2
                        );
                    }
                    case DOWN_UP:
                    {
                        return BehaviourTools.rl(
                            rabbit,
                            RABBIT_BRIDGING_DOWN_UP_RIGHT_2,
                            RABBIT_BRIDGING_DOWN_UP_LEFT_2
                        );
                    }
                }
            }
            case 1:
            {
                switch( bt )
                {
                    case ALONG:
                    {
                        return BehaviourTools.rl(
                            rabbit,
                            RABBIT_BRIDGING_RIGHT_3,
                            RABBIT_BRIDGING_LEFT_3
                        );
                    }
                    case UP:
                    {
                        return BehaviourTools.rl(
                            rabbit,
                            RABBIT_BRIDGING_UP_RIGHT_3,
                            RABBIT_BRIDGING_UP_LEFT_3
                        );
                    }
                    case DOWN_UP:
                    {
                        return BehaviourTools.rl(
                            rabbit,
                            RABBIT_BRIDGING_DOWN_UP_RIGHT_3,
                            RABBIT_BRIDGING_DOWN_UP_LEFT_3
                        );
                    }
                }
            }
            default:
            {
                return null;
            }
        }
    }

    private static State stateIntoWall( Rabbit rabbit, World world, int ss )
    {
        Block thisBlock = world.getBlockAt( rabbit.x, rabbit.y );

        boolean slopeUp = isSlopeUp( rabbit, thisBlock );
        int bx = behindX( rabbit );
        int ny = nextY( rabbit, slopeUp );

        if ( isSlope( thisBlock ) && world.getBlockAt( bx, ny ) == null )
        {
            return null;
        }

        switch( ss )
        {
            case 3:
            {
                if ( isSlope( thisBlock ) )
                {
                    return BehaviourTools.rl(
                        rabbit,
                        RABBIT_BRIDGING_IN_CORNER_UP_RIGHT_1,
                        RABBIT_BRIDGING_IN_CORNER_UP_LEFT_1
                    );
                }
                else
                {
                    return BehaviourTools.rl(
                        rabbit,
                        RABBIT_BRIDGING_IN_CORNER_RIGHT_1,
                        RABBIT_BRIDGING_IN_CORNER_LEFT_1
                    );
                }
            }
            case 2:
            {
                if ( isSlope( thisBlock ) )
                {
                    return BehaviourTools.rl(
                        rabbit,
                        RABBIT_BRIDGING_IN_CORNER_UP_RIGHT_2,
                        RABBIT_BRIDGING_IN_CORNER_UP_LEFT_2
                    );
                }
                else
                {
                    return BehaviourTools.rl(
                        rabbit,
                        RABBIT_BRIDGING_IN_CORNER_RIGHT_2,
                        RABBIT_BRIDGING_IN_CORNER_LEFT_2
                    );
                }
            }
            case 1:
            {
                if ( isSlope( thisBlock ) )
                {
                    return BehaviourTools.rl(
                        rabbit,
                        RABBIT_BRIDGING_IN_CORNER_UP_RIGHT_3,
                        RABBIT_BRIDGING_IN_CORNER_UP_LEFT_3
                    );
                }
                else
                {
                    return BehaviourTools.rl(
                        rabbit,
                        RABBIT_BRIDGING_IN_CORNER_RIGHT_3,
                        RABBIT_BRIDGING_IN_CORNER_LEFT_3
                    );
                }
            }
            default:
            {
                return null;
            }
        }
    }

    private static boolean startingIntoToWall(
        World world,
        Rabbit rabbit,
        int bs
    )
    {
        Block hereBlock = world.getBlockAt( rabbit.x, rabbit.y );

        boolean slopeUp = isSlopeUp( rabbit, hereBlock );
        int nx = nextX( rabbit );
        int ny = nextY( rabbit, slopeUp );

        Block nextBlock = world.getBlockAt( nx, ny );

        return (
           bs == 3
        )
        &&
        (
               nextBlock != null
            &&
            (
                   nextBlock.riseDir() != rabbit.dir
                || nextBlock.type == solid_flat
            )
         );
    }

    private static boolean isSlopeUp( Rabbit rabbit, Block hereBlock )
    {
        return ( hereBlock != null )
          && ( hereBlock.riseDir() == rabbit.dir );
    }

    private static int nextY( Rabbit rabbit, boolean slopeUp )
    {
        int ret = rabbit.y;
        ret += slopeUp ? -1 : 0;
        return ret;
    }

    private static int nextX( Rabbit rabbit )
    {
        int ret = rabbit.x;
        ret += rabbit.dir == Direction.RIGHT ? 1 : -1;
        return ret;
    }

    private static int behindX( Rabbit rabbit )
    {
        int ret = rabbit.x;
        ret += rabbit.dir == Direction.RIGHT ? -1 : 1;
        return ret;
    }

    private void checkForToken( Rabbit rabbit, World world )
    {
        Token token = world.getTokenAt( rabbit.x, rabbit.y );
        if ( token != null && token.type == bridge )
        {
            world.changes.removeToken( token );
            smallSteps = 3;
            bigSteps = 3;
        }
    }

    private void nextStep()
    {
        --smallSteps;
        if ( smallSteps <= 0 )
        {
            --bigSteps;
            smallSteps = 3;
        }
    }

    private static boolean isSlope( Block thisBlock )
    {
        return ( thisBlock != null && thisBlock.type != solid_flat );
    }

    private static boolean isSolid( Block block )
    {
        return (
               block.type == Type.solid_flat
            || block.type == Type.solid_up_left
            || block.type == Type.solid_up_right
        );
    }

    @Override
    public boolean behave( World world, Rabbit rabbit, State state )
    {
        boolean handled = moveRabbit( world, rabbit, state );

        if ( handled )
        {
            // If we're bridging, we're on a slope
            rabbit.onSlope = true;
        }

        return handled;
    }

    private boolean moveRabbit( World world, Rabbit rabbit, State state )
    {
        switch ( state )
        {
            case RABBIT_BRIDGING_RIGHT_1:
            case RABBIT_BRIDGING_RIGHT_2:
            case RABBIT_BRIDGING_LEFT_1:
            case RABBIT_BRIDGING_LEFT_2:
            {
                bridgeType = BridgeType.ALONG;
                return true;
            }
            case RABBIT_BRIDGING_UP_RIGHT_1:
            case RABBIT_BRIDGING_UP_RIGHT_2:
            case RABBIT_BRIDGING_UP_LEFT_1:
            case RABBIT_BRIDGING_UP_LEFT_2:
            {
                bridgeType = BridgeType.UP;
                return true;
            }
            case RABBIT_BRIDGING_DOWN_UP_RIGHT_1:
            case RABBIT_BRIDGING_DOWN_UP_RIGHT_2:
            case RABBIT_BRIDGING_DOWN_UP_LEFT_1:
            case RABBIT_BRIDGING_DOWN_UP_LEFT_2:
            {
                bridgeType = BridgeType.DOWN_UP;
                return true;
            }
            case RABBIT_BRIDGING_IN_CORNER_RIGHT_1:
            case RABBIT_BRIDGING_IN_CORNER_LEFT_1:
            case RABBIT_BRIDGING_IN_CORNER_RIGHT_2:
            case RABBIT_BRIDGING_IN_CORNER_LEFT_2:
            case RABBIT_BRIDGING_IN_CORNER_UP_RIGHT_1:
            case RABBIT_BRIDGING_IN_CORNER_UP_LEFT_1:
            case RABBIT_BRIDGING_IN_CORNER_UP_RIGHT_2:
            case RABBIT_BRIDGING_IN_CORNER_UP_LEFT_2:
            {
                bridgeType = BridgeType.ALONG;
                return true;
            }
            case RABBIT_BRIDGING_RIGHT_3:
            case RABBIT_BRIDGING_DOWN_UP_RIGHT_3:
            {
                rabbit.x++;
                world.changes.addBlock(
                    new Block(
                        rabbit.x,
                        rabbit.y,
                        Block.Type.bridge_up_right
                    )
                );

                return true;
            }
            case RABBIT_BRIDGING_LEFT_3:
            case RABBIT_BRIDGING_DOWN_UP_LEFT_3:
            {
                rabbit.x--;
                world.changes.addBlock(
                    new Block(
                        rabbit.x,
                        rabbit.y,
                        Block.Type.bridge_up_left
                    )
                );

                return true;
            }
            case RABBIT_BRIDGING_UP_RIGHT_3:
            {
                rabbit.x++;
                rabbit.y--;
                world.changes.addBlock(
                    new Block(
                        rabbit.x,
                        rabbit.y,
                        Block.Type.bridge_up_right
                    )
                );

                return true;
            }
            case RABBIT_BRIDGING_UP_LEFT_3:
            {
                rabbit.x--;
                rabbit.y--;
                world.changes.addBlock(
                    new Block(
                        rabbit.x,
                        rabbit.y,
                        Block.Type.bridge_up_left
                    )
                );

                return true;
            }
            case RABBIT_BRIDGING_IN_CORNER_RIGHT_3:
            {
                rabbit.onSlope = true;
                world.changes.addBlock(
                    new Block(
                        rabbit.x,
                        rabbit.y,
                        Block.Type.bridge_up_right
                    )
                );
                return true;
            }
            case RABBIT_BRIDGING_IN_CORNER_LEFT_3:
            {
                rabbit.onSlope = true;
                world.changes.addBlock(
                    new Block(
                        rabbit.x,
                        rabbit.y,
                        Block.Type.bridge_up_left
                    )
                );
                return true;
            }
            case RABBIT_BRIDGING_IN_CORNER_UP_RIGHT_3:
            {
                rabbit.onSlope = true;
                rabbit.y--;
                world.changes.addBlock(
                    new Block(
                        rabbit.x,
                        rabbit.y,
                        Block.Type.bridge_up_right
                    )
                );
                return true;
            }
            case RABBIT_BRIDGING_IN_CORNER_UP_LEFT_3:
            {
                rabbit.onSlope = true;
                rabbit.y--;
                world.changes.addBlock(
                    new Block(
                        rabbit.x,
                        rabbit.y,
                        Block.Type.bridge_up_left
                    )
                );
                return true;
            }
            default:
            {
                return false;
            }
        }
    }

    public void stopBridging()
    {
        bigSteps = 0;
        smallSteps = 0;
    }

    @Override
    public void saveState( Map<String, String> saveState )
    {
        BehaviourTools.addToStateIfNotDefault(
            saveState,
            "Bridging.bridgeType",
            bridgeType.toString(),
            BridgeType.ALONG.toString()
        );

        BehaviourTools.addToStateIfGtZero(
            saveState, "Bridging.bigSteps", bigSteps );

        BehaviourTools.addToStateIfGtZero(
            saveState, "Bridging.smallSteps", smallSteps );
    }

    @Override
    public void restoreFromState( Map<String, String> saveState )
    {
        bridgeType = BridgeType.valueOf(
            BehaviourTools.restoreFromState(
                saveState, "Bridging.bridgeType", bridgeType.toString() )
        );

        bigSteps = BehaviourTools.restoreFromState(
            saveState, "Bridging.bigSteps", bigSteps );

        smallSteps = BehaviourTools.restoreFromState(
            saveState, "Bridging.smallSteps", smallSteps );

        if ( smallSteps > 0 )
        {
            ++smallSteps;
        }
    }
}

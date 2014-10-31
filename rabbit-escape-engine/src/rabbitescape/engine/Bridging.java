package rabbitescape.engine;

import static rabbitescape.engine.ChangeDescription.State.*;
import static rabbitescape.engine.Token.Type.*;

import java.util.Map;

import rabbitescape.engine.ChangeDescription.State;

public class Bridging implements Behaviour
{
    private int smallSteps = 0;
    private int bigSteps = 0;

    @Override
    public State newState( Rabbit rabbit, World world )
    {
        Token token = world.getTokenAt( rabbit.x, rabbit.y );
        if ( token != null && token.type == bridge )
        {
            world.changes.removeToken( token );
            smallSteps = 4;
            bigSteps = 3;
        }

        --smallSteps;

        if ( smallSteps <= 0 )
        {
            --bigSteps;
            smallSteps = 3;
        }

        if ( bigSteps <= 0 )
        {
            smallSteps = 0;
            return null;
        }

        Block hereBlock = world.getBlockAt( rabbit.x, rabbit.y );

        boolean slopeUp = (
               ( hereBlock != null )
            && ( hereBlock.riseDir() == rabbit.dir )
        );

        int nextX = rabbit.x;
        nextX += rabbit.dir == Direction.RIGHT ? 1 : -1;

        int nextY = rabbit.y;
        nextY += slopeUp ? -1 : 0;

        Block nextBlock = world.getBlockAt( nextX, nextY );

        if ( nextBlock != null && nextBlock.riseDir() != rabbit.dir )
        {
            bigSteps = 0;
            return null; // We will be turning around - stop bridging
        }

        Block belowNextBlock = world.getBlockAt( nextX, rabbit.y );
        if (
               belowNextBlock != null
            && belowNextBlock.type == Block.Type.solid_flat
        )
        {
            bigSteps = 0;
            return null; // We skimmed the top of a wall - stop bridging
        }

        boolean slopeDown = (
               ( hereBlock != null )
            && ( hereBlock.riseDir() == Direction.opposite( rabbit.dir ) )
        );

        switch( smallSteps )
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
                if ( slopeUp )
                {
                    return BehaviourTools.rl(
                        rabbit,
                        RABBIT_BRIDGING_UP_RIGHT_2,
                        RABBIT_BRIDGING_UP_LEFT_2
                    );
                }
                else if ( slopeDown )
                {
                    return BehaviourTools.rl(
                        rabbit,
                        RABBIT_BRIDGING_DOWN_UP_RIGHT_2,
                        RABBIT_BRIDGING_DOWN_UP_LEFT_2
                    );
                }
                else
                {
                    return BehaviourTools.rl(
                        rabbit,
                        RABBIT_BRIDGING_RIGHT_2,
                        RABBIT_BRIDGING_LEFT_2
                    );
                }
            }
            case 1:
            {
                if ( slopeUp )
                {
                    return BehaviourTools.rl(
                        rabbit,
                        RABBIT_BRIDGING_UP_RIGHT_3,
                        RABBIT_BRIDGING_UP_LEFT_3
                    );
                }
                else if ( slopeDown )
                {
                    return BehaviourTools.rl(
                        rabbit,
                        RABBIT_BRIDGING_DOWN_UP_RIGHT_3,
                        RABBIT_BRIDGING_DOWN_UP_LEFT_3
                    );
                }
                else
                {
                    return BehaviourTools.rl(
                        rabbit,
                        RABBIT_BRIDGING_RIGHT_3,
                        RABBIT_BRIDGING_LEFT_3
                    );
                }
            }
            default:
            {
                return null;
            }
        }
    }

    @Override
    public boolean behave( World world, Rabbit rabbit, State state )
    {
        switch ( state )
        {
            case RABBIT_BRIDGING_RIGHT_1:
            case RABBIT_BRIDGING_RIGHT_2:
            case RABBIT_BRIDGING_LEFT_1:
            case RABBIT_BRIDGING_LEFT_2:
            case RABBIT_BRIDGING_UP_RIGHT_1:
            case RABBIT_BRIDGING_UP_RIGHT_2:
            case RABBIT_BRIDGING_UP_LEFT_1:
            case RABBIT_BRIDGING_UP_LEFT_2:
            case RABBIT_BRIDGING_DOWN_UP_RIGHT_1:
            case RABBIT_BRIDGING_DOWN_UP_RIGHT_2:
            case RABBIT_BRIDGING_DOWN_UP_LEFT_1:
            case RABBIT_BRIDGING_DOWN_UP_LEFT_2:
            {
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
        BehaviourTools.addToStateIfGtZero(
            saveState, "Bridging.bigSteps", bigSteps );

        BehaviourTools.addToStateIfGtZero(
            saveState, "Bridging.smallSteps", smallSteps );
    }

    @Override
    public void restoreFromState( Map<String, String> saveState )
    {
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

package rabbitescape.engine;

import static rabbitescape.engine.ChangeDescription.State.*;
import static rabbitescape.engine.Token.Type.*;

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
            world.removeThing( token );
            smallSteps = 5;
            bigSteps = 3;
        }

        --smallSteps;

        if ( smallSteps <= 0 )
        {
            --bigSteps;
            smallSteps = 4;
        }

        if ( bigSteps <= 0 )
        {
            return null;
        }

        Block hereBlock = world.getBlockAt( rabbit.x, rabbit.y );

        boolean slope = (
               ( hereBlock != null )
            && ( hereBlock.riseDir() != Direction.DOWN )
        );

        switch( smallSteps )
        {
            case 4:
            {
                if ( slope )
                {
                    return BehaviourTools.rl(
                        rabbit,
                        RABBIT_BRIDGING_UP_RIGHT_1,
                        RABBIT_BRIDGING_UP_LEFT_1
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
            case 3:
            {
                if ( slope )
                {
                    return BehaviourTools.rl(
                        rabbit,
                        RABBIT_BRIDGING_UP_RIGHT_2,
                        RABBIT_BRIDGING_UP_LEFT_2
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
            case 2:
            {
                if ( slope )
                {
                    return BehaviourTools.rl(
                        rabbit,
                        RABBIT_BRIDGING_UP_RIGHT_3,
                        RABBIT_BRIDGING_UP_LEFT_3
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
            default: // We do nothing for case 1 - just step up the bridge
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
            {
                return true;
            }
            case RABBIT_BRIDGING_RIGHT_3:
            {
                world.changes.blocksToAdd.add(
                    new Block(
                        rabbit.x + 1,
                        rabbit.y,
                        Block.Type.bridge_up_right
                    )
                );

                return true;
            }
            case RABBIT_BRIDGING_LEFT_3:
            {
                world.changes.blocksToAdd.add(
                    new Block(
                        rabbit.x - 1,
                        rabbit.y,
                        Block.Type.bridge_up_left
                    )
                );

                return true;
            }
            case RABBIT_BRIDGING_UP_RIGHT_3:
            {
                world.changes.blocksToAdd.add(
                    new Block(
                        rabbit.x + 1,
                        rabbit.y - 1,
                        Block.Type.bridge_up_right
                    )
                );

                return true;
            }
            case RABBIT_BRIDGING_UP_LEFT_3:
            {
                world.changes.blocksToAdd.add(
                    new Block(
                        rabbit.x - 1,
                        rabbit.y - 1,
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
}

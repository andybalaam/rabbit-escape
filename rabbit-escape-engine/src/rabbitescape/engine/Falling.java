package rabbitescape.engine;

import static rabbitescape.engine.BehaviourTools.*;
import static rabbitescape.engine.ChangeDescription.State.*;
import static rabbitescape.engine.Block.Type.*;

import rabbitescape.engine.ChangeDescription.State;

public class Falling implements Behaviour
{
    private static final int fatalHeight = 4;

    private int heightFallen = 0;

    private final Digging digging;

    /**
     * @param digging a Digging to cancel if we fall >1 block
     */
    public Falling( Digging digging )
    {
        this.digging = digging;
    }

    @Override
    public boolean behave( World world, Rabbit rabbit, State state )
    {
        switch ( state )
        {
            case RABBIT_DYING_OF_FALLING:
            {
                world.changes.killRabbit( rabbit );
                return true;
            }
            case RABBIT_DYING_OF_FALLING_2:
            {
                world.changes.killRabbit( rabbit );
                return true;
            }
            case RABBIT_FALLING:
            case RABBIT_FALLING_ONTO_LOWER_RIGHT:
            case RABBIT_FALLING_ONTO_LOWER_LEFT:
            case RABBIT_FALLING_ONTO_RISE_RIGHT:
            case RABBIT_FALLING_ONTO_RISE_LEFT:
            {
                digging.stopDigging();
                heightFallen += 2;
                rabbit.y = rabbit.y + 2;
                return true;
            }
            case RABBIT_FALLING_1_TO_DEATH:
            case RABBIT_FALLING_1:
            case RABBIT_FALLING_1_ONTO_LOWER_RIGHT:
            case RABBIT_FALLING_1_ONTO_LOWER_LEFT:
            case RABBIT_FALLING_1_ONTO_RISE_RIGHT:
            case RABBIT_FALLING_1_ONTO_RISE_LEFT:
            {
                heightFallen += 1;
                rabbit.y = rabbit.y + 1;
                return true;
            }
            default:
            {
                heightFallen = 0;
                return false;
            }
        }
    }

    @Override
    public State newState( Rabbit rabbit, World world )
    {
        if ( !falling( rabbit, world ) )
        {
            if ( heightFallen > fatalHeight )
            {
                if ( heightFallen % 2 == 0 )
                {
                    // TODO: handle falling onto slopes
                    return RABBIT_DYING_OF_FALLING;
                }
                else
                {
                    // TODO: handle falling onto slopes
                    return RABBIT_DYING_OF_FALLING_2;
                }
            }
            return null;
        }

        if (
               ( heightFallen + 1 > fatalHeight )              // Going to die
            && ( world.flatBlockAt( rabbit.x, rabbit.y + 2 ) ) // during step
        )
        {
            // TODO: handle falling onto slopes
            return State.RABBIT_FALLING_1_TO_DEATH;
        }
        else
        {
            Block block2Down = world.getBlockAt( rabbit.x, rabbit.y + 2 );
            if ( block2Down != null )
            {
                if ( block2Down.type == solid_flat ) // Flat block
                {
                    Block block1Down = world.getBlockAt(
                        rabbit.x, rabbit.y + 1 );

                    if ( block1Down == null )
                    {
                        return State.RABBIT_FALLING_1;
                    }
                    else if ( block1Down.riseDir() == rabbit.dir )
                    {
                        return rl(
                            rabbit,
                            RABBIT_FALLING_1_ONTO_RISE_RIGHT,
                            RABBIT_FALLING_1_ONTO_RISE_LEFT
                        );
                    }
                    else // Must be a slope in the opposite direction
                    {
                        return rl(
                            rabbit,
                            RABBIT_FALLING_1_ONTO_LOWER_RIGHT,
                            RABBIT_FALLING_1_ONTO_LOWER_LEFT
                        );
                    }
                }
                else if( block2Down.riseDir() == rabbit.dir )
                {
                    return rl(
                        rabbit,
                        RABBIT_FALLING_ONTO_RISE_RIGHT,
                        RABBIT_FALLING_ONTO_RISE_LEFT
                    );
                }
                else
                {
                    return rl(
                        rabbit,
                        RABBIT_FALLING_ONTO_LOWER_RIGHT,
                        RABBIT_FALLING_ONTO_LOWER_LEFT
                    );
                }
            }
            else
            {
                return State.RABBIT_FALLING;
            }
        }
    }

    boolean falling( Rabbit rabbit, World world )
    {
        int below = rabbit.y + 1;
        //noinspection RedundantIfStatement
        if (
               world.flatBlockAt( rabbit.x, below )
            || world.slopingBlockAt( rabbit.x, rabbit.y )
        )
        {
            return false;
        }

        return true;
    }
}

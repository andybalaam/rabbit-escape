package rabbitescape.engine.behaviours;

import static rabbitescape.engine.BehaviourTools.*;
import static rabbitescape.engine.ChangeDescription.State.*;
import static rabbitescape.engine.Block.Type.*;

import java.util.Map;

import rabbitescape.engine.*;
import rabbitescape.engine.ChangeDescription.State;

public class Falling extends Behaviour
{
    private static final int fatalHeight = 4;

    private int heightFallen = 0;

    private final Climbing climbing;

    public Falling( Climbing climbing )
    {
        this.climbing = climbing;
    }

    @Override
    public void cancel()
    {
    }

    @Override
    public boolean behave( World world, Rabbit rabbit, State state )
    {
        boolean handled = moveRabbit( world, rabbit, state );

        if ( handled )
        {
            // Whenever we fall onto a slope, we are on top of it
            Block thisBlock = world.getBlockAt( rabbit.x, rabbit.y );
            if ( thisBlock != null && thisBlock.type != solid_flat )
            {
                rabbit.onSlope = true;
            }
            else
            {
                rabbit.onSlope = false;
            }
        }

        return handled;
    }

    private boolean moveRabbit( World world, Rabbit rabbit, State state )
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
    public boolean checkTriggered( Rabbit rabbit, World world )
    {
        if ( climbing.abilityActive || rabbit.state == RABBIT_DIGGING )
        {
            return false;
        }

        int below = rabbit.y + 1;
        //noinspection RedundantIfStatement
        if (
               world.flatBlockAt( rabbit.x, below )
            || rabbit.onSlope
        )
        {
            return false;
        }

        return true;
    }

    @Override
    public State newState( Rabbit rabbit, World world, boolean triggered )
    {
        if ( !triggered )
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

        BehaviourTools t = new BehaviourTools( rabbit );

        if (
               ( heightFallen + 1 > fatalHeight )              // Going to die
            && (                                               // during step
                   world.flatBlockAt( rabbit.x, rabbit.y + 2 )
                || world.getBlockAt( rabbit.x, rabbit.y + 1 ) != null
            )
        )
        {
            // TODO: handle falling onto slopes
            return State.RABBIT_FALLING_1_TO_DEATH;
        }
        else
        {
            Block block1Down = world.getBlockAt(
                rabbit.x, rabbit.y + 1 );

            if ( block1Down != null )
            {
                if ( block1Down.riseDir() == rabbit.dir )
                {
                    return t.rl(
                        RABBIT_FALLING_1_ONTO_RISE_RIGHT,
                        RABBIT_FALLING_1_ONTO_RISE_LEFT
                    );
                }
                else // Must be a slope in the opposite direction
                {
                    return t.rl(
                        RABBIT_FALLING_1_ONTO_LOWER_RIGHT,
                        RABBIT_FALLING_1_ONTO_LOWER_LEFT
                    );
                }
            }

            Block block2Down = world.getBlockAt( rabbit.x, rabbit.y + 2 );
            if ( block2Down != null )
            {
                if ( block2Down.type == solid_flat ) // Flat block
                {
                    return State.RABBIT_FALLING_1;
                }
                else if( block2Down.riseDir() == rabbit.dir )
                {
                    return t.rl(
                        RABBIT_FALLING_ONTO_RISE_RIGHT,
                        RABBIT_FALLING_ONTO_RISE_LEFT
                    );
                }
                else
                {
                    return t.rl(
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

    @Override
    public void saveState( Map<String, String> saveState )
    {
        BehaviourState.addToStateIfGtZero(
            saveState, "Falling.heightFallen", heightFallen
        );
    }

    @Override
    public void restoreFromState( Map<String, String> saveState )
    {
        heightFallen = BehaviourState.restoreFromState(
            saveState, "Falling.heightFallen", heightFallen
        );
    }
}

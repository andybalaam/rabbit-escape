package rabbitescape.engine.behaviours;

import static rabbitescape.engine.ChangeDescription.State.*;
import static rabbitescape.engine.Block.Shape.*;

import java.util.Map;

import rabbitescape.engine.*;
import rabbitescape.engine.ChangeDescription.State;

public class Falling extends Behaviour
{
    private int heightFallen = 0;

    private final Climbing climbing;
    private final Brollychuting brollychuting;
    private final int fatalHeight;

    public Falling( 
        Climbing climbing, 
        Brollychuting brollychuting,
        int fatalHeight 
    )
    {
        this.climbing = climbing;
        this.brollychuting = brollychuting;
        this.fatalHeight = fatalHeight;
    }

    public boolean isFallingToDeath()
    {
        return heightFallen > fatalHeight ;
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
            if ( thisBlock != null && thisBlock.shape != FLAT )
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
            case RABBIT_DYING_OF_FALLING_2:
            case RABBIT_DYING_OF_FALLING_SLOPE_RISE_RIGHT_2:
            case RABBIT_DYING_OF_FALLING_2_SLOPE_RISE_RIGHT_2:
            case RABBIT_DYING_OF_FALLING_SLOPE_RISE_LEFT_2:
            case RABBIT_DYING_OF_FALLING_2_SLOPE_RISE_LEFT_2:
            {
                world.changes.killRabbit( rabbit );
                return true;
            }
            case RABBIT_FALLING:
            case RABBIT_FALLING_ONTO_LOWER_RIGHT:
            case RABBIT_FALLING_ONTO_LOWER_LEFT:
            case RABBIT_FALLING_ONTO_RISE_RIGHT:
            case RABBIT_FALLING_ONTO_RISE_LEFT:
            case RABBIT_DYING_OF_FALLING_2_SLOPE_RISE_RIGHT:
            case RABBIT_DYING_OF_FALLING_2_SLOPE_RISE_LEFT:
            {
                heightFallen += 2;
                rabbit.y = rabbit.y + 2;
                return true;
            }
            case RABBIT_DYING_OF_FALLING_SLOPE_RISE_RIGHT:
            case RABBIT_DYING_OF_FALLING_SLOPE_RISE_LEFT:
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
        if (   climbing.abilityActive
            || rabbit.state == RABBIT_DIGGING
            || brollychuting.hasBrolly() )
        {
            return false;
        }

        BehaviourTools t = new BehaviourTools( rabbit, world );

        //noinspection RedundantIfStatement
        if ( t.isFlat( t.blockBelow() ) )
        {
            return false;
        }

        if (
               rabbit.onSlope
            && !t.blockHereJustRemoved()
        )
        {
            return false;
        }

        return true;
    }

    @Override
    public State newState( BehaviourTools t, boolean triggered )
    {
        if ( RABBIT_DYING_OF_FALLING_SLOPE_RISE_LEFT == t.rabbit.state )
        { // part 2 of animation always comes next
            return RABBIT_DYING_OF_FALLING_SLOPE_RISE_LEFT_2;
        }

        if ( RABBIT_DYING_OF_FALLING_2_SLOPE_RISE_LEFT == t.rabbit.state )
        { // part 2 of animation always comes next
            return RABBIT_DYING_OF_FALLING_2_SLOPE_RISE_LEFT_2;
        }

        if ( RABBIT_DYING_OF_FALLING_SLOPE_RISE_RIGHT == t.rabbit.state )
        { // part 2 of animation always comes next
            return RABBIT_DYING_OF_FALLING_SLOPE_RISE_RIGHT_2;
        }

        if ( RABBIT_DYING_OF_FALLING_2_SLOPE_RISE_RIGHT == t.rabbit.state )
        {
            return RABBIT_DYING_OF_FALLING_2_SLOPE_RISE_RIGHT_2;
        }

        if ( !triggered )
        {
            if ( heightFallen > fatalHeight )
            {
                if ( heightFallen % 2 == 0 )
                {
                    return RABBIT_DYING_OF_FALLING;
                }
                else
                {
                    return RABBIT_DYING_OF_FALLING_2;
                }
            }
            return null;
        }

        if (
               ( heightFallen + 1 > fatalHeight )              // Going to die
            && (                                               // during step
                   t.isFlat( t.block2Below() )
                || t.blockBelow() != null
            )
        )
        {
            if( BehaviourTools.isRightRiseSlope( t.blockBelow() ) )
            {
                return RABBIT_DYING_OF_FALLING_SLOPE_RISE_RIGHT;
            }
            else if( BehaviourTools.isLeftRiseSlope( t.blockBelow() ) )
            {
                return RABBIT_DYING_OF_FALLING_SLOPE_RISE_LEFT;
            }
            else
            {
                return State.RABBIT_FALLING_1_TO_DEATH;
            }
        }
        else
        {
            Block below = t.blockBelow();

            if ( below != null )
            {
                if ( t.isUpSlope( below ) )
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

            Block twoBelow = t.block2Below();
            if ( twoBelow != null )
            {
                if (   heightFallen + 1 > fatalHeight
                    && BehaviourTools.isRightRiseSlope( twoBelow ) )
                {
                    return RABBIT_DYING_OF_FALLING_2_SLOPE_RISE_RIGHT;
                }
                if (   heightFallen + 1 > fatalHeight
                    && BehaviourTools.isLeftRiseSlope( twoBelow ) )
                {
                    return RABBIT_DYING_OF_FALLING_2_SLOPE_RISE_LEFT;
                }
                if ( t.isFlat( twoBelow ) ) // Flat block
                {
                    return State.RABBIT_FALLING_1;
                }
                if( t.isUpSlope( twoBelow ) )
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

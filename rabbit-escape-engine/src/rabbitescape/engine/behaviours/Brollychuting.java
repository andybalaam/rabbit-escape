package rabbitescape.engine.behaviours;

import static rabbitescape.engine.ChangeDescription.State.*;
import static rabbitescape.engine.Token.Type.brolly;

import java.util.Map;

import rabbitescape.engine.Behaviour;
import rabbitescape.engine.BehaviourState;
import rabbitescape.engine.BehaviourTools;
import rabbitescape.engine.Block;
import rabbitescape.engine.ChangeDescription.State;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.World;

public class Brollychuting extends Behaviour
{
    boolean hasAbility = false;
    private final Climbing climbing;

    public Brollychuting( Climbing climbing)
    {
        this.climbing = climbing;
    }

    @Override
    public State newState( BehaviourTools t, boolean triggered )
    {
        if ( triggered )
        {
            hasAbility = true;
        }

        if( !hasAbility )
        {
            return null;
        }

        if ( climbing.abilityActive )
        {
            return null;
        }

        Block below = t.blockBelow();

        if ( t.isFlat( below ) )
        {
            return null;
        }

        if (
            t.rabbit.onSlope
         && !t.blockHereJustRemoved()
        )
        {
            return null;
        }

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

        return RABBIT_BROLLYCHUTING;
    }

    @Override
    public boolean behave( World world, Rabbit rabbit, State state )
    {
        if ( state == RABBIT_BROLLYCHUTING )
        {
            rabbit.y = rabbit.y + 1;
            return true;
        }
        return false;
    }

    public boolean hasBrolly()
    {
        return hasAbility;
    }

    @Override
    public boolean checkTriggered( Rabbit rabbit, World world )
    {
        BehaviourTools t = new BehaviourTools( rabbit, world );

        if ( !hasAbility && t.pickUpToken( brolly, true ) )
        {
            return true;
        }

        if( !hasAbility )
        {
            return false;
        }

        if ( climbing.abilityActive || rabbit.state == RABBIT_DIGGING )
        {
            return false;
        }

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
    public void cancel()
    {
    }

    @Override
    public void saveState( Map<String, String> saveState )
    {
        BehaviourState.addToStateIfTrue(
            saveState, "Brollychuting.hasAbility", hasAbility
        );

    }

    @Override
    public void restoreFromState( Map<String, String> saveState )
    {
        hasAbility = BehaviourState.restoreFromState(
            saveState, "Brollychuting.hasAbility", hasAbility
        );

    }
}

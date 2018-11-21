package rabbitescape.engine.behaviours;

import static rabbitescape.engine.ChangeDescription.State.*;
import static rabbitescape.engine.Token.Type.*;

import java.util.Map;

import rabbitescape.engine.*;
import rabbitescape.engine.ChangeDescription.State;

public class Blocking extends Behaviour
{
    public boolean abilityActive = false;
    private boolean wasBasher = false;

    @Override
    public void cancel()
    {
        abilityActive = false;
    }

    @Override
    public boolean checkTriggered( Rabbit rabbit, World world )
    {
        BehaviourTools t = new BehaviourTools( rabbit, world );
        wasBasher = t.rabbitIsBashing();
        return t.pickUpToken( block );
    }

    @Override
    public State newState( BehaviourTools t, boolean triggered )
    {
        if ( abilityActive || triggered )
        {
            abilityActive = true;
            if ( wasBasher )
            {
                // Bashing from a slope makes them hop up a square,
                // this needs reversing when they change to a blocker.
                Block below = t.blockBelow();
                if ( BehaviourTools.isSlope( below ) )
                {
                    ++t.rabbit.y;
                }
                wasBasher = false;
            }
            Block here = t.blockHere();
            if( BehaviourTools.isRightRiseSlope( here ) )
            {
                return RABBIT_BLOCKING_RISE_RIGHT;
            }
            else if ( BehaviourTools.isLeftRiseSlope( here ) )
            {
                return RABBIT_BLOCKING_RISE_LEFT;
            }
            else
            {
                return RABBIT_BLOCKING;
            }
        }

        return null;
    }

    @Override
    public boolean behave( World world, Rabbit rabbit, State state )
    {
        return isBlocking( state );
    }

    @Override
    public void saveState( Map<String, String> saveState )
    {
        BehaviourState.addToStateIfTrue(
            saveState, "Blocking.abilityActive", abilityActive
        );
    }

    @Override
    public void restoreFromState( Map<String, String> saveState )
    {
        abilityActive = BehaviourState.restoreFromState(
            saveState, "Blocking.abilityActive", abilityActive
        );
    }

    public static boolean blockerAt( World world, int nextX, int nextY )
    {
        Rabbit[] rabbits = world.getRabbitsAt( nextX, nextY );
        for ( Rabbit r : rabbits )
        {
            if ( isBlocking( r.state ) )
            {
                return true;
            }
        }
        return false;
    }

    static boolean isBlocking( State s )
    {
        switch ( s ) {
        case RABBIT_BLOCKING:
        case RABBIT_BLOCKING_RISE_RIGHT:
        case RABBIT_BLOCKING_RISE_LEFT:
            return true;
        default:
            return false;
        }
    }
}

package rabbitescape.engine.behaviours;

import static rabbitescape.engine.ChangeDescription.State.*;
import static rabbitescape.engine.Token.Type.*;

import rabbitescape.engine.*;
import rabbitescape.engine.ChangeDescription.State;

public class Digging extends Behaviour
{
    private final Climbing climbing;

    public Digging( Climbing climbing )
    {
        this.climbing = climbing;
    }

    @Override
    public void cancel()
    {
    }

    @Override
    public boolean checkTriggered( Rabbit rabbit, World world )
    {
        if ( climbing.abilityActive )
        {
            return false;
        }

        BehaviourTools t = new BehaviourTools( rabbit, world );
        return t.pickUpToken( dig );
    }

    @Override
    public State newState( BehaviourTools t, boolean triggered )
    {
        if ( t.rabbit.state == RABBIT_DIGGING )
        {
            return RABBIT_DIGGING_2;
        }

        if (
               triggered
            || t.rabbit.state == RABBIT_DIGGING_2
            || t.rabbit.state == RABBIT_DIGGING_ON_SLOPE
        )
        {
            if ( t.rabbit.onSlope && t.blockHere() != null )
            {
                return RABBIT_DIGGING_ON_SLOPE;
            }
            else if ( t.blockBelow() != null )
            {
                return RABBIT_DIGGING;
            }
        }

        return null;
    }

    @Override
    public boolean behave( World world, Rabbit rabbit, State state )
    {
        switch ( state )
        {
            case RABBIT_DIGGING:
            {
                world.changes.removeBlockAt( rabbit.x, rabbit.y + 1 );
                ++rabbit.y;
                return true;
            }
            case RABBIT_DIGGING_ON_SLOPE:
            {
                world.changes.removeBlockAt( rabbit.x, rabbit.y );
                rabbit.onSlope = false;
                return true;
            }
            case RABBIT_DIGGING_2:
            {
                return true;
            }
            default:
            {
                return false;
            }
        }
    }
}

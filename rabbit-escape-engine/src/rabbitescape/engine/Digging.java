package rabbitescape.engine;

import static rabbitescape.engine.ChangeDescription.State.*;
import static rabbitescape.engine.Token.Type.*;

import java.util.Map;

import rabbitescape.engine.ChangeDescription.State;

public class Digging implements Behaviour
{
    private int stepsOfDigging;

    @Override
    public State newState( Rabbit rabbit, World world )
    {
        boolean justPickedUpToken = false;

        Token token = world.getTokenAt( rabbit.x, rabbit.y );
        if ( token != null && token.type == dig )
        {
            world.changes.removeToken( token );
            justPickedUpToken = true;
        }

        if ( justPickedUpToken || stepsOfDigging > 0 )
        {
            if (
                   rabbit.onSlope
                && world.getBlockAt( rabbit.x, rabbit.y ) != null
            )
            {
                stepsOfDigging = 2;
                return RABBIT_DIGGING_ON_SLOPE;
            }
            else if ( world.getBlockAt( rabbit.x, rabbit.y + 1 ) != null )
            {
                stepsOfDigging = 2;
                return RABBIT_DIGGING;
            }
        }

        --stepsOfDigging;
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
                return true;
            }
            case RABBIT_DIGGING_ON_SLOPE:
            {
                world.changes.removeBlockAt( rabbit.x, rabbit.y );
                rabbit.onSlope = false;
                return true;
            }
            default:
            {
                return false;
            }
        }
    }

    public void stopDigging()
    {
        stepsOfDigging = 0;
    }

    @Override
    public void saveState( Map<String, String> saveState )
    {
        BehaviourTools.addToStateIfGtZero(
            saveState, "Digging.stepsOfDigging", stepsOfDigging );
    }

    @Override
    public void restoreFromState( Map<String, String> saveState )
    {
        stepsOfDigging = BehaviourTools.restoreFromState(
            saveState, "Digging.stepsOfDigging", stepsOfDigging );

        if ( stepsOfDigging > 0 )
        {
            ++stepsOfDigging;
        }
    }
}

package rabbitescape.engine;

import static rabbitescape.engine.BehaviourTools.*;
import static rabbitescape.engine.ChangeDescription.State.*;
import static rabbitescape.engine.Direction.*;
import static rabbitescape.engine.Token.Type.*;

import rabbitescape.engine.ChangeDescription.State;

public class Bashing implements Behaviour
{
    private int stepsOfBashing;

    @Override
    public State newState( Rabbit rabbit, World world )
    {
        boolean justPickedUpToken = false;

        Token token = world.getTokenAt( rabbit.x, rabbit.y );
        if ( token != null && token.type == bash )
        {
            world.removeThing( token );
            justPickedUpToken = true;
        }

        if ( justPickedUpToken || stepsOfBashing > 0 )
        {
            if ( world.getBlockAt( destX( rabbit ), rabbit.y ) != null )
            {
                stepsOfBashing = 2;
                return rl(
                    rabbit,
                    RABBIT_BASHING_RIGHT,
                    RABBIT_BASHING_LEFT
                );
            }
            else if ( justPickedUpToken )
            {
                return rl(
                    rabbit,
                    RABBIT_BASHING_USELESSLY_RIGHT,
                    RABBIT_BASHING_USELESSLY_LEFT
                );
            }
        }
        --stepsOfBashing;
        return null;
    }

    @Override
    public boolean behave( World world, Rabbit rabbit, State state )
    {
        switch ( state )
        {
            case RABBIT_BASHING_RIGHT:
            case RABBIT_BASHING_LEFT:
            {
                world.removeBlockAt( destX( rabbit ), rabbit.y );
                return true;
            }
            case RABBIT_BASHING_USELESSLY_RIGHT:
            case RABBIT_BASHING_USELESSLY_LEFT:
            {
                return true;
            }
            default:
            {
                return false;
            }
        }
    }

    private int destX( Rabbit rabbit )
    {
        return ( rabbit.dir == RIGHT ) ? rabbit.x + 1 : rabbit.x - 1;
    }
}

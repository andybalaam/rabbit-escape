package rabbitescape.engine;

import static rabbitescape.engine.BehaviourTools.*;
import static rabbitescape.engine.ChangeDescription.State.*;
import static rabbitescape.engine.Direction.*;
import static rabbitescape.engine.Token.Type.*;

import java.util.Map;

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
            world.changes.removeToken( token );
            justPickedUpToken = true;
        }

        if ( justPickedUpToken || stepsOfBashing > 0 )
        {
            Block hereBlock = world.getBlockAt( rabbit.x, rabbit.y );

            int nextX = destX( rabbit );

            if (
                   rising( rabbit, hereBlock )
                && world.getBlockAt( nextX, rabbit.y - 1 ) != null )
            {
                stepsOfBashing = 2;
                return rl(
                    rabbit,
                    RABBIT_BASHING_UP_RIGHT,
                    RABBIT_BASHING_UP_LEFT
                );
            }
            else if ( world.getBlockAt( nextX, rabbit.y ) != null )
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

    private boolean rising( Rabbit rabbit, Block hereBlock )
    {
        return (
               rabbit.onSlope
            && hereBlock != null
            && hereBlock.riseDir() == rabbit.dir
        );
    }

    @Override
    public boolean behave( World world, Rabbit rabbit, State state )
    {
        switch ( state )
        {
            case RABBIT_BASHING_RIGHT:
            case RABBIT_BASHING_LEFT:
            {
                world.changes.removeBlockAt( destX( rabbit ), rabbit.y );
                return true;
            }
            case RABBIT_BASHING_UP_RIGHT:
            case RABBIT_BASHING_UP_LEFT:
            {
                world.changes.removeBlockAt( destX( rabbit ), rabbit.y - 1 );
                rabbit.y -= 1;
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

    @Override
    public void saveState( Map<String, String> saveState )
    {
        BehaviourTools.addToStateIfGtZero(
            saveState, "Bashing.stepsOfBashing", stepsOfBashing );
    }

    @Override
    public void restoreFromState( Map<String, String> saveState )
    {
        stepsOfBashing = BehaviourTools.restoreFromState(
            saveState, "Bashing.stepsOfBashing", stepsOfBashing );

        if ( stepsOfBashing > 0 )
        {
            ++stepsOfBashing;
        }
    }
}

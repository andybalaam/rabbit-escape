package rabbitescape.engine;

import static rabbitescape.engine.ChangeDescription.State.*;
import static rabbitescape.engine.Token.Type.*;

import java.util.Map;

import rabbitescape.engine.ChangeDescription.State;

public class Digging implements Behaviour
{
    private final Climbing climbing;
    private boolean justPickedUpToken;

    public Digging( Climbing climbing )
    {
        this.climbing = climbing;
    }

    public void checkForToken( Rabbit rabbit, World world )
    {
        justPickedUpToken = false;

        if ( climbing.abilityActive )
        {
            return;
        }

        Token token = world.getTokenAt( rabbit.x, rabbit.y );
        if ( token != null && token.type == dig )
        {
            world.changes.removeToken( token );
            justPickedUpToken = true;
        }
    }

    @Override
    public State newState( Rabbit rabbit, World world )
    {
        checkForToken( rabbit, world );

        if ( rabbit.state == RABBIT_DIGGING )
        {
            return RABBIT_DIGGING_2;
        }

        if (
               justPickedUpToken
            || rabbit.state == RABBIT_DIGGING_2
            || rabbit.state == RABBIT_DIGGING_ON_SLOPE
        )
        {
            if (
                   rabbit.onSlope
                && world.getBlockAt( rabbit.x, rabbit.y ) != null
            )
            {
                return RABBIT_DIGGING_ON_SLOPE;
            }
            else if ( world.getBlockAt( rabbit.x, rabbit.y + 1 ) != null )
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

    @Override
    public void saveState( Map<String, String> saveState )
    {
    }

    @Override
    public void restoreFromState( Map<String, String> saveState )
    {
    }
}

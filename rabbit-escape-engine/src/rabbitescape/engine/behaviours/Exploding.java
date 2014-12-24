package rabbitescape.engine.behaviours;

import static rabbitescape.engine.Token.Type.*;
import static rabbitescape.engine.ChangeDescription.State.*;

import java.util.Map;

import rabbitescape.engine.Behaviour;
import rabbitescape.engine.ChangeDescription.State;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.Token;
import rabbitescape.engine.World;

public class Exploding implements Behaviour
{
    private boolean justPickedUpToken;

    public void checkForToken( Rabbit rabbit, World world )
    {
        justPickedUpToken = false;

        Token token = world.getTokenAt( rabbit.x, rabbit.y );
        if ( token != null && token.type == explode )
        {
            world.changes.removeToken( token );
            justPickedUpToken = true;
        }
    }

    @Override
    public State newState( Rabbit rabbit, World world )
    {
        checkForToken( rabbit, world );

        if ( justPickedUpToken )
        {
            return RABBIT_EXPLODING;
        }
        return null;
    }

    @Override
    public boolean behave( World world, Rabbit rabbit, State state )
    {
        if ( state == RABBIT_EXPLODING )
        {
            world.changes.killRabbit( rabbit );
            return true;
        }

        return false;
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

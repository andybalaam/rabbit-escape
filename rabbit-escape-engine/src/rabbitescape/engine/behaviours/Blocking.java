package rabbitescape.engine.behaviours;

import static rabbitescape.engine.ChangeDescription.State.*;
import static rabbitescape.engine.Token.Type.*;

import java.util.Map;

import rabbitescape.engine.*;
import rabbitescape.engine.ChangeDescription.State;

public class Blocking implements Behaviour
{
    private final Climbing climbing;
    private boolean justPickedUpToken;

    public Blocking( Climbing climbing )
    {
        this.climbing = climbing;
    }

    private void checkForToken( Rabbit rabbit, World world )
    {
        justPickedUpToken = false;

        if ( climbing.abilityActive )
        {
            return;
        }

        Token token = world.getTokenAt( rabbit.x, rabbit.y );
        if ( token != null && token.type == block )
        {
            world.changes.removeToken( token );
            justPickedUpToken = true;
        }
    }

    @Override
    public State newState( Rabbit rabbit, World world )
    {
        checkForToken( rabbit, world );

        if ( justPickedUpToken || rabbit.state == RABBIT_BLOCKING )
        {
            return RABBIT_BLOCKING;
        }

        return null;
    }

    @Override
    public boolean behave( World world, Rabbit rabbit, State state )
    {
        return ( state == RABBIT_BLOCKING );
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

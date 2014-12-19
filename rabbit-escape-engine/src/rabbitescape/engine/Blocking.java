package rabbitescape.engine;

import static rabbitescape.engine.ChangeDescription.State.*;
import static rabbitescape.engine.Token.Type.*;

import java.util.Map;

import rabbitescape.engine.ChangeDescription.State;

public class Blocking implements Behaviour
{
    private final Climbing climbing;

    public Blocking( Climbing climbing )
    {
        this.climbing = climbing;
    }

    @Override
    public State newState( Rabbit rabbit, World world )
    {
        if ( climbing.abilityActive )
        {
            return null;
        }

        Token token = world.getTokenAt( rabbit.x, rabbit.y );
        if ( token != null && token.type == block )
        {
            world.changes.removeToken( token );
            return RABBIT_BLOCKING;
        }

        if( rabbit.state == RABBIT_BLOCKING )
        {
            return RABBIT_BLOCKING;
        }

        return null;
    }

    @Override
    public boolean behave( World world, Rabbit rabbit, State state )
    {
        if ( state == RABBIT_BLOCKING )
        {
            return true;
        }
        else
        {
            return false;
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

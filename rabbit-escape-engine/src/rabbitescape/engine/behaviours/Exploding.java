package rabbitescape.engine.behaviours;

import static rabbitescape.engine.Token.Type.*;
import static rabbitescape.engine.ChangeDescription.State.*;

import java.util.Map;

import rabbitescape.engine.Behaviour;
import rabbitescape.engine.ChangeDescription.State;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.Token;
import rabbitescape.engine.World;

public class Exploding extends Behaviour
{
    @Override
    public boolean checkTriggered( Rabbit rabbit, World world )
    {
        Token token = world.getTokenAt( rabbit.x, rabbit.y );
        if ( token != null && token.type == explode )
        {
            world.changes.removeToken( token );
            return true;
        }
        return false;
    }

    @Override
    public State newState( Rabbit rabbit, World world, boolean triggered )
    {
        if ( triggered )
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
}

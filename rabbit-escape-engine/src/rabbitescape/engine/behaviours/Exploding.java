package rabbitescape.engine.behaviours;

import static rabbitescape.engine.Token.Type.*;
import static rabbitescape.engine.ChangeDescription.State.*;

import rabbitescape.engine.*;
import rabbitescape.engine.ChangeDescription.State;

public class Exploding extends Behaviour
{
    @Override
    public void cancel()
    {
    }

    @Override
    public boolean checkTriggered( Rabbit rabbit, World world )
    {
        BehaviourTools t = new BehaviourTools( rabbit, world );
        return t.pickUpToken( explode, true );
    }

    @Override
    public State newState( BehaviourTools t, boolean triggered )
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
            
            world.changes.removeBlockAt( rabbit.x+1, rabbit.y );/*tavsan patlayinca cevresindeki duvarlar kırılacak(varsa) comment*/
            world.changes.removeBlockAt( rabbit.x-1, rabbit.y );
            world.changes.removeBlockAt( rabbit.x+1, rabbit.y+1 );
            world.changes.removeBlockAt( rabbit.x-1, rabbit.y+1 );
            
            return true;
        }

        return false;
    }
}

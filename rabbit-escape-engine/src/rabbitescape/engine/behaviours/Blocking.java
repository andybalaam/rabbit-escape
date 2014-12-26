package rabbitescape.engine.behaviours;

import static rabbitescape.engine.ChangeDescription.State.*;
import static rabbitescape.engine.Token.Type.*;

import rabbitescape.engine.*;
import rabbitescape.engine.ChangeDescription.State;

public class Blocking extends Behaviour
{
    private final Climbing climbing;

    public Blocking( Climbing climbing )
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
        BehaviourTools t = new BehaviourTools( rabbit, world );

        return t.pickUpToken( block );
    }

    @Override
    public State newState( BehaviourTools t, boolean triggered )
    {
        if ( triggered || t.rabbit.state == RABBIT_BLOCKING )
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
}

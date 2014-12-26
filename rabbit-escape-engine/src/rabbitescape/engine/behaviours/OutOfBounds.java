package rabbitescape.engine.behaviours;

import static rabbitescape.engine.ChangeDescription.State.*;

import rabbitescape.engine.Behaviour;
import rabbitescape.engine.BehaviourTools;
import rabbitescape.engine.ChangeDescription.State;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.World;

public class OutOfBounds extends Behaviour
{
    @Override
    public void cancel()
    {
    }

    @Override
    public boolean checkTriggered( Rabbit rabbit, World world )
    {
        return (
               rabbit.x < 0
            || rabbit.x >= world.size.width
            || rabbit.y < 0
            || rabbit.y >= world.size.height
        );
    }

    @Override
    public State newState(
        BehaviourTools t, boolean triggered
    )
    {
        if ( triggered )
        {
            return RABBIT_OUT_OF_BOUNDS;
        }

        return null;
    }

    @Override
    public boolean behave( World world, Rabbit rabbit, State state )
    {
        switch( state )
        {
            case RABBIT_OUT_OF_BOUNDS:
            {
                world.changes.killRabbit( rabbit );
                return true;
            }
            default:
            {
                return false;
            }
        }
    }
}

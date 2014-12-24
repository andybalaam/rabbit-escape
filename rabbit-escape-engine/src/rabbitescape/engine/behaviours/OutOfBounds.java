package rabbitescape.engine.behaviours;

import static rabbitescape.engine.ChangeDescription.State.*;

import java.util.Map;

import rabbitescape.engine.Behaviour;
import rabbitescape.engine.ChangeDescription.State;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.World;

public class OutOfBounds implements Behaviour
{

    @Override
    public State newState( Rabbit rabbit, World world )
    {
        if (
               rabbit.x < 0
            || rabbit.x >= world.size.width
            || rabbit.y < 0
            || rabbit.y >= world.size.height
        )
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

    @Override
    public void saveState( Map<String, String> saveState )
    {
    }

    @Override
    public void restoreFromState( Map<String, String> saveState )
    {
    }

}

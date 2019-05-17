package rabbitescape.engine.behaviours;

import static rabbitescape.engine.ChangeDescription.State.*;

import rabbitescape.engine.Behaviour;
import rabbitescape.engine.BehaviourTools;
import rabbitescape.engine.ChangeDescription.State;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.World;
import rabbitescape.engine.config.TapTimer;

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
                checkMars( world, rabbit );
                world.changes.killRabbit( rabbit );
                return true;
            }
            default:
            {
                return false;
            }
        }
    }

    /**
     * Test if mars mode has been triggered
     */
    private void checkMars( World world, Rabbit rabbit)
    {
        /* The rabbit must leave the world at the correct coordinates,
         * the index count is likely to only be correct if this is the
         * first rabbit out of the entrance, and it must be the correct
         * level.
         */
        if ( 12 == rabbit.x && -1 == rabbit.y &&
             world.getRabbitIndexCount() == 2 &&
             world.name.equals( "Ghost versus pie" ) )
        {
            TapTimer.setMars();
        }
    }
}

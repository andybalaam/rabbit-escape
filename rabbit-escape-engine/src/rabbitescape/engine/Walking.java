package rabbitescape.engine;

import static rabbitescape.engine.Direction.*;
import rabbitescape.engine.ChangeDescription.State;

public class Walking implements Behaviour
{
    @Override
    public boolean describeChanges(
        Rabbit rabbit, World world, ChangeDescription ret )
    {
        if ( rabbit.dir == RIGHT )
        {
            if ( turn( rabbit, world ) )
            {
                ret.add(
                    rabbit.x, rabbit.y, State.RABBIT_TURNING_RIGHT_TO_LEFT );
            }
            else
            {
                ret.add( rabbit.x, rabbit.y, State.RABBIT_WALKING_RIGHT );
            }
        }
        else
        {
            if ( turn( rabbit, world ) )
            {
                ret.add(
                    rabbit.x, rabbit.y, State.RABBIT_TURNING_LEFT_TO_RIGHT );
            }
            else
            {
                ret.add( rabbit.x, rabbit.y, State.RABBIT_WALKING_LEFT );
            }
        }

        return true;
    }

    @Override
    public boolean behave( Rabbit rabbit, World world )
    {
        if ( turn( rabbit, world ) )
        {
            rabbit.dir = opposite( rabbit.dir );
        }
        else
        {
            int destination = ( rabbit.dir == RIGHT ) ? rabbit.x + 1 : rabbit.x - 1;
            rabbit.x = destination;
        }

        return true;  // We always handle the behaviour if no-one else did
    }

    private static boolean turn( Rabbit rabbit, World world )
    {
        int destination = ( rabbit.dir == RIGHT ) ? rabbit.x + 1 : rabbit.x - 1;
        return world.blockAt( destination, rabbit.y );
    }
}

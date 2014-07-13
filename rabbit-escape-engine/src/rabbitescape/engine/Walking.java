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
            ret.add( rabbit.x, rabbit.y, State.RABBIT_WALKING_RIGHT );
        }
        else
        {
            ret.add( rabbit.x, rabbit.y, State.RABBIT_WALKING_LEFT );
        }

        return true;
    }

    @Override
    public boolean behave( Rabbit rabbit, World world )
    {
        int destination = ( rabbit.dir == RIGHT ) ? rabbit.x + 1 : rabbit.x - 1;

        if ( world.blockAt( destination, rabbit.y ) )
        {
            rabbit.dir = opposite( rabbit.dir );
        }
        else
        {
            rabbit.x = destination;
        }

        return true;  // We always handle the behaviour if no-one else did
    }
}

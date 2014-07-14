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
            if ( startRise( rabbit, world ) )
            {
                ret.add( rabbit.x, rabbit.y, State.RABBIT_RISING_RIGHT_1 );
            }
            else if ( finishRise( rabbit, world ) )
            {
                ret.add( rabbit.x, rabbit.y, State.RABBIT_RISING_RIGHT_2 );
            }
            else if ( turn( rabbit, world ) )
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
            if ( startRise( rabbit, world ) )
            {
                ret.add( rabbit.x, rabbit.y, State.RABBIT_RISING_LEFT_1 );
            }
            else if ( finishRise( rabbit, world ) )
            {
                ret.add( rabbit.x, rabbit.y, State.RABBIT_RISING_LEFT_2 );
            }
            else if ( turn( rabbit, world ) )
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
        if ( finishRise( rabbit, world ) )
        {
            rabbit.x = dest( rabbit );
            rabbit.y -= 1;
        }
        else if ( turn( rabbit, world ) )
        {
            rabbit.dir = opposite( rabbit.dir );
        }
        else
        {
            rabbit.x = dest( rabbit );
        }

        return true;  // We always handle the behaviour if no-one else did
    }

    private static boolean turn( Rabbit rabbit, World world )
    {
        return world.squareBlockAt( dest( rabbit ), rabbit.y );
    }

    private static int dest( Rabbit rabbit )
    {
        return ( rabbit.dir == RIGHT ) ? rabbit.x + 1 : rabbit.x - 1;
    }

    private boolean startRise( Rabbit rabbit, World world )
    {
        return riseBlockAt( dest( rabbit ), rabbit, world );
    }

    private boolean finishRise( Rabbit rabbit, World world )
    {
        return riseBlockAt( rabbit.x, rabbit, world );
    }

    private boolean riseBlockAt( int x, Rabbit rabbit, World world )
    {
        return riseBlockType( rabbit ).isInstance(
            world.getBlockAt( x, rabbit.y ) );
    }

    private Class<? extends Block> riseBlockType( Rabbit rabbit )
    {
        return rabbit.dir == RIGHT
            ? SlopeRightBlock.class : SlopeLeftBlock.class;
    }
}

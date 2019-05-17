package rabbitescape.engine.behaviours;

import rabbitescape.engine.Behaviour;
import rabbitescape.engine.BehaviourTools;
import rabbitescape.engine.ChangeDescription.State;
import rabbitescape.engine.Direction;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.World;

public class RabbotWait extends Behaviour
{
    private boolean within1Vertically( Rabbit otherRabbit, Rabbit rabbit )
    {
        return ( Math.abs( otherRabbit.y - rabbit.y ) < 2 );
    }

    private boolean noseToNose( Rabbit otherRabbit, Rabbit rabbit )
    {
        if ( 
            otherRabbit.x == rabbit.x - 1 &&
            otherRabbit.dir == Direction.RIGHT &&
            rabbit.dir == Direction.LEFT 
        )
        {
            return true;
        }
        else if ( 
            otherRabbit.x == rabbit.x + 1 &&
            otherRabbit.dir == Direction.LEFT &&
            rabbit.dir == Direction.RIGHT 
        )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public void cancel()
    {
    }

    @Override
    public boolean checkTriggered( Rabbit rabbit, World world )
    {
        if (
            rabbit.type == Rabbit.Type.RABBOT &&
            !Blocking.isBlocking(rabbit.state) &&
            !Digging.isDigging(rabbit.state)
        )
        {
            for ( Rabbit otherRabbit : world.rabbits )
            {
                if (
                    otherRabbit.type == Rabbit.Type.RABBIT &&
                    within1Vertically( otherRabbit, rabbit ) &&
                    noseToNose( otherRabbit, rabbit ) &&
                    !Blocking.isBlocking(otherRabbit.state)
                )
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public State newState( BehaviourTools t, boolean triggered )
    {
        if ( triggered )
        {
            return t.rl( 
                State.RABBIT_WAITING_RIGHT,
                State.RABBIT_WAITING_LEFT 
            );
        }
        else
        {
            return null;
        }
    }

    @Override
    public boolean behave( World world, Rabbit rabbit, State state )
    {
        if ( 
            state == State.RABBIT_WAITING_LEFT ||
            state == State.RABBIT_WAITING_RIGHT 
        )
        {
            return true;
        }

        return false;
    }
}

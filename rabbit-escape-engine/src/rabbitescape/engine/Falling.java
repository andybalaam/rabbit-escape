package rabbitescape.engine;

import rabbitescape.engine.ChangeDescription.State;

public class Falling implements Behaviour
{
    private static final int fatalHeight = 4;

    private int heightFallen = 0;

    @Override
    public boolean behave( Rabbit rabbit, World world )
    {
        if ( !falling( rabbit, world ) )
        {
            if ( heightFallen > fatalHeight )
            {
                rabbit.die();
                return true;
            }

            heightFallen = 0;
            return false;
        }

        int furtherBelow = rabbit.y + 2;

        if ( world.blockAt( rabbit.x, furtherBelow ) )
        {
            heightFallen += 1;
            rabbit.y = rabbit.y + 1;
        }
        else
        {
            heightFallen += 2;
            rabbit.y = furtherBelow;
        }

        return true;
    }

    @Override
    public boolean describeChanges(
        Rabbit rabbit, World world, ChangeDescription ret )
    {
        if ( !falling( rabbit, world ) )
        {
            if ( heightFallen > fatalHeight )
            {
                if ( heightFallen % 2 == 0 )
                {
                    ret.add( rabbit.x, rabbit.y, State.RABBIT_DYING_OF_FALLING );
                }
                else
                {
                    ret.add( rabbit.x, rabbit.y, State.RABBIT_DYING_OF_FALLING_2 );
                }
                return true;
            }

            return false;
        }

        if ( heightFallen + 1 > fatalHeight )
        {
            int furtherBelow = rabbit.y + 2;
            if ( world.blockAt( rabbit.x, furtherBelow ) )
            {
                ret.add( rabbit.x, rabbit.y, State.RABBIT_FALLING_1_TO_DEATH );
            }
        }
        else
        {
            ret.add( rabbit.x, rabbit.y, State.RABBIT_FALLING );
        }

        return true;
    }

    boolean falling( Rabbit rabbit, World world )
    {
        int below = rabbit.y + 1;
        if ( world.blockAt( rabbit.x, below ) )
        {
            return false;
        }

        return true;
    }
}

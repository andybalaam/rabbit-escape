package rabbitescape.engine;

public class Falling implements Behaviour
{
    @Override
    public boolean behave( Rabbit rabbit, World world )
    {
        if ( !falling( rabbit, world ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public boolean describeChanges(
        Rabbit rabbit, World world, ChangeDescription ret )
    {
        if ( !falling( rabbit, world ) )
        {
            return false;
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

        int furtherBelow = rabbit.y + 2;

        if ( world.blockAt( rabbit.x, furtherBelow ) )
        {
            // Only fall 1 step
            rabbit.y = below;
        }
        else
        {
            rabbit.y = furtherBelow;
        }

        return true;
    }
}

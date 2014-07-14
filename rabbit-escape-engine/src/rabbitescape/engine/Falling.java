package rabbitescape.engine;

import static rabbitescape.engine.ChangeDescription.State.*;
import rabbitescape.engine.ChangeDescription.State;

public class Falling implements Behaviour
{
    private static final int fatalHeight = 4;

    private int heightFallen = 0;

    @Override
    public boolean behave( Rabbit rabbit, State state )
    {
        switch ( state )
        {
            case RABBIT_DYING_OF_FALLING:
            {
                rabbit.die();
                return true;
            }
            case RABBIT_DYING_OF_FALLING_2:
            {
                rabbit.die();
                return true;
            }
            case RABBIT_FALLING:
            {
                heightFallen += 2;
                rabbit.y = rabbit.y + 2;
                return true;
            }
            case RABBIT_FALLING_1_TO_DEATH:
            case RABBIT_FALLING_1:
            {
                heightFallen += 1;
                rabbit.y = rabbit.y + 1;
                return true;
            }
            default:
            {
                heightFallen = 0;
                return false;
            }
        }
    }

    @Override
    public State newState( Rabbit rabbit, World world )
    {
        if ( !falling( rabbit, world ) )
        {
            if ( heightFallen > fatalHeight )
            {
                if ( heightFallen % 2 == 0 )
                {
                    return RABBIT_DYING_OF_FALLING;
                }
                else
                {
                    return RABBIT_DYING_OF_FALLING_2;
                }
            }
            return null;
        }

        if (
               ( heightFallen + 1 > fatalHeight )                // Going to die
            && ( world.squareBlockAt( rabbit.x, rabbit.y + 2 ) ) // during step
        )
        {
            return State.RABBIT_FALLING_1_TO_DEATH;
        }
        else
        {
            if ( world.squareBlockAt( rabbit.x, rabbit.y + 2 ) )
            {
                return State.RABBIT_FALLING_1;
            }
            else
            {
                return State.RABBIT_FALLING;
            }
        }
    }

    boolean falling( Rabbit rabbit, World world )
    {
        int below = rabbit.y + 1;
        if ( world.squareBlockAt( rabbit.x, below ) )
        {
            return false;
        }

        return true;
    }
}

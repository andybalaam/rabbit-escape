package rabbitescape.engine;

import static rabbitescape.engine.ChangeDescription.State.*;

import java.util.ArrayList;
import java.util.List;

public class Rabbit extends Thing
{
    public static enum Mode
    {
        NORMAL,
        BASH,
    }

    private final List<Behaviour> behaviours;

    public Direction dir;

    public Rabbit( int x, int y, Direction dir )
    {
        super( x, y, RABBIT_WALKING_LEFT );
        this.dir = dir;
        behaviours = createBehaviours();
    }

    private static List<Behaviour> createBehaviours()
    {
        List<Behaviour> ret = new ArrayList<Behaviour>();

        ret.add( new Exiting() );
        ret.add( new Falling() );
        ret.add( new Bashing() );
        ret.add( new Walking() );

        return ret;
    }

    @Override
    public void calcNewState( World world )
    {
        for ( Behaviour behaviour : behaviours )
        {
            state = behaviour.newState( this, world );
            if ( state != null )
            {
                break;
            }
        }
    }

    @Override
    public void step( World world )
    {
        for ( Behaviour behaviour : behaviours )
        {
            boolean handled = behaviour.behave( world, this, state );
            if ( handled )
            {
                break;
            }
        }
    }
}

package rabbitescape.engine;

import java.util.ArrayList;
import java.util.List;

public class Rabbit extends Thing
{
    public Direction dir;
    private final List<Behaviour> behaviours;

    public Rabbit( int x, int y, Direction dir )
    {
        super( x, y );
        this.dir = dir;
        behaviours = createBehaviours();
    }

    private static List<Behaviour> createBehaviours()
    {
        List<Behaviour> ret = new ArrayList<Behaviour>();

        ret.add( new Exiting() );
        ret.add( new Falling() );
        ret.add( new Walking() );

        return ret;
    }

    @Override
    public void init( World world )
    {
        calcNewState( world );
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
        calcNewState( world );
    }

    private void calcNewState( World world )
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

    public void die()
    {
        alive = false;
    }
}

package rabbitescape.engine;

import java.util.ArrayList;
import java.util.List;

public class Rabbit extends Thing
{
    private final List<Behaviour> behaviours;
    public Direction dir;

    public Rabbit( int x, int y, Direction dir )
    {
        super( x, y );
        this.dir = dir;
        behaviours = createBehaviours();
    }

    private static List<Behaviour> createBehaviours()
    {
        List<Behaviour> ret = new ArrayList<Behaviour>();

        ret.add( new Falling() );
        ret.add( new Walking() );

        return ret;
    }

    @Override
    public void step( World world )
    {
        for ( Behaviour behaviour : behaviours )
        {
            boolean handled = behaviour.behave( this, world );
            if ( handled )
            {
                break;
            }
        }
    }

    @Override
    public void describeChanges( World world, ChangeDescription ret )
    {
        for ( Behaviour behaviour : behaviours )
        {
            boolean handled = behaviour.describeChanges( this, world, ret );
            if ( handled )
            {
                break;
            }
        }
    }
}

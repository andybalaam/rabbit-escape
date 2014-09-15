package rabbitescape.engine;

import static rabbitescape.engine.ChangeDescription.State.*;

import java.util.ArrayList;
import java.util.List;

import rabbitescape.engine.ChangeDescription.State;

public class Rabbit extends Thing
{
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
        List<Behaviour> ret = new ArrayList<>();

        Digging digging = new Digging();

        ret.add( new Exiting() );
        ret.add( new Falling( digging ) );
        ret.add( new Bashing() );
        ret.add( digging );
        ret.add( new Bridging() );
        ret.add( new Walking() );

        return ret;
    }

    @Override
    public void calcNewState( World world )
    {
        boolean done = false;
        for ( Behaviour behaviour : behaviours )
        {
            State thisState = behaviour.newState( this, world );
            if ( thisState != null && !done )
            {
                state = thisState;
                done = true;
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

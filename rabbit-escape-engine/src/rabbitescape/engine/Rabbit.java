package rabbitescape.engine;

import static rabbitescape.engine.ChangeDescription.State.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rabbitescape.engine.ChangeDescription.State;
import rabbitescape.engine.behaviours.*;

public class Rabbit extends Thing
{
    private final List<Behaviour> behaviours;

    public Direction dir;
    public boolean onSlope;

    public Rabbit( int x, int y, Direction dir )
    {
        super( x, y, RABBIT_WALKING_LEFT );
        this.dir = dir;
        this.onSlope = false;
        behaviours = createBehaviours();
    }

    private static List<Behaviour> createBehaviours()
    {
        List<Behaviour> ret = new ArrayList<>();

        Climbing climbing = new Climbing();
        Digging digging = new Digging( climbing );

        ret.add( new Exploding() );
        ret.add( new OutOfBounds() );
        ret.add( new Exiting() );
        ret.add( new Falling( climbing ) );
        ret.add( new Bashing( climbing ) );
        ret.add( digging );
        ret.add( new Bridging( climbing ) );
        ret.add( new Blocking( climbing ) );
        ret.add( climbing );
        ret.add( new Walking() );

        return ret;
    }

    @Override
    public void calcNewState( World world )
    {
        for ( Behaviour behaviour : behaviours )
        {
            behaviour.triggered = false;
        }

        for ( Behaviour behaviour : behaviours )
        {
            behaviour.triggered = behaviour.checkTriggered( this, world );
            if ( behaviour.triggered )
            {
                cancelAllBehavioursExcept( behaviour );
                break;
            }
        }

        boolean done = false;
        for ( Behaviour behaviour : behaviours )
        {
            State thisState = behaviour.newState(
                this, world, behaviour.triggered );

            if ( thisState != null && !done )
            {
                state = thisState;
                done = true;
            }
        }
    }

    private void cancelAllBehavioursExcept( Behaviour exception )
    {
        for ( Behaviour behaviour : behaviours )
        {
            if ( behaviour != exception )
            {
                behaviour.cancel();
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

    @Override
    public Map<String, String> saveState()
    {
        Map<String, String> ret = new HashMap<String, String>();

        BehaviourState.addToStateIfTrue( ret, "onSlope", onSlope );

        for ( Behaviour behaviour : behaviours )
        {
            behaviour.saveState( ret );
        }

        return ret;
    }

    @Override
    public void restoreFromState( Map<String, String> state )
    {
        onSlope = BehaviourState.restoreFromState(
            state, "onSlope", false
        );

        for ( Behaviour behaviour : behaviours )
        {
            behaviour.restoreFromState( state );
        }
    }
}

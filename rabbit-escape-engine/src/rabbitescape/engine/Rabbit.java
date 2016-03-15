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
    private final List<Behaviour> behavioursTriggerOrder;

    private Falling falling;

    public Direction dir;
    public boolean onSlope;

    public Rabbit( int x, int y, Direction dir )
    {
        super( x, y, RABBIT_WALKING_LEFT );
        this.dir = dir;
        this.onSlope = false;
        behaviours = new ArrayList<>();
        behavioursTriggerOrder = new ArrayList<>();
        createBehaviours();
    }

    private void createBehaviours()
    {
        Climbing climbing = new Climbing();
        Digging digging = new Digging();
        Exploding exploding = new Exploding();
        Burning burning = new Burning();
        OutOfBounds outOfBounds = new OutOfBounds();
        Drowning drowning = new Drowning();
        Exiting exiting = new Exiting();
        Brollychuting brollychuting = new Brollychuting( climbing );
        falling = new Falling( climbing, brollychuting );
        Bashing bashing = new Bashing();
        Bridging bridging = new Bridging();
        Blocking blocking = new Blocking();
        Walking walking = new Walking();

        behavioursTriggerOrder.add( exploding );
        behavioursTriggerOrder.add( outOfBounds );
        behavioursTriggerOrder.add( burning );
        behavioursTriggerOrder.add( drowning );
        behavioursTriggerOrder.add( falling );
        behavioursTriggerOrder.add( exiting );
        behavioursTriggerOrder.add( brollychuting );
        behavioursTriggerOrder.add( climbing );
        behavioursTriggerOrder.add( bashing );
        behavioursTriggerOrder.add( digging );
        behavioursTriggerOrder.add( bridging );
        behavioursTriggerOrder.add( blocking );
        behavioursTriggerOrder.add( walking );

        behaviours.add( exploding );
        behaviours.add( outOfBounds );
        behaviours.add( burning );
        behaviours.add( drowning );
        behaviours.add( falling );
        behaviours.add( exiting );
        behaviours.add( brollychuting );
        behaviours.add( bashing );
        behaviours.add( digging );
        behaviours.add( bridging );
        behaviours.add( blocking );
        behaviours.add( climbing );
        behaviours.add( walking );

        assert behavioursTriggerOrder.size() == behaviours.size();
    }

    public boolean isFallingToDeath()
    {
        return falling.isFallingToDeath();
    }

    @Override
    public void calcNewState( World world )
    {
        for ( Behaviour behaviour : behavioursTriggerOrder )
        {
            behaviour.triggered = false;
        }

        for ( Behaviour behaviour : behavioursTriggerOrder )
        {
            behaviour.triggered = behaviour.checkTriggered( this, world );
            if ( behaviour.triggered )
            {
                cancelAllBehavioursExcept( behaviour );
            }
        }

        boolean done = false;
        for ( Behaviour behaviour : behaviours )
        {
            State thisState = behaviour.newState(
                new BehaviourTools( this, world ), behaviour.triggered );

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

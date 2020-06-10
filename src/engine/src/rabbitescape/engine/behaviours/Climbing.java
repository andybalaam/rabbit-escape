package rabbitescape.engine.behaviours;

import static rabbitescape.engine.Token.Type.*;

import java.util.Map;

import rabbitescape.engine.*;
import rabbitescape.engine.ChangeDescription.State;
import rabbitescape.engine.behaviours.climbing.ClimbingInterFace;
import rabbitescape.engine.behaviours.climbing.NotClimbing;

public class Climbing extends Behaviour
{
    boolean hasAbility = false;
    public boolean abilityActive = false;
    private ClimbingInterFace climbingState;
    
    public Climbing() {setClimbingState(new NotClimbing());}
    
	public ClimbingInterFace getClimbingState() {
		return climbingState;
	}

	public void setClimbingState(ClimbingInterFace climbingState) {
		this.climbingState = climbingState;
	}
	
    @Override
    public void cancel()
    {
        abilityActive = false;
    }

    @Override
    public boolean checkTriggered( Rabbit rabbit, World world )
    {
        BehaviourTools t = new BehaviourTools( rabbit, world );

        return !hasAbility && t.pickUpToken( climb, true );
    }

    @Override
    public State newState( BehaviourTools t, boolean triggered )
    {
        if ( triggered )
        {
            hasAbility = true;
        }

        if ( !hasAbility )
        {
            return null;
        }

        climbingState = climbingState.newState(t, this);

        return climbingState.getState();
    }

    @Override
    public boolean behave( World world, Rabbit rabbit, State state )
    {
        BehaviourTools t = new BehaviourTools( rabbit, world );

        if( t.rabbitIsClimbing() )
        { // Can't be both on a wall and on a slope.
            rabbit.onSlope = false;
        }

        return climbingState.behave(world, rabbit, this);
    }

    @Override
    public void saveState( Map<String, String> saveState )
    {
        BehaviourState.addToStateIfTrue(
            saveState, "Climbing.hasAbility", hasAbility
        );

        BehaviourState.addToStateIfTrue(
            saveState, "Climbing.abilityActive", abilityActive
        );
    }

    @Override
    public void restoreFromState( Map<String, String> saveState )
    {
        hasAbility = BehaviourState.restoreFromState(
            saveState, "Climbing.hasAbility", hasAbility
        );

        abilityActive = BehaviourState.restoreFromState(
            saveState, "Climbing.abilityActive", abilityActive
        );
    }
}

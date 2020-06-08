package rabbitescape.engine.behaviours;

import static rabbitescape.engine.Direction.*;
import static rabbitescape.engine.Token.Type.*;

import java.util.Map;

import rabbitescape.engine.*;
import rabbitescape.engine.ChangeDescription.State;
import rabbitescape.engine.behaviours.bashing.*;

public class Bashing extends Behaviour
{
    private int stepsOfBashing;
    private BashingInterFace bashingState;
    
	public BashingInterFace getBashingState() {
		return bashingState;
	}

	public void setBashingState(BashingInterFace bashingState) {
		this.bashingState = bashingState;
	}
	
	public void setBashingState(BashingInterFace right, BashingInterFace left, Rabbit rabbit) {
        if (rabbit.dir == RIGHT) {
            setBashingState(right);
        } else {
            setBashingState(left);
        }
	}
	
	public Bashing() {setBashingState(new NotBashing());}
	
    @Override
    public void cancel()
    {
        stepsOfBashing = 0;
    }

    @Override
    public boolean checkTriggered( Rabbit rabbit, World world )
    {
        BehaviourTools t = new BehaviourTools( rabbit, world );

        return t.pickUpToken( bash );
    }

    @Override
    public State newState( BehaviourTools t, boolean triggered )
    {
        if ( triggered || stepsOfBashing > 0 )
        {
            if (
                   t.isOnUpSlope()
                && t.blockAboveNext() != null
            )
            {
                if (t.blockAboveNext().material == Block.Material.METAL)
                {
                    stepsOfBashing = 0;
                    setBashingState(new BashingUselesslyRightUp(), new BashingUselesslyLeftUp(),
                            t.rabbit);
                }
                else
                {
                    stepsOfBashing = 2;
                    setBashingState(new BashingUpRight(), new BashingUpLeft(), t.rabbit);
                }
            }
            else if (
                t.isOnUpSlope()
             && t.blockAboveNext() == null
             && triggered
            )
            {
                setBashingState(new BashingUselesslyRightUp(), new BashingUselesslyLeftUp(), t.rabbit);
            }
            else if ( t.blockNext() != null )
            {
                if ( t.blockNext().material == Block.Material.METAL )
                {
                    stepsOfBashing = 0;
                    setBashingState(new BashingUselesslyRight(), new BashingUselesslyLeft(), t.rabbit);
                }
                else
                {
                    stepsOfBashing = 2;
                    setBashingState(new BashingRight(), new BashingLeft(), t.rabbit);
                }
            }
            else if ( triggered )
            {
                setBashingState(new BashingUselesslyRight(), new BashingUselesslyLeft(), t.rabbit);
            }
        }
        --stepsOfBashing;
        return null;
    }

    @Override
    public boolean behave( World world, Rabbit rabbit, State state )
    {
        return bashingState.behave(world, rabbit);
    }

    public static int destX( Rabbit rabbit )
    {
        return ( rabbit.dir == RIGHT ) ? rabbit.x + 1 : rabbit.x - 1;
    }

    @Override
    public void saveState( Map<String, String> saveState )
    {
        BehaviourState.addToStateIfGtZero(
            saveState, "Bashing.stepsOfBashing", stepsOfBashing
        );
    }

    @Override
    public void restoreFromState( Map<String, String> saveState )
    {
        stepsOfBashing = BehaviourState.restoreFromState(
            saveState, "Bashing.stepsOfBashing", stepsOfBashing
        );

        if ( stepsOfBashing > 0 )
        {
            ++stepsOfBashing;
        }
    }
}

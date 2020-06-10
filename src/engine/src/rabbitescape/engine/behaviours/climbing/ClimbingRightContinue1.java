package rabbitescape.engine.behaviours.climbing;

import rabbitescape.engine.BehaviourTools;
import rabbitescape.engine.ChangeDescription.State;
import rabbitescape.engine.World;
import rabbitescape.engine.behaviours.Climbing;
import rabbitescape.engine.Rabbit;

import static rabbitescape.engine.ChangeDescription.State.RABBIT_CLIMBING_RIGHT_CONTINUE_1;

public class ClimbingRightContinue1 implements ClimbingInterFace {

    @Override
    public State getState() {
        return RABBIT_CLIMBING_RIGHT_CONTINUE_1;
    }

    @Override
    public ClimbingInterFace newState(BehaviourTools t, Climbing climbing) {
        return new ClimbingRightContinue2();
    }

    @Override
    public boolean behave(World world, Rabbit rabbit, Climbing climbing) {
        climbing.abilityActive = true;
        return true;
    }
}

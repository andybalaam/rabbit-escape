package rabbitescape.engine.behaviours.climbing;

import rabbitescape.engine.BehaviourTools;
import rabbitescape.engine.ChangeDescription.State;
import rabbitescape.engine.World;
import rabbitescape.engine.behaviours.Climbing;
import rabbitescape.engine.Rabbit;

import static rabbitescape.engine.ChangeDescription.State.RABBIT_CLIMBING_LEFT_CONTINUE_1;

public class ClimbingLeftContinue1 implements ClimbingInterFace {

    @Override
    public State getState() {
        return RABBIT_CLIMBING_LEFT_CONTINUE_1;
    }

    @Override
    public ClimbingInterFace newState(BehaviourTools t, Climbing climbing) {
        return new ClimbingLeftContinue2();
    }

    @Override
    public boolean behave(World world, Rabbit rabbit, Climbing climbing) {
        climbing.abilityActive = true;
        return true;
    }
}

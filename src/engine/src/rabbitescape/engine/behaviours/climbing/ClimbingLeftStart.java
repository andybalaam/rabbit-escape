package rabbitescape.engine.behaviours.climbing;

import rabbitescape.engine.BehaviourTools;
import rabbitescape.engine.Block;
import rabbitescape.engine.ChangeDescription.State;
import rabbitescape.engine.World;
import rabbitescape.engine.behaviours.Climbing;
import rabbitescape.engine.Rabbit;

import static rabbitescape.engine.ChangeDescription.State.*;

public class ClimbingLeftStart implements ClimbingInterFace {

    @Override
    public State getState() {
        return RABBIT_CLIMBING_LEFT_START;
    }

    @Override
    public ClimbingInterFace newState(BehaviourTools t, Climbing climbing) {
        Block endBlock = t.blockAboveNext();

        if (t.isWall(endBlock)) {
            return new ClimbingLeftContinue2();
        } else {
            return new ClimbingLeftEnd();
        }
    }

    @Override
    public boolean behave(World world, Rabbit rabbit, Climbing climbing) {
        climbing.abilityActive = true;
        return true;
    }
}

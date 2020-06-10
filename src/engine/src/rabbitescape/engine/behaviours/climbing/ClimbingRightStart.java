package rabbitescape.engine.behaviours.climbing;

import rabbitescape.engine.BehaviourTools;
import rabbitescape.engine.Block;
import rabbitescape.engine.ChangeDescription.State;
import rabbitescape.engine.World;
import rabbitescape.engine.behaviours.Climbing;
import rabbitescape.engine.Rabbit;

import static rabbitescape.engine.ChangeDescription.State.*;

public class ClimbingRightStart implements ClimbingInterFace {

    @Override
    public State getState() {
        return RABBIT_CLIMBING_RIGHT_START;
    }

    @Override
    public ClimbingInterFace newState(BehaviourTools t, Climbing climbing) {
        Block endBlock = t.blockAboveNext();

        if (t.isWall(endBlock)) {
            return new ClimbingRightContinue2();
        } else {
            return new ClimbingRightEnd();
        }
    }

    @Override
    public boolean behave(World world, Rabbit rabbit, Climbing climbing) {
        climbing.abilityActive = true;
        return true;
    }
}
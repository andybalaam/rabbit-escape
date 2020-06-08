package rabbitescape.engine.behaviours.bashing;

import rabbitescape.engine.ChangeDescription.State;
import rabbitescape.engine.World;
import rabbitescape.engine.Rabbit;

import static rabbitescape.engine.ChangeDescription.State.RABBIT_BASHING_USELESSLY_LEFT_UP;

public class BashingUselesslyLeftUp implements BashingInterFace {

    @Override
    public State getState() {
        return RABBIT_BASHING_USELESSLY_LEFT_UP;
    }

    @Override
    public boolean behave(World world, Rabbit rabbit) {
        rabbit.slopeBashHop = true;
        rabbit.y -= 1;
        return true;
    }
}

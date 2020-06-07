package rabbitescape.engine.behaviours.Bashing;

import rabbitescape.engine.ChangeDescription.State;
import rabbitescape.engine.World;
import rabbitescape.engine.Rabbit;

import static rabbitescape.engine.ChangeDescription.State.RABBIT_BASHING_USELESSLY_RIGHT;

public class BashingUselesslyRight implements BashingInterFace {

    @Override
    public State getState() {
        return RABBIT_BASHING_USELESSLY_RIGHT;
    }

    @Override
    public boolean behave(World world, Rabbit rabbit) {
        rabbit.slopeBashHop = false;
        return true;
    }
}

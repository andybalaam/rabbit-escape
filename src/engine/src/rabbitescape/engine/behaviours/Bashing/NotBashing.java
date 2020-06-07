package rabbitescape.engine.behaviours.Bashing;

import rabbitescape.engine.ChangeDescription.State;
import rabbitescape.engine.World;
import rabbitescape.engine.Rabbit;

public class NotBashing implements BashingInterFace {

    @Override
    public State getState() {
        return null;
    }

    @Override
    public boolean behave(World world, Rabbit rabbit) {
        rabbit.slopeBashHop = false;
        return false;
    }
}

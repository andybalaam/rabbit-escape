package rabbitescape.engine.behaviours.bashing;

import rabbitescape.engine.ChangeDescription.State;
import rabbitescape.engine.behaviours.Bashing;
import rabbitescape.engine.World;
import rabbitescape.engine.Rabbit;

import static rabbitescape.engine.ChangeDescription.State.RABBIT_BASHING_RIGHT;

public class BashingRight implements BashingInterFace {
    @Override
    public State getState() {
        return RABBIT_BASHING_RIGHT;
    }

    @Override
    public boolean behave(World world, Rabbit rabbit) {
        rabbit.slopeBashHop = false;
        world.changes.removeBlockAt(Bashing.destX(rabbit), rabbit.y);
        return true;
    }
}

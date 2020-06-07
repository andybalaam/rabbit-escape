package rabbitescape.engine.behaviours.Bashing;

import rabbitescape.engine.ChangeDescription.State;
import rabbitescape.engine.behaviours.Bashing;
import rabbitescape.engine.World;
import rabbitescape.engine.Rabbit;

import static rabbitescape.engine.ChangeDescription.State.RABBIT_BASHING_UP_RIGHT;

public class BashingUpRight implements BashingInterFace {

    @Override
    public State getState() {
        return RABBIT_BASHING_UP_RIGHT;
    }

    @Override
    public boolean behave(World world, Rabbit rabbit) {
        world.changes.removeBlockAt(Bashing.destX(rabbit), rabbit.y - 1);
        rabbit.slopeBashHop = true;
        rabbit.y -= 1;
        return true;
    }
}

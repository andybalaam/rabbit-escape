package rabbitescape.engine.behaviours.Bashing;

import rabbitescape.engine.ChangeDescription.*;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.World;


public interface BashingInterFace {
    public State getState();

    public boolean behave(World world,  Rabbit rabbit);
}

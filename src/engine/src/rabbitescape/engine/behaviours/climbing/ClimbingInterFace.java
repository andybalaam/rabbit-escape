package rabbitescape.engine.behaviours.climbing;

import rabbitescape.engine.BehaviourTools;
import rabbitescape.engine.ChangeDescription.State;
import rabbitescape.engine.World;
import rabbitescape.engine.behaviours.Climbing;
import rabbitescape.engine.Rabbit;

public interface ClimbingInterFace {
    public State getState();

    public ClimbingInterFace newState(BehaviourTools t, Climbing climbing);

    public boolean behave(World world, Rabbit rabbit, Climbing climbing);
}

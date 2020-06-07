package rabbitescape.engine.behaviours;

import rabbitescape.engine.BehaviourTools;
import rabbitescape.engine.ChangeDescription.State;
import rabbitescape.engine.World;
import rabbitescape.engine.behaviours.Climbing;

public interface ClimbingInterFace {
    public State getState();

    public ClimbingInterFace newState(BehaviourTools t, Climbing climbing);

    public boolean behave(World world, Character character, Climbing climbing);
}

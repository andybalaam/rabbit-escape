package rabbitescape.engine;

import static rabbitescape.engine.Direction.*;
import rabbitescape.engine.ChangeDescription.State;

public class BehaviourTools
{
    public static State rl( Rabbit rabbit, State rightState, State leftState )
    {
        return rabbit.dir == RIGHT ? rightState : leftState;
    }
}

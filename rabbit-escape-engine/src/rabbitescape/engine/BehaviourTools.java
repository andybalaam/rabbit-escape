package rabbitescape.engine;

import static rabbitescape.engine.Direction.RIGHT;

public class BehaviourTools
{
    private final Rabbit rabbit;

    public BehaviourTools( Rabbit rabbit )
    {
        this.rabbit = rabbit;
    }

    public static ChangeDescription.State rl(
        Rabbit rabbit,
        ChangeDescription.State rightState,
        ChangeDescription.State leftState
    )
    {
        return rabbit.dir == RIGHT ? rightState : leftState;
    }
}

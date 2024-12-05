package rabbitescape.engine.behaviours;

import static rabbitescape.engine.ChangeDescription.State.*;
import static rabbitescape.engine.Token.Type.*;

import rabbitescape.engine.*;
import rabbitescape.engine.ChangeDescription.State;
public class Jumping extends Behaviour
{
    @Override
    public void cancel()
    {
    }

    @Override
    public boolean checkTriggered(Rabbit rabbit, World world)
    {
        BehaviourTools t = new BehaviourTools(rabbit, world);
        return t.pickUpToken( jump );
    }

    @Override
    public State newState(BehaviourTools t, boolean triggered)
    {
        if (triggered)
        {
            if (t.isOnUpSlope()){
                if(t.blockAboveNext() != null || t.blockAbove2Next() != null) return null;
                return t.rl(RABBIT_JUMPING_ON_UP_SLOPE_RIGHT, RABBIT_JUMPING_ON_UP_SLOPE_LEFT);
            }
            if (t.block2Next() != null || t.blockNext() != null) return null;
            if (t.isOnDownSlope()) return t.rl(RABBIT_JUMPING_ON_DOWN_SLOPE_RIGHT, RABBIT_JUMPING_ON_DOWN_SLOPE_LEFT);
            return t.rl(RABBIT_JUMPING_RIGHT, RABBIT_JUMPING_LEFT);
        }

        return null;
    }

    @Override
    public boolean behave(World world, Rabbit rabbit, State state)
    {
        if (
        state!=RABBIT_JUMPING_RIGHT && state!=RABBIT_JUMPING_LEFT
        && state!=RABBIT_JUMPING_ON_UP_SLOPE_RIGHT && state!=RABBIT_JUMPING_ON_UP_SLOPE_LEFT
        && state!=RABBIT_JUMPING_ON_DOWN_SLOPE_RIGHT && state!=RABBIT_JUMPING_ON_DOWN_SLOPE_LEFT
        ) {
            return false;
        }

        if (RABBIT_JUMPING_ON_UP_SLOPE_RIGHT == state || RABBIT_JUMPING_ON_UP_SLOPE_LEFT == state)
        {
            rabbit.y -= 1;
            rabbit.x += rabbit.dir == Direction.RIGHT ? 1 : -1;
            return true;
        }
        if (RABBIT_JUMPING_ON_DOWN_SLOPE_RIGHT == state || RABBIT_JUMPING_ON_DOWN_SLOPE_LEFT == state)
        {
            rabbit.x += rabbit.dir == Direction.RIGHT ? 1 : -1;
            return true;
        }
        rabbit.x += rabbit.dir == Direction.RIGHT ? 2 : -2;

        return true;
    }
}
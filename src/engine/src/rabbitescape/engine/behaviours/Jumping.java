package rabbitescape.engine.behaviours;

import static rabbitescape.engine.ChangeDescription.State.*;
import static rabbitescape.engine.Token.Type.*;

import rabbitescape.engine.*;
import rabbitescape.engine.ChangeDescription.State;

// 이 클래스는 토끼가 점프 토큰을 주웠을 때 1칸을 점프하는 행동을 나타낸다. 토끼는 위로가 아닌 앞으로 점프하기 때문에 위에 블록이 있어도 점프할 수 있다. 그리고 뛰어넘는 칸이 비어있어도 점프할 수 있다. 하지만 앞에 블록이 있으면 점프할 수 없다. 점프 이후에는 다시 걷는 행동으로 돌아간다.
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
        if (t.block2Next() != null || t.blockNext() != null) {
            return null;
        }

        if (triggered)
        {
            return t.rl(RABBIT_JUMPING_RIGHT, RABBIT_JUMPING_LEFT);
        }

        return null;
    }

    @Override
    public boolean behave(World world, Rabbit rabbit, State state)
    {
        if (state!=RABBIT_JUMPING_RIGHT && state!=RABBIT_JUMPING_LEFT) {
            return false;
        }

        rabbit.x += rabbit.dir == Direction.RIGHT ? 2 : -2;

        return true;
    }
}
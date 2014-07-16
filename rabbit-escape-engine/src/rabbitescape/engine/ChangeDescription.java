package rabbitescape.engine;

import java.util.ArrayList;
import java.util.List;

public class ChangeDescription
{
    public enum State
    {
        RABBIT_WALKING_LEFT,
        RABBIT_TURNING_LEFT_TO_RIGHT,
        RABBIT_WALKING_RIGHT,
        RABBIT_TURNING_RIGHT_TO_LEFT,
        RABBIT_FALLING,
        RABBIT_FALLING_1,
        RABBIT_FALLING_1_TO_DEATH,
        RABBIT_DYING_OF_FALLING_2,
        RABBIT_DYING_OF_FALLING,
        RABBIT_RISING_RIGHT_1,
        RABBIT_RISING_RIGHT_2,
        RABBIT_RISING_LEFT_1,
        RABBIT_RISING_LEFT_2,
        RABBIT_LOWERING_RIGHT_1,
        RABBIT_LOWERING_RIGHT_2,
        RABBIT_LOWERING_LEFT_1,
        RABBIT_LOWERING_LEFT_2,
    }

    public static class Change
    {
        public final int x;
        public final int y;
        public final State state;

        public Change( int x, int y, State state )
        {
            this.x = x;
            this.y = y;
            this.state = state;
        }
    }

    public List<Change> changes = new ArrayList<Change>();

    public void add( int x, int y, State state )
    {
        changes.add( new Change( x, y, state ) );
    }
}

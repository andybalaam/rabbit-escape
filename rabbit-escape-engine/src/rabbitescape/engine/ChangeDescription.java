package rabbitescape.engine;

import java.util.ArrayList;
import java.util.List;

public class ChangeDescription
{
    public enum State
    {
        NOTHING,
        RABBIT_WALKING_LEFT,
        RABBIT_TURNING_LEFT_TO_RIGHT,
        RABBIT_TURNING_LEFT_TO_RIGHT_RISING,
        RABBIT_TURNING_LEFT_TO_RIGHT_LOWERING,
        RABBIT_WALKING_RIGHT,
        RABBIT_TURNING_RIGHT_TO_LEFT,
        RABBIT_TURNING_RIGHT_TO_LEFT_RISING,
        RABBIT_TURNING_RIGHT_TO_LEFT_LOWERING,
        RABBIT_FALLING,
        RABBIT_FALLING_1,
        RABBIT_FALLING_1_TO_DEATH,
        RABBIT_DYING_OF_FALLING_2,
        RABBIT_DYING_OF_FALLING,
        RABBIT_FALLING_ONTO_LOWER_RIGHT,
        RABBIT_FALLING_ONTO_RISE_RIGHT,
        RABBIT_FALLING_ONTO_LOWER_LEFT,
        RABBIT_FALLING_ONTO_RISE_LEFT,
        RABBIT_FALLING_1_ONTO_LOWER_RIGHT,
        RABBIT_FALLING_1_ONTO_RISE_RIGHT,
        RABBIT_FALLING_1_ONTO_LOWER_LEFT,
        RABBIT_FALLING_1_ONTO_RISE_LEFT,
        RABBIT_RISING_RIGHT_START,
        RABBIT_RISING_RIGHT_CONTINUE,
        RABBIT_RISING_RIGHT_END,
        RABBIT_RISING_LEFT_START,
        RABBIT_RISING_LEFT_CONTINUE,
        RABBIT_RISING_LEFT_END,
        RABBIT_LOWERING_RIGHT_START,
        RABBIT_LOWERING_RIGHT_CONTINUE,
        RABBIT_LOWERING_RIGHT_END,
        RABBIT_LOWERING_LEFT_START,
        RABBIT_LOWERING_LEFT_CONTINUE,
        RABBIT_LOWERING_LEFT_END,
        RABBIT_LOWERING_AND_RISING_RIGHT,
        RABBIT_LOWERING_AND_RISING_LEFT,
        RABBIT_RISING_AND_LOWERING_RIGHT,
        RABBIT_RISING_AND_LOWERING_LEFT,
        RABBIT_ENTERING_EXIT,
        RABBIT_BASHING_RIGHT,
        RABBIT_BASHING_LEFT,
        RABBIT_BASHING_USELESSLY_RIGHT,
        RABBIT_BASHING_USELESSLY_LEFT,
        TOKEN_BASH_STILL,
        TOKEN_BASH_FALLING,
        TOKEN_DIG_STILL,
        TOKEN_DIG_FALLING,
        ENTRANCE,
        EXIT,
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

    public final List<Change> changes = new ArrayList<>();

    public void add( int x, int y, State state )
    {
        changes.add( new Change( x, y, state ) );
    }
}

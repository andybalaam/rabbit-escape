package rabbitescape.engine;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains the monolothic enum of rabbit and token states.
 */
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
        
        /** The flat block is two squares below where this starts.*/
        RABBIT_FALLING_1_TO_DEATH,
        
        /** Part 2 of the animation for RABBIT_FALLING_1_TO_DEATH.*/
        RABBIT_DYING_OF_FALLING_2,
        
        /** The rabbit starts on the death square.*/
        RABBIT_DYING_OF_FALLING,

        /** The slope is one square below where this starts. */
        RABBIT_DYING_OF_FALLING_SLOPE_RISE_LEFT,
        
        /** Part 2 of the animation for RABBIT_DYING_OF_FALLING_SLOPE_RISE_LEFT */
        RABBIT_DYING_OF_FALLING_SLOPE_RISE_LEFT_2,
        
        /** The slope is one square below where this starts. */
        RABBIT_DYING_OF_FALLING_SLOPE_RISE_RIGHT,
        
        /** Part 2 of the animation for RABBIT_DYING_OF_FALLING_SLOPE_RISE_RIGHT */
        RABBIT_DYING_OF_FALLING_SLOPE_RISE_RIGHT_2,
        
        /** The slope is two squares below where this starts. */
        RABBIT_DYING_OF_FALLING_2_SLOPE_RISE_RIGHT,
        
        /** Part 2 of RABBIT_DYING_OF_FALLING_2_SLOPE_RISE_RIGHT. */
        RABBIT_DYING_OF_FALLING_2_SLOPE_RISE_RIGHT_2,
        
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
        RABBIT_BASHING_UP_RIGHT,
        RABBIT_BASHING_UP_LEFT,
        RABBIT_BASHING_USELESSLY_RIGHT,
        RABBIT_BASHING_USELESSLY_LEFT,
        RABBIT_BASHING_USELESSLY_RIGHT_UP,
        RABBIT_BASHING_USELESSLY_LEFT_UP,
        RABBIT_DIGGING,
        RABBIT_DIGGING_ON_SLOPE,
        RABBIT_DIGGING_2,
        RABBIT_BRIDGING_IN_CORNER_RIGHT_1,
        RABBIT_BRIDGING_IN_CORNER_RIGHT_2,
        RABBIT_BRIDGING_IN_CORNER_RIGHT_3,
        RABBIT_BRIDGING_IN_CORNER_LEFT_1,
        RABBIT_BRIDGING_IN_CORNER_LEFT_2,
        RABBIT_BRIDGING_IN_CORNER_LEFT_3,
        RABBIT_BRIDGING_IN_CORNER_UP_RIGHT_1,
        RABBIT_BRIDGING_IN_CORNER_UP_RIGHT_2,
        RABBIT_BRIDGING_IN_CORNER_UP_RIGHT_3,
        RABBIT_BRIDGING_IN_CORNER_UP_LEFT_1,
        RABBIT_BRIDGING_IN_CORNER_UP_LEFT_2,
        RABBIT_BRIDGING_IN_CORNER_UP_LEFT_3,
        RABBIT_BRIDGING_RIGHT_1,
        RABBIT_BRIDGING_RIGHT_2,
        RABBIT_BRIDGING_RIGHT_3,
        RABBIT_BRIDGING_LEFT_1,
        RABBIT_BRIDGING_LEFT_2,
        RABBIT_BRIDGING_LEFT_3,
        RABBIT_BRIDGING_UP_RIGHT_1,
        RABBIT_BRIDGING_UP_RIGHT_2,
        RABBIT_BRIDGING_UP_RIGHT_3,
        RABBIT_BRIDGING_UP_LEFT_1,
        RABBIT_BRIDGING_UP_LEFT_2,
        RABBIT_BRIDGING_UP_LEFT_3,
        RABBIT_BRIDGING_DOWN_UP_RIGHT_1,
        RABBIT_BRIDGING_DOWN_UP_RIGHT_2,
        RABBIT_BRIDGING_DOWN_UP_RIGHT_3,
        RABBIT_BRIDGING_DOWN_UP_LEFT_1,
        RABBIT_BRIDGING_DOWN_UP_LEFT_2,
        RABBIT_BRIDGING_DOWN_UP_LEFT_3,
        RABBIT_CLIMBING_LEFT_START,
        RABBIT_CLIMBING_LEFT_CONTINUE_1,
        RABBIT_CLIMBING_LEFT_CONTINUE_2,
        RABBIT_CLIMBING_LEFT_END,
        RABBIT_CLIMBING_LEFT_BANG_HEAD,
        RABBIT_CLIMBING_RIGHT_START,
        RABBIT_CLIMBING_RIGHT_CONTINUE_1,
        RABBIT_CLIMBING_RIGHT_CONTINUE_2,
        RABBIT_CLIMBING_RIGHT_END,
        RABBIT_CLIMBING_RIGHT_BANG_HEAD,
        RABBIT_BLOCKING,
        RABBIT_OUT_OF_BOUNDS,
        RABBIT_EXPLODING,
        TOKEN_BASH_STILL,
        TOKEN_BASH_FALLING,
        TOKEN_DIG_STILL,
        TOKEN_DIG_FALLING,
        TOKEN_BRIDGE_STILL,
        TOKEN_BRIDGE_FALLING,
        TOKEN_BLOCK_STILL,
        TOKEN_BLOCK_FALLING,
        TOKEN_CLIMB_STILL,
        TOKEN_CLIMB_FALLING,
        TOKEN_EXPLODE_FALLING,
        TOKEN_EXPLODE_STILL,
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

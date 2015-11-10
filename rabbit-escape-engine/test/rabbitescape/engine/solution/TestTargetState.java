package rabbitescape.engine.solution;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.Test;

import rabbitescape.engine.World;

public class TestTargetState
{
    @Test
    public void Equal_actions_are_equal()
    {
        AssertStateAction instr1 = new AssertStateAction( World.CompletionState.RUNNING );
        AssertStateAction instr2 = new AssertStateAction( World.CompletionState.RUNNING );

        assertThat( instr1, equalTo( instr2 ) );
        assertThat( instr1.hashCode(), equalTo( instr2.hashCode() ) );
    }

    @Test
    public void Different_actions_are_unequal()
    {
        AssertStateAction instr1 = new AssertStateAction( World.CompletionState.RUNNING );
        AssertStateAction instr2 = new AssertStateAction( World.CompletionState.LOST );

        assertThat( instr1, not( equalTo( instr2 ) ) );

        // Note: technically a correct hashCode could fail this, but it's
        //       desirable that in most cases it won't.
        assertThat(
            instr1.hashCode(), not( equalTo( instr2.hashCode() ) ) );
    }
}

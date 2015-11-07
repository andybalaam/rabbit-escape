package rabbitescape.engine.solution;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.Test;

import rabbitescape.engine.World;

public class TestTargetState
{
    @Test
    public void Equal_instructions_are_equal()
    {
        TargetState instr1 = new TargetState(
            World.CompletionState.RUNNING, 2 );

        TargetState instr2 = new TargetState(
            World.CompletionState.RUNNING, 2 );

        assertThat( instr1, equalTo( instr2 ) );
        assertThat( instr1.hashCode(), equalTo( instr2.hashCode() ) );
    }

    @Test
    public void Different_instructions_are_unequal()
    {
        TargetState instr1 = new TargetState(
            World.CompletionState.RUNNING, 2 );

        TargetState instr2 = new TargetState(
            World.CompletionState.LOST, 2 );

        assertThat( instr1, not( equalTo( instr2 ) ) );

        // Note: technically a correct hashCode could fail this, but it's
        //       desirable that in most cases it won't.
        assertThat(
            instr1.hashCode(), not( equalTo( instr2.hashCode() ) ) );
    }
}

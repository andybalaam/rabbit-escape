package rabbitescape.engine.solution;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.*;

public class TestWaitAction
{
    @Test
    public void Equal_wait_action_are_equal()
    {
        WaitAction instr1 = new WaitAction( 5 );
        WaitAction instr2 = new WaitAction( 5 );

        assertThat( instr1, equalTo( instr2 ) );
        assertThat( instr1.hashCode(), equalTo( instr2.hashCode() ) );
    }

    @Test
    public void Different_wait_times_make_them_unequal()
    {
        WaitAction instr1 = new WaitAction( 5 );
        WaitAction instr2 = new WaitAction( 4 );

        assertThat( instr1, not( equalTo( instr2 ) ) );

        // Note: technically a correct hashCode could fail this, but it's
        //       desirable that in most cases it won't.
        assertThat(
            instr1.hashCode(), not( equalTo( instr2.hashCode() ) ) );
    }
}

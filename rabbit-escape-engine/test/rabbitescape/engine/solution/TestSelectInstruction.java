package rabbitescape.engine.solution;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.Test;

import rabbitescape.engine.Token;

public class TestSelectInstruction
{
    @Test
    public void Equal_instructions_are_equal()
    {
        SelectInstruction instr1 = new SelectInstruction( Token.Type.bridge );
        SelectInstruction instr2 = new SelectInstruction( Token.Type.bridge );

        assertThat( instr1, equalTo( instr2 ) );
        assertThat( instr1.hashCode(), equalTo( instr2.hashCode() ) );
    }

    @Test
    public void Different_instructions_are_unequal()
    {
        SelectInstruction instr1 = new SelectInstruction( Token.Type.bridge );
        SelectInstruction instr2 = new SelectInstruction( Token.Type.bash );

        assertThat( instr1, not( equalTo( instr2 ) ) );

        // Note: technically a correct hashCode could fail this, but it's
        //       desirable that in most cases it won't.
        assertThat(
            instr1.hashCode(), not( equalTo( instr2.hashCode() ) ) );
    }
}

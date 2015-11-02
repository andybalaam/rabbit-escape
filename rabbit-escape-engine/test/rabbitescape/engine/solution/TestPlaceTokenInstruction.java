package rabbitescape.engine.solution;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.*;

public class TestPlaceTokenInstruction
{
    @Test
    public void Equal_instructions_are_equal()
    {
        PlaceTokenInstruction instr1 = new PlaceTokenInstruction( 5, 4 );
        PlaceTokenInstruction instr2 = new PlaceTokenInstruction( 5, 4 );

        assertThat( instr1, equalTo( instr2 ) );
        assertThat( instr1.hashCode(), equalTo( instr2.hashCode() ) );
    }

    @Test
    public void Different_instructions_are_unequal()
    {
        PlaceTokenInstruction instr1 = new PlaceTokenInstruction( 5, 4 );
        PlaceTokenInstruction instr2 = new PlaceTokenInstruction( 4, 5 );

        assertThat( instr1, not( equalTo( instr2 ) ) );

        // Note: technically a correct hashCode could fail this, but it's
        //       desirable that in most cases it won't.
        assertThat(
            instr1.hashCode(), not( equalTo( instr2.hashCode() ) ) );
    }
}

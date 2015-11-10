package rabbitescape.engine.solution;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.Test;

import rabbitescape.engine.Token;

public class TestSelectAction
{
    @Test
    public void Equal_actions_are_equal()
    {
        SelectAction a1 = new SelectAction( Token.Type.bridge );
        SelectAction a2 = new SelectAction( Token.Type.bridge );

        assertThat( a1, equalTo( a2 ) );
        assertThat( a1.hashCode(), equalTo( a2.hashCode() ) );
    }

    @Test
    public void Different_actions_are_unequal()
    {
        SelectAction a1 = new SelectAction( Token.Type.bridge );
        SelectAction a2 = new SelectAction( Token.Type.bash );

        assertThat( a1, not( equalTo( a2 ) ) );

        // Note: technically a correct hashCode could fail this, but it's
        //       desirable that in most cases it won't.
        assertThat(
            a1.hashCode(), not( equalTo( a2.hashCode() ) ) );
    }
}

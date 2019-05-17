package rabbitescape.engine;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static rabbitescape.engine.ChangeDescription.State.*;

import org.junit.Test;

public class TestRabbit
{
    @Test
    public void Rabbit_reports_state_in_lowercase()
    {
        Rabbit r = new Rabbit( 1, 1, Direction.LEFT, Rabbit.Type.RABBIT );
        r.state = RABBIT_WALKING_LEFT;
        assertThat(r.stateName(), equalTo("rabbit_walking_left"));
    }

    @Test
    public void Rabbot_reports_state_except_it_says_rabbot()
    {
        Rabbit r = new Rabbit( 1, 1, Direction.LEFT, Rabbit.Type.RABBOT );
        r.state = RABBIT_WALKING_LEFT;
        assertThat(r.stateName(), equalTo("rabbot_walking_left"));
    }
}

package rabbitescape.engine.solution;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.Test;

public class TestSolutionTimeStep
{
    @Test
    public void Identical_empty_timesteps_are_equal()
    {
        SolutionTimeStep ts1 = new SolutionTimeStep();
        SolutionTimeStep ts2 = new SolutionTimeStep();

        assertThat( ts1, equalTo( ts2 ) );

        assertThat( ts1.hashCode(), equalTo( ts2.hashCode() ) );
    }
}

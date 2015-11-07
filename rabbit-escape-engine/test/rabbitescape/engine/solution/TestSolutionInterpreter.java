package rabbitescape.engine.solution;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import static rabbitescape.engine.util.Util.*;

import org.junit.*;

import java.util.Arrays;
import java.util.Iterator;

public class TestSolutionInterpreter
{
    @Test
    public void Empty_solution_does_nothing()
    {
        Solution solution = new Solution();
        SolutionInterpreter interpreter = new SolutionInterpreter( solution );

        Iterator<SolutionTimeStep> it = interpreter.iterator();

        assertThat( it.hasNext(), is( false ) );
    }

    @Test
    public void One_wait_waits_for_that_long()
    {
        Solution solution = new Solution(
            new SolutionStep( new WaitInstruction( 3 ) ) );

        SolutionInterpreter interpreter = new SolutionInterpreter( solution );

        assertThat(
            list( interpreter ),
            equalTo(
                Arrays.asList(
                      new SolutionTimeStep()
                    , new SolutionTimeStep()
                    , new SolutionTimeStep()
                )
            )
        );
    }

    @Test
    public void Multiple_waits_wait_for_the_total()
    {
        Solution solution = new Solution(
              new SolutionStep( new WaitInstruction( 1 ) )
            , new SolutionStep( new WaitInstruction( 2 ) )
            , new SolutionStep( new WaitInstruction( 3 ) )
        );

        SolutionInterpreter interpreter = new SolutionInterpreter( solution );

        assertThat(
            list( interpreter ),
            equalTo(
                Arrays.asList(
                      new SolutionTimeStep()
                    , new SolutionTimeStep()
                    , new SolutionTimeStep()
                    , new SolutionTimeStep()
                    , new SolutionTimeStep()
                    , new SolutionTimeStep()
                )
            )
        );
    }
}

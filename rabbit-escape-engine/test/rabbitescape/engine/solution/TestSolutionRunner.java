package rabbitescape.engine.solution;

import static org.junit.Assert.fail;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Arrays;

import org.junit.Test;

import rabbitescape.engine.World;
import rabbitescape.engine.World.CompletionState;
import rabbitescape.engine.solution.SolutionExceptions;
import rabbitescape.engine.textworld.TextWorldManip;

public class TestSolutionRunner
{
    @Test( expected = SolutionExceptions.UnexpectedState.class )
    public void Unexpected_state_is_an_error()
    {
        SolutionRunner.runSolution(
            expectingSolution( CompletionState.LOST ), neverEndingWorld() );
    }

    @Test
    public void Unexpected_state_is_serialised_to_helpful_message()
    {
        try
        {
            SolutionRunner.runSolution(
                expectingSolution( CompletionState.LOST ), neverEndingWorld() );
            fail( "Expected exception!" );
        }
        catch( SolutionExceptions.UnexpectedState e )
        {
            e.solutionId = 3;

            assertThat(
                e.getMessage(),
                equalTo(
                    "Solution failed: state was RUNNING but we expected LOST"
                    + " at instruction 1 of solution 3."
                )
            );
        }
    }

    @Test( expected = SolutionExceptions.DidNotWin.class )
    public void Failing_unexpectedly_is_an_error()
    {
        SolutionRunner.runSolution(
            expectingSolution( CompletionState.WON ), neverEndingWorld() );
    }

    @Test
    public void Failing_unexpectedly_is_serialised_to_helpful_message()
    {
        try
        {
            SolutionRunner.runSolution(
                expectingSolution( CompletionState.WON ), neverEndingWorld() );
            fail( "Expected exception!" );
        }
        catch( SolutionExceptions.UnexpectedState e )
        {
            e.solutionId = 4;

            assertThat(
                e.getMessage(),
                equalTo(
                    "Solution failed: We expected to win, but the state was"
                    + " RUNNING at instruction 1 of solution 4."
                )
            );
        }
    }

    // --

    private World neverEndingWorld()
    {
        return TextWorldManip.createWorld(
            "#r  #",
            "#####"
        );
    }

    private Solution expectingSolution( CompletionState expected )
    {
        return new Solution(
            Arrays.asList(
                new Instruction[]
                {
                    new TargetState( expected, 1 )
                }
            )
        );
    }
}

package rabbitescape.engine.solution;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.*;

import rabbitescape.engine.Token;
import rabbitescape.engine.World;

public class TestSolutionStep
{
    @Test
    public void Equal_solution_steps_are_equal()
    {
        SolutionStep solution1 = makeSolutionStep( 3 );
        SolutionStep solution2 = makeSolutionStep( 3 );

        assertThat( solution1, equalTo( solution2 ) );
        assertThat( solution1.hashCode(), equalTo( solution2.hashCode() ) );
    }

    @Test
    public void Different_instruction_lists_make_them_unequal()
    {
        SolutionStep solution1 = makeSolutionStep( 3 );
        SolutionStep solution2 = makeShortSolutionStep( 3 );

        assertThat( solution1, not( equalTo( solution2 ) ) );

        // Note: technically a correct hashCode could fail this, but it's
        //       desirable that in most cases it won't.
        assertThat(
            solution1.hashCode(), not( equalTo( solution2.hashCode() ) ) );
    }

    @Test
    public void Different_instructions_make_them_unequal()
    {
        SolutionStep solution1 = makeSolutionStep( 3 );
        SolutionStep solution2 = makeSolutionStep( 2 );

        assertThat( solution1, not( equalTo( solution2 ) ) );

        // Note: technically a correct hashCode could fail this, but it's
        //       desirable that in most cases it won't.
        assertThat(
            solution1.hashCode(), not( equalTo( solution2.hashCode() ) ) );
    }

    // ---

    private static SolutionStep makeSolutionStep( int waitTime )
    {
        return new SolutionStep(
              new WaitInstruction( waitTime )
            , new PlaceTokenInstruction( 3, 2 )
            , new SelectInstruction( Token.Type.block )
            , new TargetState( World.CompletionState.RUNNING )
        );
    }

    private static SolutionStep makeShortSolutionStep( int waitTime )
    {
        return new SolutionStep(
              new WaitInstruction( waitTime )
            , new PlaceTokenInstruction( 3, 2 )
            , new SelectInstruction( Token.Type.block )
        );
    }
}

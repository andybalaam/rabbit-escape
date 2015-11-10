package rabbitescape.engine.solution;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.*;

import rabbitescape.engine.Token;
import rabbitescape.engine.World;

public class TestSolutionCommand
{
    @Test
    public void Equal_solution_commands_are_equal()
    {
        SolutionCommand solution1 = makeSolutionCommand( 3 );
        SolutionCommand solution2 = makeSolutionCommand( 3 );

        assertThat( solution1, equalTo( solution2 ) );
        assertThat( solution1.hashCode(), equalTo( solution2.hashCode() ) );
    }

    @Test
    public void Different_action_lists_make_them_unequal()
    {
        SolutionCommand solution1 = makeSolutionCommand( 3 );
        SolutionCommand solution2 = makeShortSolutionCommand( 3 );

        assertThat( solution1, not( equalTo( solution2 ) ) );

        // Note: technically a correct hashCode could fail this, but it's
        //       desirable that in most cases it won't.
        assertThat(
            solution1.hashCode(), not( equalTo( solution2.hashCode() ) ) );
    }

    @Test
    public void Different_actions_make_them_unequal()
    {
        SolutionCommand solution1 = makeSolutionCommand( 3 );
        SolutionCommand solution2 = makeSolutionCommand( 2 );

        assertThat( solution1, not( equalTo( solution2 ) ) );

        // Note: technically a correct hashCode could fail this, but it's
        //       desirable that in most cases it won't.
        assertThat(
            solution1.hashCode(), not( equalTo( solution2.hashCode() ) ) );
    }

    // ---

    private static SolutionCommand makeSolutionCommand( int waitTime )
    {
        return new SolutionCommand(
              new WaitAction( waitTime )
            , new PlaceTokenAction( 3, 2 )
            , new SelectAction( Token.Type.block )
            , new AssertStateAction( World.CompletionState.RUNNING )
        );
    }

    private static SolutionCommand makeShortSolutionCommand( int waitTime )
    {
        return new SolutionCommand(
              new WaitAction( waitTime )
            , new PlaceTokenAction( 3, 2 )
            , new SelectAction( Token.Type.block )
        );
    }
}

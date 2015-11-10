package rabbitescape.engine.solution;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.*;

import rabbitescape.engine.Token;
import rabbitescape.engine.World;

public class TestSolution
{
    @Test
    public void Equal_solutions_are_equal()
    {
        Solution solution1 = makeSolution( 3 );
        Solution solution2 = makeSolution( 3 );

        assertThat( solution1, equalTo( solution2 ) );
        assertThat( solution1.hashCode(), equalTo( solution2.hashCode() ) );
    }

    @Test
    public void Different_command_lists_make_them_unequal()
    {
        Solution solution1 = makeSolution( 3 );
        Solution solution2 = makeShortSolution( 3 );

        assertThat( solution1, not( equalTo( solution2 ) ) );

        // Note: technically a correct hashCode could fail this, but it's
        //       desirable that in most cases it won't.
        assertThat(
            solution1.hashCode(), not( equalTo( solution2.hashCode() ) ) );
    }

    @Test
    public void Different_commands_make_them_unequal()
    {
        Solution solution1 = makeSolution( 3 );
        Solution solution2 = makeSolution( 2 );

        assertThat( solution1, not( equalTo( solution2 ) ) );

        // Note: technically a correct hashCode could fail this, but it's
        //       desirable that in most cases it won't.
        assertThat(
            solution1.hashCode(), not( equalTo( solution2.hashCode() ) ) );
    }

    // ---

    private static Solution makeSolution( int waitTime )
    {
        return new Solution(
              new SolutionCommand( new WaitAction( waitTime ) )
            , new SolutionCommand( new PlaceTokenAction( 3, 2 ) )
            , new SolutionCommand( new SelectAction( Token.Type.block ) )
            , new SolutionCommand(
                new AssertStateAction( World.CompletionState.RUNNING ) )
        );
    }

    private static Solution makeShortSolution( int waitTime )
    {
        return new Solution(
              new SolutionCommand( new WaitAction( waitTime ) )
            , new SolutionCommand( new PlaceTokenAction( 3, 2 ) )
            , new SolutionCommand( new SelectAction( Token.Type.block ) )
        );
    }
}

package rabbitescape.engine.solution;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.Arrays;
import org.junit.*;

import rabbitescape.engine.Token;
import rabbitescape.engine.World;

public class TestSolution
{
    @Test
    public void Equal_solutions_are_equal()
    {
        Solution solution1 = makeSolution( 1, 3 );
        Solution solution2 = makeSolution( 1, 3 );

        assertThat( solution1, equalTo( solution2 ) );
        assertThat( solution1.hashCode(), equalTo( solution2.hashCode() ) );
    }

    @Test
    public void Different_solution_ids_make_them_unequal()
    {
        Solution solution1 = makeSolution( 1, 3 );
        Solution solution2 = makeSolution( 2, 3 );

        assertThat( solution1, not( equalTo( solution2 ) ) );

        // Note: technically a correct hashCode could fail this, but it's
        //       desirable that in most cases it won't.
        assertThat(
            solution1.hashCode(), not( equalTo( solution2.hashCode() ) ) );
    }

    @Test
    public void Different_instruction_lists_make_them_unequal()
    {
        Solution solution1 = makeSolution( 1, 3 );
        Solution solution2 = makeShortSolution( 1, 3 );

        assertThat( solution1, not( equalTo( solution2 ) ) );

        // Note: technically a correct hashCode could fail this, but it's
        //       desirable that in most cases it won't.
        assertThat(
            solution1.hashCode(), not( equalTo( solution2.hashCode() ) ) );
    }

    @Test
    public void Different_instructions_make_them_unequal()
    {
        Solution solution1 = makeSolution( 1, 3 );
        Solution solution2 = makeSolution( 1, 2 );

        assertThat( solution1, not( equalTo( solution2 ) ) );

        // Note: technically a correct hashCode could fail this, but it's
        //       desirable that in most cases it won't.
        assertThat(
            solution1.hashCode(), not( equalTo( solution2.hashCode() ) ) );
    }

    // ---

    private static Solution makeSolution( int solutionId, int waitTime )
    {
        return new Solution(
            solutionId,
            Arrays.asList(
                new Instruction[]
                {
                      new WaitInstruction( waitTime )
                    , new PlaceTokenInstruction( 3, 2 )
                    , new SelectInstruction( Token.Type.block )
                    , new TargetState(
                        World.CompletionState.RUNNING, 1, 4 )
                }
            )
        );
    }

    private static Solution makeShortSolution( int solutionId, int waitTime )
    {
        return new Solution(
            solutionId,
            Arrays.asList(
                new Instruction[]
                {
                      new WaitInstruction( waitTime )
                    , new PlaceTokenInstruction( 3, 2 )
                    , new SelectInstruction( Token.Type.block )
                }
            )
        );
    }
}

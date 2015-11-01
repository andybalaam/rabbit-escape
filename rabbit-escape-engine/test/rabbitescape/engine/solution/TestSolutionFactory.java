package rabbitescape.engine.solution;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import rabbitescape.engine.Token;
import rabbitescape.engine.World;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

public class TestSolutionFactory
{
    @Test
    public void Empty_string_gives_empty_solution()
    {
        assertThat(
            SolutionFactory.create( "", 1 ),
            equalTo( new Solution( 1, new ArrayList<Instruction>() ) )
        );
    }

    @Test
    public void Single_status_gives_single_validation_instruction()
    {
        assertThat(
            SolutionFactory.create( "WON", 1 ),
            equalTo(
                new Solution(
                    1,
                    Arrays.asList(
                        new Instruction[]
                        {
                            new TargetState( World.CompletionState.WON, 1, 0 )
                        }
                    )
                )
            )
        );
    }

    @Test
    public void Multiple_instructions_are_found_and_listed()
    {
        assertThat(
            SolutionFactory.create( "1;2;WON", 1 ),
            equalTo(
                new Solution(
                    1,
                    Arrays.asList(
                        new Instruction[]
                        {
                            new WaitInstruction( 1 ),
                            new WaitInstruction( 2 ),
                            new TargetState( World.CompletionState.WON, 1, 2 )
                        }
                    )
                )
            )
        );
    }

    @Test
    public void Simultaneous_instructions_are_noted()
    {
        assertThat(
            SolutionFactory.create( "bash&(1,1)&3;WON", 1 ),
            equalTo(
                new Solution(
                    1,
                    Arrays.asList(
                        new Instruction[]
                        {
                            new SelectInstruction( Token.Type.bash ),
                            new PlaceTokenInstruction( 1, 1 ),
                            new WaitInstruction( 3 ),
                            new TargetState( World.CompletionState.WON, 1, 1 )
                        }
                    )
                )
            )
        );
    }

    @Test
    public void Nonwait_instructions_get_a_wait_appended()
    {
        assertThat(
            SolutionFactory.create( "bridge;LOST", 1 ),
            equalTo(
                new Solution(
                    1,
                    Arrays.asList(
                        new Instruction[]
                        {
                            new SelectInstruction( Token.Type.bridge ),
                            new WaitInstruction( 1 ),
                            new TargetState( World.CompletionState.LOST, 1, 1 )
                        }
                    )
                )
            )
        );
    }

    @Test
    public void If_not_specified_assert_we_win_at_end()
    {
        assertThat(
            SolutionFactory.create( "bridge;(2,4)", 2 ),
            equalTo(
                new Solution(
                    2,
                    Arrays.asList(
                        new Instruction[]
                        {
                            new SelectInstruction( Token.Type.bridge ),
                            new WaitInstruction( 1 ),
                            new PlaceTokenInstruction( 2, 4 ),
                            new TargetState( World.CompletionState.WON, 2, 3 )
                        }
                    )
                )
            )
        );
    }
}

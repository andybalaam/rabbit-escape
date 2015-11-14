package rabbitescape.engine.solution;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rabbitescape.engine.Token;
import rabbitescape.engine.World.CompletionState;

public class TestSolutionInterpreter
{
    private static final CompletionState R = CompletionState.RUNNING;
    private static final CompletionState W = CompletionState.WON;
    //private static final CompletionState L = CompletionState.LOST;

    @Test
    public void Empty_solution_does_nothing()
    {
        Solution solution = new Solution();

        SolutionInterpreter interpreter =
            new SolutionInterpreter( solution, false );

        assertThat( interpreter.next(), nullValue() );
    }

    @Test
    public void One_wait_waits_for_that_long()
    {
        Solution solution = new Solution(
            new SolutionCommand( new WaitAction( 3 ) ) );

        SolutionInterpreter interpreter =
            new SolutionInterpreter(solution, false );

        assertThat(
            interpreterList( interpreter ),
            equalTo(
                Arrays.asList(
                      new SolutionTimeStep( 1 )
                    , new SolutionTimeStep( 1 )
                    , new SolutionTimeStep( 1 )
                )
            )
        );
    }

    @Test
    public void Multiple_waits_wait_for_the_total()
    {
        Solution solution = new Solution(
              new SolutionCommand(
                  new WaitAction( 1 ), new WaitAction( 2 ) )
            , new SolutionCommand( new WaitAction( 3 ) )
        );

        SolutionInterpreter interpreter =
            new SolutionInterpreter( solution, false );

        assertThat(
            interpreterList( interpreter ),
            equalTo(
                Arrays.asList(
                      new SolutionTimeStep( 1 )
                    , new SolutionTimeStep( 1 )
                    , new SolutionTimeStep( 1 )
                    , new SolutionTimeStep( 2 )
                    , new SolutionTimeStep( 2 )
                    , new SolutionTimeStep( 2 )
                )
            )
        );
    }

    @Test
    public void Single_nonwait_action_makes_single_time_step()
    {
        Solution solution = new Solution(
            new SolutionCommand( new SelectAction( Token.Type.explode ) )
        );

        SolutionInterpreter interpreter =
            new SolutionInterpreter( solution, false );

        assertThat(
            interpreterList( interpreter ),
            equalTo(
                Arrays.asList(
                    new SolutionTimeStep(
                        1, new SelectAction( Token.Type.explode ) )
                )
            )
        );
    }

    @Test
    public void Multiple_nonwait_actions_in_single_cmd_make_1_time_step()
    {
        Solution solution = new Solution(
            new SolutionCommand(
                new SelectAction( Token.Type.explode ),
                new PlaceTokenAction( 2, 2 )
            )
        );

        SolutionInterpreter interpreter =
            new SolutionInterpreter( solution, false );

        assertThat(
            interpreterList( interpreter ),
            equalTo(
                Arrays.asList(
                    new SolutionTimeStep(
                        1
                        , new SelectAction( Token.Type.explode )
                        , new PlaceTokenAction( 2, 2 )
                    )
                )
            )
        );
    }

    @Test
    public void Do_then_wait_then_do_translates_into_time_steps()
    {
        Solution solution = new Solution(
            new SolutionCommand(
                new SelectAction( Token.Type.explode ),
                new PlaceTokenAction( 2, 2 )
            ),
            new SolutionCommand( new WaitAction( 4 ) ),
            new SolutionCommand( new PlaceTokenAction( 3, 2 ) )
        );

        SolutionInterpreter interpreter =
            new SolutionInterpreter( solution, false );

        assertThat(
            interpreterList( interpreter ),
            equalTo(
                Arrays.asList(
                    new SolutionTimeStep(
                        1
                        , new SelectAction( Token.Type.explode )
                        , new PlaceTokenAction( 2, 2 )
                    ),
                    new SolutionTimeStep( 2 ),
                    new SolutionTimeStep( 2 ),
                    new SolutionTimeStep( 2 ),
                    new SolutionTimeStep( 2 ),
                    new SolutionTimeStep( 3, new PlaceTokenAction( 3, 2 ) )
                )
            )
        );
    }

    @Test
    public void Wait_then_do_then_wait_translates_into_time_steps()
    {
        Solution solution = new Solution(
            new SolutionCommand(),
            new SolutionCommand( new WaitAction( 1 ) ),
            new SolutionCommand(
                new SelectAction( Token.Type.explode ),
                new PlaceTokenAction( 2, 2 )
            ),
            new SolutionCommand( new WaitAction( 2 ) ),
            new SolutionCommand()
        );

        SolutionInterpreter interpreter = new SolutionInterpreter(
            solution, false );

        assertThat(
            interpreterList( interpreter ),
            equalTo(
                Arrays.asList(
                    new SolutionTimeStep( 1 ),
                    new SolutionTimeStep( 2 ),
                    new SolutionTimeStep(
                          3
                        , new SelectAction( Token.Type.explode )
                        , new PlaceTokenAction( 2, 2 )
                    ),
                    new SolutionTimeStep( 4 ),
                    new SolutionTimeStep( 4 ),
                    new SolutionTimeStep( 5 )
                )
            )
        );
    }

    @Test
    public void Waits_mixed_with_dos_wait_after_doing_no_matter_the_order()
    {
        Solution solution = new Solution(
            new SolutionCommand(
                new WaitAction( 3 ),
                new SelectAction( Token.Type.explode ),
                new PlaceTokenAction( 2, 2 )
            ),
            new SolutionCommand( new WaitAction( 2 ) ),
            new SolutionCommand(
                new SelectAction( Token.Type.dig ),
                new PlaceTokenAction( 1, 1 ),
                new WaitAction( 3 )
            )
        );

        SolutionInterpreter interpreter =
            new SolutionInterpreter( solution, false );

        assertThat(
            interpreterList( interpreter ),
            equalTo(
                Arrays.asList(
                    new SolutionTimeStep(
                        1
                        , new SelectAction( Token.Type.explode )
                        , new PlaceTokenAction( 2, 2 )
                    ),
                    new SolutionTimeStep( 1 ),
                    new SolutionTimeStep( 1 ),

                    new SolutionTimeStep( 2 ),
                    new SolutionTimeStep( 2 ),

                    new SolutionTimeStep(
                        3,
                        new SelectAction( Token.Type.dig ),
                        new PlaceTokenAction( 1, 1 )
                    ),
                    new SolutionTimeStep( 3 ),
                    new SolutionTimeStep( 3 )
                )
            )
        );
    }

    @Test
    public void Empty_command_is_like_wait_1()
    {
        Solution solution = new Solution(
            new SolutionCommand()
        );

        SolutionInterpreter interpreter =
            new SolutionInterpreter( solution, false );

        assertThat(
            interpreterList( interpreter ),
            equalTo(
                Arrays.asList(
                    new SolutionTimeStep( 1 )
                )
            )
        );
    }

    @Test
    public void Many_empty_commands()
    {
        Solution solution = new Solution(
            new SolutionCommand(),
            new SolutionCommand( new SelectAction( Token.Type.dig ) ),
            new SolutionCommand( new PlaceTokenAction( 1, 1 ) ),
            new SolutionCommand(
                new AssertStateAction( CompletionState.RUNNING ) ),
            new SolutionCommand(),
            new SolutionCommand(),
            new SolutionCommand()
        );

        SolutionInterpreter interpreter =
            new SolutionInterpreter( solution, false );

        assertThat(
            interpreterList( interpreter ),
            equalTo(
                Arrays.asList(
                    new SolutionTimeStep( 1 ),

                    new SolutionTimeStep(
                        2, new SelectAction( Token.Type.dig ) ),

                    new SolutionTimeStep( 3, new PlaceTokenAction( 1, 1 ) ),

                    new SolutionTimeStep(
                        4, new AssertStateAction( CompletionState.RUNNING ) ),

                    new SolutionTimeStep( 5 ),

                    new SolutionTimeStep( 6 ),

                    new SolutionTimeStep( 7 )
                )
            )
        );
    }

    @Test
    public void If_no_commands_we_do_a_final_assert()
    {
        Solution solution = new Solution();

        SolutionInterpreter interpreter = new SolutionInterpreter( solution );

        assertThat(
            interpreterList( interpreter ),
            equalTo(
                Arrays.asList(
                    new SolutionTimeStep(
                        1, new AssertStateAction( CompletionState.WON ) )
                )
            )
        );
    }

    @Test
    public void If_normal_commands_we_do_a_final_assert()
    {
        Solution solution = new Solution(
            new SolutionCommand(
                new SelectAction( Token.Type.dig ),
                new PlaceTokenAction( 1, 1 ) ),
            new SolutionCommand( new PlaceTokenAction( 1, 1 ) )
        );

        SolutionInterpreter interpreter = new SolutionInterpreter( solution );

        assertThat(
            interpreterList( interpreter ),
            equalTo(
                Arrays.asList(
                    new SolutionTimeStep(
                        1,
                        new SelectAction( Token.Type.dig ),
                        new PlaceTokenAction( 1, 1 )
                    ),
                    new SolutionTimeStep( 2, new PlaceTokenAction( 1, 1 ) ),
                    new SolutionTimeStep(
                        3, new AssertStateAction( CompletionState.WON ) )
                )
            )
        );
    }

    @Test
    public void If_last_command_is_empty_we_do_a_final_assert()
    {
        Solution solution = new Solution(
            new SolutionCommand(
                new SelectAction( Token.Type.dig ),
                new PlaceTokenAction( 1, 1 ),
                new WaitAction( 3 )
            ),
            new SolutionCommand( new PlaceTokenAction( 1, 1 ) ),
            new SolutionCommand()
        );

        SolutionInterpreter interpreter = new SolutionInterpreter( solution );

        assertThat(
            interpreterList( interpreter ),
            equalTo(
                Arrays.asList(
                    new SolutionTimeStep(
                        1,
                        new SelectAction( Token.Type.dig ),
                        new PlaceTokenAction( 1, 1 )
                    ),
                    new SolutionTimeStep( 1 ),
                    new SolutionTimeStep( 1 ),
                    new SolutionTimeStep( 2, new PlaceTokenAction( 1, 1 ) ),
                    new SolutionTimeStep( 3 ),
                    new SolutionTimeStep(
                        4, new AssertStateAction( CompletionState.WON ) )
                )
            )
        );
    }

    @Test
    public void If_last_command_is_assert_we_do_not_add_an_assert()
    {
        Solution solution = new Solution(
            new SolutionCommand(
                new SelectAction( Token.Type.dig ),
                new PlaceTokenAction( 1, 1 ) ),
            new SolutionCommand( new PlaceTokenAction( 1, 1 ) ),
            new SolutionCommand(
                new AssertStateAction( CompletionState.LOST ) )
        );

        SolutionInterpreter interpreter = new SolutionInterpreter( solution );

        assertThat(
            interpreterList( interpreter ),
            equalTo(
                Arrays.asList(
                    new SolutionTimeStep(
                        1,
                        new SelectAction( Token.Type.dig ),
                        new PlaceTokenAction( 1, 1 )
                    ),
                    new SolutionTimeStep( 2, new PlaceTokenAction( 1, 1 ) ),
                    new SolutionTimeStep(
                        3, new AssertStateAction( CompletionState.LOST ) )
                )
            )
        );
    }

    @Test
    public void If_assert_then_wait_we_do_add_a_final_assert()
    {
        Solution solution = new Solution(
            new SolutionCommand(
                new SelectAction( Token.Type.dig ),
                new PlaceTokenAction( 1, 1 )
            ),
            new SolutionCommand( new PlaceTokenAction( 1, 1 ) ),
            new SolutionCommand(
                new AssertStateAction( CompletionState.WON ) ),
            new SolutionCommand()
        );

        SolutionInterpreter interpreter = new SolutionInterpreter( solution );

        assertThat(
            interpreterList( interpreter ),
            equalTo(
                Arrays.asList(
                    new SolutionTimeStep(
                        1,
                        new SelectAction( Token.Type.dig ),
                        new PlaceTokenAction( 1, 1 )
                    ),
                    new SolutionTimeStep( 2, new PlaceTokenAction( 1, 1 ) ),
                    new SolutionTimeStep(
                        3, new AssertStateAction( CompletionState.WON ) ),
                    new SolutionTimeStep( 4 ),
                    new SolutionTimeStep(
                        5, new AssertStateAction( CompletionState.WON ) )
                )
            )
        );
    }

    @Test
    public void Until_stops_with_assert_when_not_running()
    {
        // Just one until action ...
        Solution solution = new Solution(
            new SolutionCommand( new UntilAction( CompletionState.WON ) )
        );

        SolutionInterpreter i = new SolutionInterpreter( solution );

        // ... leads to lots of time steps...
        assertThat( i.next( R ), equalTo( new SolutionTimeStep( 1 ) ) );
        assertThat( i.next( R ), equalTo( new SolutionTimeStep( 1 ) ) );
        assertThat( i.next( R ), equalTo( new SolutionTimeStep( 1 ) ) );

        // ... and when we've finished, an assert.
        assertThat(
            i.next( W ),
            equalTo(
                new SolutionTimeStep(
                      1
                    , new AssertStateAction( CompletionState.WON )
                )
            )
        );

        // Then we're done
        assertThat( i.next( R ), nullValue() );
    }

    // ---

    private List<SolutionTimeStep> interpreterList(
        SolutionInterpreter interpreter )
    {
        List<SolutionTimeStep> ret = new ArrayList<SolutionTimeStep>();

        for (
            SolutionTimeStep step = interpreter.next();
            step != null;
            step = interpreter.next()
        )
        {
            ret.add( step );
        }

        return ret;
    }
}

package rabbitescape.engine.solution;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import static rabbitescape.engine.util.Util.*;

import org.junit.*;

import java.util.Arrays;
import java.util.Iterator;

import rabbitescape.engine.Token;
import rabbitescape.engine.World.CompletionState;

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
            new SolutionCommand( new WaitInstruction( 3 ) ) );

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
              new SolutionCommand(
                  new WaitInstruction( 1 ), new WaitInstruction( 2 ) )
            , new SolutionCommand( new WaitInstruction( 3 ) )
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

    @Test
    public void Single_nonwait_instruction_makes_single_time_step()
    {
        Solution solution = new Solution(
            new SolutionCommand( new SelectInstruction( Token.Type.explode ) )
        );

        SolutionInterpreter interpreter = new SolutionInterpreter( solution );

        assertThat(
            list( interpreter ),
            equalTo(
                Arrays.asList(
                    new SolutionTimeStep(
                        new SelectInstruction( Token.Type.explode ) )
                )
            )
        );
    }

    @Test
    public void Multiple_nonwait_instructions_in_single_cmd_make_1_time_step()
    {
        Solution solution = new Solution(
            new SolutionCommand(
                new SelectInstruction( Token.Type.explode ),
                new PlaceTokenInstruction( 2, 2 )
            )
        );

        SolutionInterpreter interpreter = new SolutionInterpreter( solution );

        assertThat(
            list( interpreter ),
            equalTo(
                Arrays.asList(
                    new SolutionTimeStep(
                          new SelectInstruction( Token.Type.explode )
                        , new PlaceTokenInstruction( 2, 2 )
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
                new SelectInstruction( Token.Type.explode ),
                new PlaceTokenInstruction( 2, 2 )
            ),
            new SolutionCommand( new WaitInstruction( 4 ) ),
            new SolutionCommand( new PlaceTokenInstruction( 3, 2 ) )
        );

        SolutionInterpreter interpreter = new SolutionInterpreter( solution );

        assertThat(
            list( interpreter ),
            equalTo(
                Arrays.asList(
                    new SolutionTimeStep(
                          new SelectInstruction( Token.Type.explode )
                        , new PlaceTokenInstruction( 2, 2 )
                    ),
                    new SolutionTimeStep(),
                    new SolutionTimeStep(),
                    new SolutionTimeStep(),
                    new SolutionTimeStep(),
                    new SolutionTimeStep( new PlaceTokenInstruction( 3, 2 ) )
                )
            )
        );
    }

    @Test
    public void Wait_then_do_then_wait_translates_into_time_steps()
    {
        Solution solution = new Solution(
            new SolutionCommand(),
            new SolutionCommand( new WaitInstruction( 1 ) ),
            new SolutionCommand(
                new SelectInstruction( Token.Type.explode ),
                new PlaceTokenInstruction( 2, 2 )
            ),
            new SolutionCommand( new WaitInstruction( 2 ) ),
            new SolutionCommand()
        );

        SolutionInterpreter interpreter = new SolutionInterpreter( solution );

        assertThat(
            list( interpreter ),
            equalTo(
                Arrays.asList(
                    new SolutionTimeStep(),
                    new SolutionTimeStep(),
                    new SolutionTimeStep(
                          new SelectInstruction( Token.Type.explode )
                        , new PlaceTokenInstruction( 2, 2 )
                    ),
                    new SolutionTimeStep(),
                    new SolutionTimeStep(),
                    new SolutionTimeStep()
                )
            )
        );
    }

    @Test
    public void Waits_mixed_with_dos_wait_after_doing_no_matter_the_order()
    {
        Solution solution = new Solution(
            new SolutionCommand(
                new WaitInstruction( 3 ),
                new SelectInstruction( Token.Type.explode ),
                new PlaceTokenInstruction( 2, 2 )
            ),
            new SolutionCommand( new WaitInstruction( 2 ) ),
            new SolutionCommand(
                new SelectInstruction( Token.Type.dig ),
                new PlaceTokenInstruction( 1, 1 ),
                new WaitInstruction( 3 )
            )
        );

        SolutionInterpreter interpreter = new SolutionInterpreter( solution );

        assertThat(
            list( interpreter ),
            equalTo(
                Arrays.asList(
                    new SolutionTimeStep(
                          new SelectInstruction( Token.Type.explode )
                        , new PlaceTokenInstruction( 2, 2 )
                    ),
                    new SolutionTimeStep(),
                    new SolutionTimeStep(),

                    new SolutionTimeStep(),
                    new SolutionTimeStep(),

                    new SolutionTimeStep(
                        new SelectInstruction( Token.Type.dig ),
                        new PlaceTokenInstruction( 1, 1 )
                    ),
                    new SolutionTimeStep(),
                    new SolutionTimeStep()
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

        SolutionInterpreter interpreter = new SolutionInterpreter( solution );

        assertThat(
            list( interpreter ),
            equalTo(
                Arrays.asList(
                    new SolutionTimeStep()
                )
            )
        );
    }

    @Test
    public void Many_empty_commands()
    {
        Solution solution = new Solution(
            new SolutionCommand(),
            new SolutionCommand( new SelectInstruction( Token.Type.dig ) ),
            new SolutionCommand( new PlaceTokenInstruction( 1, 1 ) ),
            new SolutionCommand( new TargetState( CompletionState.RUNNING ) ),
            new SolutionCommand(),
            new SolutionCommand(),
            new SolutionCommand()
        );

        SolutionInterpreter interpreter = new SolutionInterpreter( solution );

        assertThat(
            list( interpreter ),
            equalTo(
                Arrays.asList(
                    new SolutionTimeStep(),

                    new SolutionTimeStep(
                        new SelectInstruction( Token.Type.dig ) ),

                    new SolutionTimeStep( new PlaceTokenInstruction( 1, 1 ) ),

                    new SolutionTimeStep(
                        new TargetState( CompletionState.RUNNING ) ),

                    new SolutionTimeStep(),

                    new SolutionTimeStep(),

                    new SolutionTimeStep()
                )
            )
        );
    }
}

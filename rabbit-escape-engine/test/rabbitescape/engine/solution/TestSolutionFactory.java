package rabbitescape.engine.solution;

import org.junit.*;

import rabbitescape.engine.Token;
import rabbitescape.engine.World;
import rabbitescape.engine.World.CompletionState;

import static org.junit.Assert.fail;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

public class TestSolutionFactory
{
    @Test
    public void Empty_string_gives_empty_solution_TODO_NOT_AT_MOMENT()
    {
        assertThat(
            SolutionFactory.create( "" ),
            equalTo(
                new Solution(
                      new SolutionStep( new WaitInstruction( 1 ) )
                    , new SolutionStep( new TargetState( CompletionState.WON ) )
                )
            )
        );
    }

    @Test
    public void Single_status_gives_single_validation_instruction()
    {
        assertThat(
            SolutionFactory.create( "WON" ),
            equalTo(
                new Solution(
                    new SolutionStep(
                        new TargetState( World.CompletionState.WON ) )
                )
            )
        );
    }

    @Test
    public void Multiple_instructions_are_found_and_listed()
    {
        assertThat(
            SolutionFactory.create( "1;2;WON" ),
            equalTo(
                new Solution(
                    new SolutionStep( new WaitInstruction( 1 ) ),
                    new SolutionStep(  new WaitInstruction( 2 ) ),
                    new SolutionStep(
                        new TargetState( World.CompletionState.WON ) )
                )
            )
        );
    }

    @Test
    public void Simultaneous_instructions_are_noted()
    {
        assertThat(
            SolutionFactory.create( "bash&(1,1)&3;WON" ),
            equalTo(
                new Solution(
                    new SolutionStep(
                        new SelectInstruction( Token.Type.bash ),
                        new PlaceTokenInstruction( 1, 1 ),
                        new WaitInstruction( 3 ) ),
                    new SolutionStep(
                        new TargetState( World.CompletionState.WON ) )
                )
            )
        );
    }

    @Test
    public void Nonwait_instructions_get_a_wait_appended_except_at_end()
    {
        assertThat(
            SolutionFactory.create( "bridge;LOST" ),
            equalTo(
                new Solution(
                    new SolutionStep(
                        new SelectInstruction( Token.Type.bridge ),
                        new WaitInstruction( 1 )
                    ),
                    new SolutionStep(
                        new TargetState( World.CompletionState.LOST ) )
                )
            )
        );
    }

    @Test
    public void Empty_steps_get_a_wait_appended_except_at_end()
    {
        assertThat(
            SolutionFactory.create( ";;" ),
            equalTo(
                new Solution(
                    new SolutionStep( new WaitInstruction( 1 ) ),
                    new SolutionStep( new WaitInstruction( 1 ) ),
                    new SolutionStep( new WaitInstruction( 1 ) ),
                    new SolutionStep(
                        new TargetState( World.CompletionState.WON ) )
                )
            )
        );
    }

    @Test
    public void If_not_specified_assert_we_win_at_end()
    {
        assertThat(
            SolutionFactory.create( "bridge;(22,40)" ),
            equalTo(
                new Solution(
                    new SolutionStep(
                        new SelectInstruction( Token.Type.bridge ),
                        new WaitInstruction( 1 )
                    ),
                    new SolutionStep(
                        new PlaceTokenInstruction( 22, 40 ),
                        new WaitInstruction( 1 )
                    ),
                    new SolutionStep(
                        new TargetState( World.CompletionState.WON ) )
                )
            )
        );
    }

    @Test
    public void Can_parse_single_instruction()
    {
        assertThat(
            SolutionFactory.createStep( "bash" ),
            equalTo(
                new SolutionStep(
                    new SelectInstruction( Token.Type.bash ) )
            )
        );
    }

    @Test
    public void Can_parse_multiple_single_instructions()
    {
        assertThat(
            SolutionFactory.createStep( "bash&(1,2)" ),
            equalTo(
                new SolutionStep(
                      new SelectInstruction( Token.Type.bash )
                    , new PlaceTokenInstruction( 1, 2 )
                )
            )
        );
    }

    @Test
    public void Unrecognised_ability_is_an_error()
    {
        try
        {
            SolutionFactory.create( "unknown_ability" );
            fail( "Expected an InvalidInstruction!" );
        }
        catch ( InvalidInstruction e )
        {
            assertThat( e.instruction, equalTo( "unknown_ability" ) );
        }
    }

    @Test
    public void Unrecognised_state_is_an_error()
    {
        try
        {
            SolutionFactory.create( "1;UNKNOWN_STATE" );
            fail( "Expected an InvalidInstruction!" );
        }
        catch ( InvalidInstruction e )
        {
            assertThat( e.instruction, equalTo( "UNKNOWN_STATE" ) );
        }
    }

    @Test
    public void Nonnumeric_position_is_an_error()
    {
        try
        {
            SolutionFactory.create( "bash;(3,a)" );
            fail( "Expected an InvalidInstruction!" );
        }
        catch ( InvalidInstruction e )
        {
            assertThat( e.instruction, equalTo( "(3,a)" ) );
        }
    }

    @Test
    public void Massive_wait_is_an_error()
    {
        String bigNum = String.valueOf( Long.MAX_VALUE );

        try
        {
            SolutionFactory.create( bigNum );
            fail( "Expected an InvalidInstruction!" );
        }
        catch ( InvalidInstruction e )
        {
            assertThat( e.instruction, equalTo( bigNum ) );
        }
    }

    @Test
    public void Massive_position_is_an_error()
    {
        String bigNum = String.valueOf( Long.MAX_VALUE );

        try
        {
            SolutionFactory.create( "bash;(3," + bigNum + ")" );
            fail( "Expected an InvalidInstruction!" );
        }
        catch ( InvalidInstruction e )
        {
            assertThat( e.instruction, equalTo( "(3," + bigNum + ")" ) );
        }
    }
}

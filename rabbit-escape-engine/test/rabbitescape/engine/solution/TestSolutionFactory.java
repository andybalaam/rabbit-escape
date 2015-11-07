package rabbitescape.engine.solution;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.*;

import rabbitescape.engine.Token;
import rabbitescape.engine.World;

import static org.junit.Assert.fail;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

public class TestSolutionFactory
{
    @Test
    public void Empty_string_gives_empty_solution()
    {
        assertThat(
            SolutionFactory.create( "" ),
            equalTo( new Solution( new ArrayList<Instruction>() ) )
        );
    }

    @Test
    public void Single_status_gives_single_validation_instruction()
    {
        assertThat(
            SolutionFactory.create( "WON" ),
            equalTo(
                new Solution(
                    Arrays.asList(
                        new Instruction[]
                        {
                            new TargetState( World.CompletionState.WON )
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
            SolutionFactory.create( "1;2;WON" ),
            equalTo(
                new Solution(
                    Arrays.asList(
                        new Instruction[]
                        {
                            new WaitInstruction( 1 ),
                            new WaitInstruction( 2 ),
                            new TargetState( World.CompletionState.WON )
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
            SolutionFactory.create( "bash&(1,1)&3;WON" ),
            equalTo(
                new Solution(
                    Arrays.asList(
                        new Instruction[]
                        {
                            new SelectInstruction( Token.Type.bash ),
                            new PlaceTokenInstruction( 1, 1 ),
                            new WaitInstruction( 3 ),
                            new TargetState( World.CompletionState.WON )
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
            SolutionFactory.create( "bridge;LOST" ),
            equalTo(
                new Solution(
                    Arrays.asList(
                        new Instruction[]
                        {
                            new SelectInstruction( Token.Type.bridge ),
                            new WaitInstruction( 1 ),
                            new TargetState( World.CompletionState.LOST )
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
            SolutionFactory.create( "bridge;(22,40)" ),
            equalTo(
                new Solution(
                    Arrays.asList(
                        new Instruction[]
                        {
                            new SelectInstruction( Token.Type.bridge ),
                            new WaitInstruction( 1 ),
                            new PlaceTokenInstruction( 22, 40 ),
                            new TargetState( World.CompletionState.WON )
                        }
                    )
                )
            )
        );
    }

    @Test
    public void Can_parse_single_instruction()
    {
        assertThat(
            SolutionFactory.createTimeStep( "bash", 1 ),
            equalTo(
                Arrays.asList(
                    new Instruction[]
                    {
                        new SelectInstruction( Token.Type.bash )
                    }
                )
            )
        );
    }

    @Test
    public void Can_parse_multiple_single_instructions()
    {
        assertThat(
            SolutionFactory.createTimeStep( "bash&(1,2)", 1 ),
            equalTo(
                Arrays.asList(
                    new Instruction[]
                    {
                          new SelectInstruction( Token.Type.bash )
                        , new PlaceTokenInstruction( 1, 2 )
                    }
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

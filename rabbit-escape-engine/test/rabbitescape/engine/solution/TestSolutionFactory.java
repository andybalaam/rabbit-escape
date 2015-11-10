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
                      new SolutionCommand( new WaitAction( 1 ) )
                    , new SolutionCommand( new AssertStateAction( CompletionState.WON ) )
                )
            )
        );
    }

    @Test
    public void Single_status_gives_single_validation_action()
    {
        assertThat(
            SolutionFactory.create( "WON" ),
            equalTo(
                new Solution(
                    new SolutionCommand(
                        new AssertStateAction( World.CompletionState.WON ) )
                )
            )
        );
    }

    @Test
    public void Multiple_actions_are_found_and_listed()
    {
        assertThat(
            SolutionFactory.create( "1;2;WON" ),
            equalTo(
                new Solution(
                    new SolutionCommand( new WaitAction( 1 ) ),
                    new SolutionCommand( new WaitAction( 2 ) ),
                    new SolutionCommand(
                        new AssertStateAction( World.CompletionState.WON ) )
                )
            )
        );
    }

    @Test
    public void Simultaneous_actions_are_noted()
    {
        assertThat(
            SolutionFactory.create( "bash&(1,1)&3;WON" ),
            equalTo(
                new Solution(
                    new SolutionCommand(
                        new SelectAction( Token.Type.bash ),
                        new PlaceTokenAction( 1, 1 ),
                        new WaitAction( 3 ) ),
                    new SolutionCommand(
                        new AssertStateAction( World.CompletionState.WON ) )
                )
            )
        );
    }

    @Test
    public void Nonwait_actions_get_a_wait_appended_except_at_end()
    {
        assertThat(
            SolutionFactory.create( "bridge;LOST" ),
            equalTo(
                new Solution(
                    new SolutionCommand(
                        new SelectAction( Token.Type.bridge ),
                        new WaitAction( 1 )
                    ),
                    new SolutionCommand(
                        new AssertStateAction( World.CompletionState.LOST ) )
                )
            )
        );
    }

    @Test
    public void Empty_commands_get_a_wait_appended_except_at_end()
    {
        assertThat(
            SolutionFactory.create( ";;" ),
            equalTo(
                new Solution(
                    new SolutionCommand( new WaitAction( 1 ) ),
                    new SolutionCommand( new WaitAction( 1 ) ),
                    new SolutionCommand( new WaitAction( 1 ) ),
                    new SolutionCommand(
                        new AssertStateAction( World.CompletionState.WON ) )
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
                    new SolutionCommand(
                        new SelectAction( Token.Type.bridge ),
                        new WaitAction( 1 )
                    ),
                    new SolutionCommand(
                        new PlaceTokenAction( 22, 40 ),
                        new WaitAction( 1 )
                    ),
                    new SolutionCommand(
                        new AssertStateAction( World.CompletionState.WON ) )
                )
            )
        );
    }

    @Test
    public void Can_parse_single_action()
    {
        assertThat(
            SolutionFactory.createCommand( "bash" ),
            equalTo(
                new SolutionCommand(
                    new SelectAction( Token.Type.bash ) )
            )
        );
    }

    @Test
    public void Can_parse_multiple_single_actions()
    {
        assertThat(
            SolutionFactory.createCommand( "bash&(1,2)" ),
            equalTo(
                new SolutionCommand(
                      new SelectAction( Token.Type.bash )
                    , new PlaceTokenAction( 1, 2 )
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
            fail( "Expected an InvalidAction!" );
        }
        catch ( InvalidAction e )
        {
            assertThat( e.action, equalTo( "unknown_ability" ) );
        }
    }

    @Test
    public void Unrecognised_state_is_an_error()
    {
        try
        {
            SolutionFactory.create( "1;UNKNOWN_STATE" );
            fail( "Expected an InvalidAction!" );
        }
        catch ( InvalidAction e )
        {
            assertThat( e.action, equalTo( "UNKNOWN_STATE" ) );
        }
    }

    @Test
    public void Nonnumeric_position_is_an_error()
    {
        try
        {
            SolutionFactory.create( "bash;(3,a)" );
            fail( "Expected an InvalidAction!" );
        }
        catch ( InvalidAction e )
        {
            assertThat( e.action, equalTo( "(3,a)" ) );
        }
    }

    @Test
    public void Massive_wait_is_an_error()
    {
        String bigNum = String.valueOf( Long.MAX_VALUE );

        try
        {
            SolutionFactory.create( bigNum );
            fail( "Expected an InvalidAction!" );
        }
        catch ( InvalidAction e )
        {
            assertThat( e.action, equalTo( bigNum ) );
        }
    }

    @Test
    public void Massive_position_is_an_error()
    {
        String bigNum = String.valueOf( Long.MAX_VALUE );

        try
        {
            SolutionFactory.create( "bash;(3," + bigNum + ")" );
            fail( "Expected an InvalidAction!" );
        }
        catch ( InvalidAction e )
        {
            assertThat( e.action, equalTo( "(3," + bigNum + ")" ) );
        }
    }
}

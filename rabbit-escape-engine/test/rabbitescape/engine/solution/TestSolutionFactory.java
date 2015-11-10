package rabbitescape.engine.solution;

import org.junit.*;

import rabbitescape.engine.Token;
import rabbitescape.engine.World;

import static org.junit.Assert.fail;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

public class TestSolutionFactory
{
    @Test
    public void Empty_string_gives_single_empty_command()
    {
        assertThat(
            SolutionParser.parse( "" ),
            equalTo(
                new Solution( new SolutionCommand() )
            )
        );
    }

    @Test
    public void Single_status_gives_single_validation_action()
    {
        assertThat(
            SolutionParser.parse( "WON" ),
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
            SolutionParser.parse( "1;2;WON" ),
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
            SolutionParser.parse( "bash&(1,1)&3;WON" ),
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
    public void Nonwait_actions_dont_get_a_wait_appended()
    {
        assertThat(
            SolutionParser.parse( "bridge;LOST" ),
            equalTo(
                new Solution(
                    new SolutionCommand(
                        new SelectAction( Token.Type.bridge )
                    ),
                    new SolutionCommand(
                        new AssertStateAction( World.CompletionState.LOST ) )
                )
            )
        );
    }

    @Test
    public void Empty_commands_dont_get_a_wait_appended()
    {
        assertThat(
            SolutionParser.parse( ";;" ),
            equalTo(
                new Solution(
                    new SolutionCommand(),
                    new SolutionCommand(),
                    new SolutionCommand()
                )
            )
        );
    }

    @Test
    public void No_assert_added_at_end()
    {
        assertThat(
            SolutionParser.parse( "bridge;(22,40)" ),
            equalTo(
                new Solution(
                    new SolutionCommand(
                        new SelectAction( Token.Type.bridge )
                    ),
                    new SolutionCommand(
                        new PlaceTokenAction( 22, 40 )
                    )
                )
            )
        );
    }

    @Test
    public void Can_parse_single_action()
    {
        assertThat(
            SolutionParser.parseCommand( "bash" ),
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
            SolutionParser.parseCommand( "bash&(1,2)" ),
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
            SolutionParser.parse( "unknown_ability" );
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
            SolutionParser.parse( "1;UNKNOWN_STATE" );
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
            SolutionParser.parse( "bash;(3,a)" );
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
            SolutionParser.parse( bigNum );
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
            SolutionParser.parse( "bash;(3," + bigNum + ")" );
            fail( "Expected an InvalidAction!" );
        }
        catch ( InvalidAction e )
        {
            assertThat( e.action, equalTo( "(3," + bigNum + ")" ) );
        }
    }
}

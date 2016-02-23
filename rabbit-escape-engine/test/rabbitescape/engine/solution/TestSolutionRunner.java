package rabbitescape.engine.solution;

import static org.junit.Assert.fail;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.junit.Test;

import rabbitescape.engine.Token;
import rabbitescape.engine.World;
import rabbitescape.engine.World.CompletionState;
import rabbitescape.engine.solution.SolutionExceptions;
import rabbitescape.engine.textworld.TextWorldManip;

public class TestSolutionRunner
{
    @Test( expected = SolutionExceptions.UnexpectedState.class )
    public void Unexpected_state_is_an_error()
    {
        SolutionRunner.runSolution(
            expectingSolution( CompletionState.LOST ), neverEndingWorld() );
    }

    @Test
    public void Unexpected_state_is_serialised_to_helpful_message()
    {
        try
        {
            SolutionRunner.runSolution(
                expectingSolution( CompletionState.LOST ), neverEndingWorld() );
            fail( "Expected exception!" );
        }
        catch( SolutionExceptions.UnexpectedState e )
        {
            e.solutionId = 3;
            e.level = "baz";
            e.world = "z";

            assertThat(
                e.getMessage(),
                equalTo(
                    "Solution failed: state was RUNNING but we expected LOST"
                    + " at command 1 of solution 3 in baz:\nz."
                    + "\nTo see: ./runrabbit swing -l baz -s3"
                )
            );
        }
    }

    @Test( expected = SolutionExceptions.DidNotWin.class )
    public void Failing_unexpectedly_is_an_error()
    {
        SolutionRunner.runSolution(
            expectingSolution( CompletionState.WON ), neverEndingWorld() );
    }

    @Test
    public void Failing_unexpectedly_is_serialised_to_helpful_message()
    {
        try
        {
            SolutionRunner.runSolution(
                expectingSolution( CompletionState.WON ), neverEndingWorld() );
            fail( "Expected exception!" );
        }
        catch( SolutionExceptions.UnexpectedState e )
        {
            e.solutionId = 4;
            e.level = "X";
            e.world = "";

            assertThat(
                e.getMessage(),
                equalTo(
                    "Solution failed: We expected to win, but the state was"
                    + " RUNNING at command 1 of solution 4 in X:\n."
                    + "\nTo see: ./runrabbit swing -l X -s4"
                )
            );
        }
    }

    @Test( expected = SolutionExceptions.RanPastEnd.class )
    public void Going_on_beyond_the_end_is_an_error()
    {
        SolutionRunner.runSolution( waitFourSolution(), threeStepWorld() );
    }

    @Test
    public void Going_on_beyond_the_end_is_serialised_to_helpful_message()
    {
        try
        {
            SolutionRunner.runSolution( waitFourSolution(), threeStepWorld() );
            fail( "Expected exception!" );
        }
        catch( SolutionExceptions.RanPastEnd e )
        {
            e.solutionId = 5;
            e.world = "";

            assertThat(
                e.getMessage(),
                equalTo(
                    "Solution failed: world has stopped (state: WON) but"
                    + " there are more solution commands"
                    + " at command 3 of solution 5 in <>:\n."
                    + "\nTo see: ./runrabbit swing -l <> -s5"
                )
            );
        }
    }

    @Test( expected = SolutionExceptions.UsedRunOutAbility.class )
    public void Using_missing_ability_is_an_error()
    {
        SolutionRunner.runSolution(
            useBash30Solution(), neverEndingWorldWithBash() );
    }

    @Test
    public void Using_missing_ability_is_serialised_to_helpful_message()
    {
        try
        {
            SolutionRunner.runSolution(
                useBash30Solution(), neverEndingWorldWithBash() );

            fail( "Expected exception!" );
        }
        catch( SolutionExceptions.UsedRunOutAbility e )
        {
            e.solutionId = 6;
            e.level = "foo";
            e.world = "";

            assertThat(
                e.getMessage(),
                equalTo(
                    "Solution failed: ability 'bash' was used when there"
                    + " were none left at command 4 of solution 6 in foo:\n."
                    + "\nTo see: ./runrabbit swing -l foo -s6"
                )
            );
        }
    }

    @Test( expected = SolutionExceptions.UsedMissingAbility.class )
    public void Using_run_out_ability_is_an_error()
    {
        SolutionRunner.runSolution( useBash30Solution(), neverEndingWorld() );
    }

    @Test
    public void Using_run_out_ability_is_serialised_to_helpful_message()
    {
        try
        {
            SolutionRunner.runSolution(
                useBash30Solution(), neverEndingWorld() );

            fail( "Expected exception!" );
        }
        catch( SolutionExceptions.UsedMissingAbility e )
        {
            e.solutionId = 7;
            e.level = "foo";
            e.world = "";

            assertThat(
                e.getMessage(),
                equalTo(
                    "Solution failed: ability 'bash' was used but this level"
                    + " does not provide it at command 2 of solution 7 in"
                    + " foo:\n."
                    + "\nTo see: ./runrabbit swing -l foo -s7"
                )
            );
        }
    }

    @Test( expected = SolutionExceptions.PlacedTokenOutsideWorld.class )
    public void Placing_a_token_outside_the_world_is_an_error()
    {
        SolutionRunner.runSolution(
            useBash100Solution(), neverEndingWorldWithBash() );
    }

    @Test
    public void Placing_a_token_outside_the_world_is_serialised_nicely()
    {
        try
        {
            SolutionRunner.runSolution(
                useBash100Solution(), neverEndingWorldWithBash() );

            fail( "Expected exception!" );
        }
        catch( SolutionExceptions.PlacedTokenOutsideWorld e )
        {
            e.solutionId = 8;
            e.level = "bar";
            e.world = "";

            assertThat(
                e.getMessage(),
                equalTo(
                    "Solution failed: placed a token at (10, 0) but the"
                    + " world is only 5x2 in size"
                    + " at command 2 of solution 8 in bar:\n."
                    + "\nTo see: ./runrabbit swing -l bar -s8"
                )
            );
        }
    }

    @Test
    public void Rabbit_dying_by_walking_out_of_level_is_not_an_error()
    {
        SolutionRunner.runSolution(
            waitFiveThenLostSolution(),
            TextWorldManip.createWorld(
                "#r   ",
                "#####",
                ":num_rabbits=0",
                ":num_to_save=1"
            )
        );
    }

    @Test( expected = SolutionExceptions.FailedToPlaceToken.class )
    public void Placing_a_token_on_a_block_is_an_error()
    {
        SolutionRunner.runSolution( useBash30Solution(), blockAt30World() );
    }

    @Test
    public void Placing_a_token_on_a_block_is_serialised_nicely()
    {
        try
        {
            SolutionRunner.runSolution( useBash30Solution(), blockAt30World() );

            fail( "Expected exception!" );
        }
        catch( SolutionExceptions.FailedToPlaceToken e )
        {
            e.solutionId = 9;
            e.level = "bar";
            e.world = "y\nx";

            assertThat(
                e.getMessage(),
                equalTo(
                    "Solution failed: tried to place a bash token at (3, 0) but"
                    + " a block was already there so it did not place"
                    + " at command 4 of solution 9 in bar:\ny\nx."
                    + "\nTo see: ./runrabbit swing -l bar -s9"
                )
            );
        }
    }

    @Test
    public void Until_never_ending_is_serialised_nicely()
    {
        try
        {
            SolutionRunner.runSolution(
                new Solution(
                    new SolutionCommand(
                        new UntilAction( CompletionState.WON )
                    )
                ),
                neverEndingWorld()
            );

            fail( "Expected exception!" );
        }
        catch( SolutionExceptions.UntilActionNeverEnded e )
        {
            e.solutionId = 10;
            e.level = "qux";
            e.world = "x\ny";

            assertThat(
                e.getMessage(),
                equalTo(
                    "Solution failed: the level never finished, but"
                    + " there was an until:WON"
                    + " action at command 1 of solution 10 in qux:\nx\ny."
                    + "\nTo see: ./runrabbit swing -l qux -s10"
                )
            );
        }
    }

    @Test
    public void Real_level_with_WON_at_end_works()
    {
        World world = TextWorldManip.createWorld(
            ":num_rabbits=1",
            ":num_to_save=1",
            "Q    ",
            "    O",
            "#####"
        );

        boolean solved = SolutionRunner.runSolution(
            SolutionParser.parse( "6;WON" ), world );

        assertThat( solved, is( true ) );
    }

    @Test
    public void Real_level_with_no_assert_but_we_won_works()
    {
        World world = TextWorldManip.createWorld(
            ":num_rabbits=1",
            ":num_to_save=1",
            "Q    ",
            "    O",
            "#####"
        );

        boolean solved = SolutionRunner.runSolution(
            SolutionParser.parse( "6" ), world );

        assertThat( solved, is( true ) );
    }

    @Test
    public void Real_level_with_LOST_at_end_works()
    {
        World world = TextWorldManip.createWorld(
            ":num_rabbits=1",
            ":num_to_save=1",
            "Q    ",
            "     ",
            "#####"
        );

        boolean solved = SolutionRunner.runSolution(
            SolutionParser.parse( "7;LOST" ), world );

        assertThat( solved, is( false ) );
    }

    @Test
    public void Real_level_with_no_assert_and_still_running_did_not_solve()
    {
        World world = TextWorldManip.createWorld(
            ":num_rabbits=1",
            ":num_to_save=1",
            "Q    ",
            "     ",
            "#####"
        );

        boolean solved = SolutionRunner.runSolution(
            SolutionParser.parse( "2;RUNNING" ), world );

        assertThat( solved, is( false ) );
    }

    @Test
    public void Real_level_with_until_WON_works()
    {
        World world = TextWorldManip.createWorld(
            ":num_rabbits=1",
            ":num_to_save=1",
            "Q    ",
            "    O",
            "#####"
        );

        boolean solved = SolutionRunner.runSolution(
            SolutionParser.parse( "until:WON" ), world );

        assertThat( solved, is( true ) );
    }

    @Test
    public void Real_level_with_until_LOST_works()
    {
        World world = TextWorldManip.createWorld(
            ":num_rabbits=1",
            ":num_to_save=1",
            "Q    ",
            "     ",
            "#####"
        );

        boolean solved = SolutionRunner.runSolution(
            SolutionParser.parse( "until:LOST" ), world );

        assertThat( solved, is( false ) );
    }

    @Test
    public void Print_step() throws UnsupportedEncodingException // for the UTF8
    {
        World world = TextWorldManip.createWorld(
            ":num_rabbits=0",
            ":num_to_save=1",
            ":solution.1=until:WON",
            " d j ",
            "#####",
            "#O###",
            "#####"
        );

        Solution solution = SolutionParser.parse( world.solutions[0] );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream( baos );

        SolutionRunner.runSolution( solution, world, ps, false );

        String out = baos.toString("UTF8");

        String exp =
            "Waiting:0" + "\n" +
            "  Saved:0" + "\n" +
            "00  d j " + "\n" +
            "01 #####" + "\n" +
            "02 #O###" + "\n" +
            "03 #####" + "\n" +
            "   00000" + "\n" +
            "   01234" + "\n" +
            "Waiting:0" + "\n" +
            "  Saved:0" + "\n" +
            "00  dj  " + "\n" +
            "01 #####" + "\n" +
            "02 #O###" + "\n" +
            "03 #####" + "\n" +
            "   00000" + "\n" +
            "   01234" + "\n" +
            "Waiting:0" + "\n" +
            "  Saved:0" + "\n" +
            "00  j   " + "\n" +
            "01 #####" + "\n" +
            "02 #O###" + "\n" +
            "03 #####" + "\n" +
            "   00000" + "\n" +
            "   01234" + "\n" +
            "Waiting:0" + "\n" +
            "  Saved:0" + "\n" +
            "00      " + "\n" +
            "01 #j###" + "\n" +
            "02 #O###" + "\n" +
            "03 #####" + "\n" +
            "   00000" + "\n" +
            "   01234" + "\n" +
            "Waiting:0" + "\n" +
            "  Saved:0" + "\n" +
            "00      " + "\n" +
            "01 #j###" + "\n" +
            "02 #O###" + "\n" +
            "03 #####" + "\n" +
            "   00000" + "\n" +
            "   01234" + "\n" +
            "Waiting:0" + "\n" +
            "  Saved:0" + "\n" +
            "00      " + "\n" +
            "01 # ###" + "\n" +
            "02 #O###" + "\n" +
            "03 #####" + "\n" +
            "   00000" + "\n" +
            "   01234" + "\n" +
            "Waiting:0" + "\n" +
            "  Saved:1" + "\n" +
            "00      " + "\n" +
            "01 # ###" + "\n" +
            "02 #O###" + "\n" +
            "03 #####" + "\n" +
            "   00000" + "\n" +
            "   01234" + "\n" +
            "Waiting:0" + "\n" +
            "  Saved:1" + "\n" +
            "00      " + "\n" +
            "01 # ###" + "\n" +
            "02 #O###" + "\n" +
            "03 #####" + "\n" +
            "   00000" + "\n" +
            "   01234" + "\n";

        assertThat( out, equalTo( exp ) );
    }

    // --

    private World neverEndingWorld()
    {
        return TextWorldManip.createWorld(
            "#r  #",
            "#####"
        );
    }

    private World neverEndingWorldWithBash()
    {
        return TextWorldManip.createWorld(
            "#r  #",
            "#####",
            ":bash=1"
        );
    }

    private World threeStepWorld()
    {
        return TextWorldManip.createWorld(
            "#r O",
            "####",
            ":num_rabbits=0",
            ":num_to_save=1"
        );
    }

    private World blockAt30World()
    {
        return TextWorldManip.createWorld(
            "#r ##",
            "#####",
            ":bash=2"
        );
    }

    private Solution expectingSolution( CompletionState expected )
    {
        return new Solution( new SolutionCommand( new AssertStateAction( expected ) ) );
    }

    private Solution waitFourSolution()
    {
        return new Solution(
            new SolutionCommand( new WaitAction( 1 ) ),
            new SolutionCommand( new WaitAction( 2 ) ),
            new SolutionCommand( new WaitAction( 2 ) )
        );
    }

    private Solution waitFiveThenLostSolution()
    {
        return new Solution(
            new SolutionCommand( new WaitAction( 5 ) ),
            new SolutionCommand( new AssertStateAction( CompletionState.LOST ) )
        );
    }

    private Solution useBash30Solution()
    {
        return new Solution(
            new SolutionCommand( new SelectAction( Token.Type.bash ) ),
            new SolutionCommand( new PlaceTokenAction( 1, 0 ) ),
            new SolutionCommand( new WaitAction( 1 ) ),
            new SolutionCommand( new PlaceTokenAction( 3, 0 ) )
        );
    }

    private Solution useBash100Solution()
    {
        return new Solution(
            new SolutionCommand( new SelectAction( Token.Type.bash ) ),
            new SolutionCommand( new PlaceTokenAction( 10, 0 ) )
        );
    }
}

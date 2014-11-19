package rabbitescape.engine.logic;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static rabbitescape.engine.util.Util.*;
import static rabbitescape.engine.textworld.TextWorldManip.*;
import static rabbitescape.engine.World.CompletionState.*;

import org.junit.Test;

import rabbitescape.engine.Token;
import rabbitescape.engine.World;
import rabbitescape.engine.World.DontStepAfterFinish;

public class TestWorld
{
    @Test
    public void World_will_not_step_with_no_live_rabbits()
    {
        World world = createWorld(
            ":num_rabbits=1",
            " Q   ",
            "   O ",
            "#####"
        );

        world.step();
        world.step();
        world.step();
        world.step();  // All gone now, so

        // This step should throw
        DontStepAfterFinish exception = null;
        try
        {
            world.step();
        }
        catch( DontStepAfterFinish e )
        {
            exception = e;
        }

        assertThat( exception, notNullValue() );
    }

    @Test
    public void World_reports_when_finished_no_live_rabbits()
    {
        World world = createWorld(
            ":num_rabbits=5",
            ":rabbit_delay=5",
            " Q ",
            " O ",  // Exit right below entrance
            "###"
        );

        world.step(); // First one over the exit

        fiveSteps( world );
        assertThat( world.completionState(), equalTo( RUNNING ) );

        fiveSteps( world );
        assertThat( world.completionState(), equalTo( RUNNING ) );

        fiveSteps( world );
        assertThat( world.completionState(), equalTo( RUNNING ) );

        fiveSteps( world );
        assertThat( world.completionState(), equalTo( RUNNING ) );

        // Fifth one over the exit: send it in
        world.step();

        // We should now be finished
        assertThat( world.completionState(), equalTo( WON ) );
    }

    @Test
    public void World_reports_won_when_enough_rabbits_saved()
    {
        World world = createWorld(
            ":num_rabbits=0",
            ":num_saved=2",
            ":num_to_save=2",
            "   ",
            "###"
        );

        // We should now be finished
        assertThat( world.completionState(), equalTo( WON ) );
    }

    @Test
    public void World_reports_lost_when_not_enough_rabbits_saved()
    {
        World world = createWorld(
            ":num_rabbits=0",
            ":num_saved=2",
            ":num_to_save=3",
            "   ",
            "###"
        );

        // We should now be finished
        assertThat( world.completionState(), equalTo( LOST ) );
    }

    @Test
    public void World_stores_number_of_abilities()
    {
        World world = createWorld(
                ":bash=5",
                "   ",
                "###"
        );

        assertThat( world.abilities.get( Token.Type.bash ), equalTo( 5 ) );
    }

    @Test
    public void World_reduces_abilities_when_you_use_a_token()
    {
        World world = createWorld(
                ":bash=5",
                ":dig=3",
                ":bridge=2",
                "   ",
                "###"
        );

        // This is what we are testing
        world.changes.addToken( 0, 0, Token.Type.bash );
        world.step();

        // There should be one less bash
        assertThat( world.abilities.get( Token.Type.bash ), equalTo( 4 ) );

        // The dig ability was unaffected
        assertThat( world.abilities.get( Token.Type.dig ), equalTo( 3 ) );

        // The bridge ability was unaffected
        assertThat( world.abilities.get( Token.Type.bridge ), equalTo( 2 ) );
    }

    @Test
    public void World_refuses_to_add_a_token_if_none_left()
    {
        World world = createWorld(
                ":bash=1",
                "   ",
                "###"
        );

        // Use up the last bash
        world.changes.addToken( 0, 0, Token.Type.bash );
        world.step();

        // Sanity
        assertThat( world.abilities.get( Token.Type.bash ), equalTo( 0 ) );

        // This is what we are testing: can't add another
        World.UnableToAddToken caughtException = null;
        try
        {
            world.changes.addToken( 1, 0, Token.Type.bash );
        }
        catch ( World.UnableToAddToken e )
        {
            caughtException = e;
        }

        assertThat( caughtException, notNullValue() );
    }

    @Test
    public void Cant_find_token_if_already_removed()
    {
        World world = createWorld(
            " i ",
            "###"
        );

        Token token = world.getTokenAt( 1, 0 );

        // Sanity
        assertThat( token, is( notNullValue() ) );

        // Remove it
        world.changes.removeToken( token );

        // This is what we are testing: it's gone
        assertThat( world.getTokenAt( 1, 0 ), is( nullValue() ) );

        // Sanity: after a step it's still gone
        world.step();
        assertThat( world.getTokenAt( 1, 0 ), is( nullValue() ) );
    }

    @Test
    public void Can_find_token_if_there_were_2_and_only_1_is_removed()
    {
        World world = createWorld(
            " i ",
            "###"
        );

        world.things.add( new Token( 1, 0, Token.Type.bridge ) );

        Token token = world.getTokenAt( 1, 0 );

        // Sanity
        assertThat( token, is( notNullValue() ) );

        // Remove one token
        world.changes.removeToken( token );

        // This is what we are testing: there's another
        Token token2 = world.getTokenAt( 1, 0 );
        assertThat( token2, is( notNullValue() ) );

        // Remove that one too
        world.changes.removeToken( token2 );

        // Now there's nothing left
        assertThat( world.getTokenAt( 1, 0 ), is( nullValue() ) );
    }

    // ---

    private void fiveSteps( World world )
    {
        for( @SuppressWarnings( "unused" ) int t : range( 5 ) )
        {
            world.step();
        }
    }
}

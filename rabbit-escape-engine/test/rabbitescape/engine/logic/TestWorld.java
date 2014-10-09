package rabbitescape.engine.logic;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static rabbitescape.engine.util.Util.*;
import static rabbitescape.engine.textworld.TextWorldManip.*;

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
        assertThat( world.finished(), is( false ) );

        fiveSteps( world );
        assertThat( world.finished(), is( false ) );

        fiveSteps( world );
        assertThat( world.finished(), is( false ) );

        fiveSteps( world );
        assertThat( world.finished(), is( false ) );

        // Fifth one over the exit: send it in
        world.step();

        // We should now be finished
        assertThat( world.finished(), is( true ) );
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

    // ---

    private void fiveSteps( World world )
    {
        for( @SuppressWarnings( "unused" ) int t : range( 5 ) )
        {
            world.step();
        }
    }
}

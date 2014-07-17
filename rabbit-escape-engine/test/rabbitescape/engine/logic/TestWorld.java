package rabbitescape.engine.logic;

import static rabbitescape.engine.util.Util.*;
import static rabbitescape.engine.textworld.TextWorldManip.*;

import static org.hamcrest.core.IsNull.*;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.Test;

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

    private void fiveSteps( World world )
    {
        for( @SuppressWarnings( "unused" ) int t : range( 5 ) )
        {
            world.step();
        }
    }
}

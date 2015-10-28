package rabbitescape.engine.logic;

import static org.hamcrest.core.IsEqual.*;
import static org.hamcrest.MatcherAssert.*;
import static rabbitescape.engine.Tools.*;
import static rabbitescape.engine.textworld.TextWorldManip.*;

import org.junit.Test;

import rabbitescape.engine.World;

public class TestExit
{
    @Test
    public void Rabbit_disappears_into_exit()
    {
        World world = createWorld(
            "r  O ",
            "#####"
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                " r>O ",
                "#####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "  r> ",
                "#####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   R ",  // Entering
                "#####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   O ",  // Gone
                "#####"
            )
        );
    }

    @Test
    public void World_keeps_score()
    {
        World world = createWorld(
            "Ojjjj   ",
            "########"
        );

        world.step();
        assertThat( world.num_saved, equalTo( 0 ) );
        world.step();
        assertThat( world.num_saved, equalTo( 1 ) );
        world.step();
        assertThat( world.num_saved, equalTo( 2 ) );
        world.step();
        assertThat( world.num_saved, equalTo( 3 ) );
        world.step();
        assertThat( world.num_saved, equalTo( 4 ) );
    }

    @Test
    public void Splatting_prevents_exit()
    {
        World world = createWorld(
            "r    ",
            "#    ",
            "     ",
            "  r  ",
            "  #  ",
            "     ",
            "#O O#",
            "#####"
        );

        for ( int i = 0; i < 5; i++ )
        {
            world.step();
        }
        assertThat( world.num_saved, equalTo ( 1 ) );
        // Check there have been enough steps for both
        // to exit or splat.
        assertThat(
            renderCompleteWorld( world, false ), 
            equalTo(new String[] {
                "     ",
                "#    ",
                "     ",
                "     ",
                "  #  ",
                "     ",
                "#O O#",
                "#####"
            }));
    }
}

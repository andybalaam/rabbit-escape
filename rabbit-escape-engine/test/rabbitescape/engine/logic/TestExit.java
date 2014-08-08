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
        assertThat( world.numSavedRabbits, equalTo( 0 ) );
        world.step();
        assertThat( world.numSavedRabbits, equalTo( 1 ) );
        world.step();
        assertThat( world.numSavedRabbits, equalTo( 2 ) );
        world.step();
        assertThat( world.numSavedRabbits, equalTo( 3 ) );
        world.step();
        assertThat( world.numSavedRabbits, equalTo( 4 ) );
    }
}

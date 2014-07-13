package rabbitescape.engine.logic;

import static org.hamcrest.MatcherAssert.*;
import static rabbitescape.engine.TextWorldManip.*;
import static rabbitescape.engine.Tools.*;

import org.junit.Test;

import rabbitescape.engine.World;

public class TestFalling
{
    @Test
    public void Fall_when_no_floor()
    {
        World world = createWorld(
            "   r ",
            "     ",
            "     ",
            "     ",
            "#####"
        );

        world.step();

        assertThat(
            renderWorld( world, false ),
            equalTo(
                "     ",
                "     ",
                "   r ",
                "     ",
                "#####"
            )
        );
    }

    @Test
    public void Fall_only_1_when_floor_is_1_below()
    {
        World world = createWorld(
            "   r ",
            "     ",
            "   # ",
            "     ",
            "#####"
        );

        world.step();

        assertThat(
            renderWorld( world, false ),
            equalTo(
                "     ",
                "   r ",
                "   # ",
                "     ",
                "#####"
            )
        );
    }

    @Test
    public void Fall_4_squares_without_dieing()
    {
        World world = createWorld(
            "   r ",
            "     ",
            "     ",
            "     ",
            "     ",
            "#####"
        );

        world.step();
        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "     ",
                "     ",
                "     ",
                "     ",
                "   r>",  // Landed and going to walk off
                "#####"
            )
        );
    }

    @Test
    public void Fall_6_squares_and_you_die()
    {
        World world = createWorld(
            "   r ",
            "     ",
            "     ",
            "     ",
            "     ",
            "     ",
            "     ",
            "#####"
        );

        world.step();
        world.step();
        world.step();

        assertThat(
            renderWorld( world, false ),
            equalTo(
                "     ",
                "     ",
                "     ",
                "     ",
                "     ",
                "     ",
                "   r ",  // Landed ...
                "#####"
            )
        );

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "     ",
                "     ",
                "     ",
                "     ",
                "     ",
                "     ",
                "   X ",  // ... and going to die
                "#####"
            )
        );

        world.step();


        assertThat(
            renderWorld( world, true ),
            equalTo(
                "     ",
                "     ",
                "     ",
                "     ",
                "     ",
                "     ",
                "     ",  // and now dead
                "#####"
            )
        );
    }

    @Test
    public void Fall_5_squares_and_you_die_earlier()
    {
        World world = createWorld(
            "   r ",
            "     ",
            "     ",
            "     ",
            "     ",
            "     ",
            "#####"
        );

        world.step();
        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "     ",
                "     ",
                "     ",
                "     ",
                "   r ",  // Not quite landed ...
                "   x ",  // ... but about to die ...
                "#####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "     ",
                "     ",
                "     ",
                "     ",
                "     ",
                "   y ",  // ... and will continue dying next step
                "#####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "     ",
                "     ",
                "     ",
                "     ",
                "     ",
                "     ",  // and now dead
                "#####"
            )
        );
    }
}

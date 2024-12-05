package rabbitescape.engine.logic;

import static org.hamcrest.MatcherAssert.*;
import static rabbitescape.engine.Tools.*;
import static rabbitescape.engine.textworld.TextWorldManip.*;

import org.junit.Test;

import rabbitescape.engine.World;

public class TestJumping
{
    @Test
    public void Jump_on_flat_ground()
    {
        World world = createWorld(
            "rw   ",
            "#####"
        );

        world.step();

        assertThat(
            renderWorld( world, true, false),
            equalTo(
                " w   ",
                "#####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false),
            equalTo(
                "   r>",
                "#####"
            )
        );
    }

    @Test
    public void Jump_between_bridges()
    {
        World world = createWorld(
            "rw   ",
            "## ##"
        );

        world.step();

        assertThat(
            renderWorld(world, true, false),
            equalTo(
                " w   ",
                "## ##"
            )
        );

        world.step();

        assertThat(
            renderWorld(world, true, false),
            equalTo(
                "   r>",
                "## ##"
            )
        );
    }

    @Test
    public void Jump_between_2bridges()
    {
        World world = createWorld(
            "rw   ",
            "##  #"
        );

        world.step();

        assertThat(
            renderWorld(world, true, false),
            equalTo(
                " w   ",
                "##  #"
            )
        );

        world.step();

        assertThat(
            renderWorld(world, true, false),
            equalTo(
                "   r ",
                "## f#"
            )
        );
    }

    @Test
    public void Jump_on_up_slope_right()
    {
        World world = createWorld(
            "rw   ",
            " /   ",
            "## ##"
        );

        world.step();

        assertThat(
            renderWorld(world, true, false),
            equalTo(
                "     ",
                "r~   ",
                "## ##"
            )
        );

        world.step();

        assertThat(
            renderWorld(world, true, false),
            equalTo(
                " w   ",
                " r   ",
                "## ##"
            )
        );

        world.step();

        assertThat(
            renderWorld(world, true, false),
            equalTo(
                "  r> ",
                " /   ",
                "## ##"
            )
        );

        world.step();

        assertThat(
            renderWorld(world, true, false),
            equalTo(
                "   r ",
                " / f ",
                "## ##"
            )
        );
    }

    @Test
    public void Jump_on_up_slope_left()
    {
        World world = createWorld(
            "   wj",
            "   \\ ",
            "## ##"
        );

        world.step();

        assertThat(
            renderWorld(world, true, false),
            equalTo(
                "     ",
                "   `j",
                "## ##"
            )
        );

        world.step();

        assertThat(
            renderWorld(world, true, false),
            equalTo(
                "   w ",
                "   j ",
                "## ##"
            )
        );

        world.step();

        assertThat(
            renderWorld(world, true, false),
            equalTo(
                " <j  ",
                "   \\ ",
                "## ##"
            )
        );

        world.step();

        assertThat(
            renderWorld(world, true, false),
            equalTo(
                " j   ",
                " f \\ ",
                "## ##"
            )
        );
    }

    @Test
    public void Jump_on_down_slope_right()
    {
        World world = createWorld(
            "rw   ",
            "#\\   ",
            "## ##"
        );

        world.step();

        assertThat(
            renderWorld(world, true, false),
            equalTo(
                "     ",
                "#w   ",
                "## ##"
            )
        );

        world.step();

        assertThat(
            renderWorld(world, true, false),
            equalTo(
                "     ",
                "#\\r> ",
                "## ##"
            )
        );

        world.step();

        assertThat(
            renderWorld(world, true, false),
            equalTo(
                "     ",
                "#\\ r>",
                "## ##"
            )
        );
    }

    @Test
    public void Jump_on_down_slope_left()
    {
        World world = createWorld(
            "   wj",
            "   /#",
            "## ##"
        );

        world.step();

        assertThat(
            renderWorld(world, true, false),
            equalTo(
                "     ",
                "   w#",
                "## ##"
            )
        );

        world.step();

        assertThat(
            renderWorld(world, true, false),
            equalTo(
                "     ",
                " <j/#",
                "## ##"
            )
        );

        world.step();

        assertThat(
            renderWorld(world, true, false),
            equalTo(
                "     ",
                "<j /#",
                "## ##"
            )
        );
    }

    @Test
    public void Jump_twice()
    {
        World world = createWorld(
            "rw w  ",
            "## # #"
        );

        world.step();

        assertThat(
            renderWorld(world, true, false),
            equalTo(
                " w w  ",
                "## # #"
            )
        );

        world.step();

        assertThat(
            renderWorld(world, true, false),
            equalTo(
                "   w  ",
                "## # #"
            )
        );

        world.step();

        assertThat(
            renderWorld(world, true, false),
            equalTo(
                "     r",
                "## # #"
            )
        );
    }
}

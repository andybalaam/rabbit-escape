package rabbitescape.engine.logic;

import static org.hamcrest.MatcherAssert.*;
import static rabbitescape.engine.TextWorldManip.*;
import static rabbitescape.engine.Tools.*;

import org.junit.Test;

import rabbitescape.engine.World;

public class TestWalking
{
    @Test
    public void Take_a_step_right_on_normal_ground()
    {
        World world = createWorld(
            "     ",
            "     ",
            " r   ",
            "#####"
        );

        world.step();

        assertThat(
            renderWorld( world, false ),
            equalTo(
                "     ",
                "     ",
                "  r  ",
                "#####"
            )
        );
    }

    @Test
    public void Take_a_step_left_on_normal_ground()
    {
        World world = createWorld(
            "     ",
            "     ",
            "  j  ",
            "#####"
        );

        world.step();

        assertThat(
            renderWorld( world, false ),
            equalTo(
                "     ",
                "     ",
                " j   ",
                "#####"
            )
        );
    }

    @Test
    public void Turn_when_you_hit_a_wall_on_left()
    {
        World world = createWorld(
            "#j   ",
            "#####"
        );

        world.step();

        assertThat(
            renderWorld( world, false ),
            equalTo(
                "#r   ",
                "#####"
            )
        );
    }

    @Test
    public void Turn_when_you_hit_a_wall_on_right()
    {
        World world = createWorld(
            "# r# ",
            "#####"
        );

        world.step();

        assertThat(
            renderWorld( world, false ),
            equalTo(
                "# j# ",
                "#####"
            )
        );
    }
}

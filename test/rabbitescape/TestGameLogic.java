package rabbitescape;

import static org.hamcrest.MatcherAssert.*;
import static rabbitescape.TextWorldManip.*;
import static rabbitescape.Tools.*;

import org.junit.Test;

public class TestGameLogic
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
            renderWorld( world ),
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
            renderWorld( world ),
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
            renderWorld( world ),
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
            renderWorld( world ),
            equalTo(
                "# j# ",
                "#####"
            )
        );
    }

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
            renderWorld( world ),
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
            renderWorld( world ),
            equalTo(
                "     ",
                "   r ",
                "   # ",
                "     ",
                "#####"
            )
        );
    }
}

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

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "#|   ",
                "#####"
            )
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

    @Test
    public void Climb_up_a_slope_right()
    {
        World world = createWorld(
            "      ",
            "   /##",
            "r /###",
            "######"
        );

        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "      ",
                "   /##",
                " r~###",  // Approaching
                "######"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "      ",
                "   $##",
                "  r###",  // Going up slope
                "######"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "    ' ",
                "   r##",
                "  /###",  // Approaching end
                "######"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "    r>",  // At top
                "   /##",
                "  /###",
                "######"
            )
        );
    }

    @Test
    public void Climb_up_a_slope_left()
    {
        World world = createWorld(
            "      ",
            "##\\   ",
            "###\\ j",
            "######"
        );

        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "      ",
                "##\\   ",
                "###`j ",  // Approaching
                "######"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "      ",
                "##^   ",
                "###j  ",  // Going up slope
                "######"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                " !    ",
                "##j   ",
                "###\\  ",  // Approaching top
                "######"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "<j    ",  // At top
                "##\\   ",
                "###\\  ",
                "######"
            )
        );
    }

    @Test
    public void Climb_down_a_slope_right()
    {
        World world = createWorld(
            "r     ",
            "##\\   ",
            "###\\  ",
            "######"
        );

        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                " r    ",
                "##-   ", // Approaching
                "###\\  ",
                "######"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "      ",
                "##r   ", // Going down slope
                "###@  ",
                "######"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "      ",
                "##\\   ", // Approaching bottom
                "###r_ ",
                "######"
            )
        );
        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "      ",
                "##\\   ",
                "###\\r>", // At bottom
                "######"
            )
        );
    }

    @Test
    public void Climb_down_a_slope_left()
    {
        World world = createWorld(
            "     j",
            "   /##",
            "  /###",
            "######"
        );

        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "    j ",
                "   =##", // Approaching
                "  /###",
                "######"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "      ",
                "   j##", // Going down slope
                "  %###",
                "######"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "      ",
                "   /##", // Approaching bottom
                " +j###",
                "######"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "      ",
                "   /##",
                "<j/###", // At bottom
                "######"
            )
        );
    }

    @Test
    public void Turn_on_a_slope_up_right()
    {
        World world = createWorld(
            "   #",
            "r /#",
            "####"
        );

        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "   #",
                " r~#",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "   #",
                "  }#",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "   #",
                " +j#",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "   #",
                "<j/#",
                "####"
            )
        );
    }

    @Test
    public void Turn_on_a_slope_up_left()
    {
        World world = createWorld(
            "#   ",
            "#\\ j",
            "####"
        );

        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "#   ",
                "#`j ",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "#   ",
                "#{  ",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "#   ",
                "#r_ ",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "#   ",
                "#\\r>",
                "####"
            )
        );
    }

    @Test
    public void Turn_on_a_slope_down_right()
    {
        World world = createWorld(
            " r  ",
            "##\\#",
            "####"
        );

        assertThat(
            renderWorld( world, true ),
            equalTo(
                " r  ",
                "##-#",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "    ",
                "##]#",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                " !  ",
                "##j#",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "<j  ",
                "##\\#",
                "####"
            )
        );
    }

    @Test
    public void Turn_on_a_slope_down_left()
    {
        World world = createWorld(
            "  j ",
            "#/##",
            "####"
        );

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "  j ",
                "#=##",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "    ",
                "#[##",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "  ' ",
                "#r##",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "  r>",
                "#/##",
                "####"
            )
        );
    }
}

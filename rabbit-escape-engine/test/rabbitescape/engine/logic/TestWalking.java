package rabbitescape.engine.logic;

import static org.hamcrest.MatcherAssert.*;
import static rabbitescape.engine.Tools.*;
import static rabbitescape.engine.textworld.TextWorldManip.*;

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
            renderWorld( world, false, false ),
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
            renderWorld( world, false, false ),
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
            renderWorld( world, true, false ),
            equalTo(
                "#|   ",
                "#####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, false, false ),
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
            renderWorld( world, false, false ),
            equalTo(
                "# j# ",
                "#####"
            )
        );
    }

    @Test
    public void Turn_when_you_hit_the_back_of_a_slope_on_left()
    {
        World world = createWorld(
            "/j  ",
            "####"
        );

        world.step();

        assertThat(
            renderWorld( world, false, false ),
            equalTo(
                "/r  ",
                "####"
            )
        );
    }

    @Test
    public void Turn_when_you_hit_the_back_of_a_slope_on_right()
    {
        World world = createWorld(
            "  r\\",
            "####"
        );

        world.step();

        assertThat(
            renderWorld( world, false, false ),
            equalTo(
                "  j\\",
                "####"
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
            renderWorld( world, true, false ),
            equalTo(
                "      ",
                "   /##",
                " r~###",  // Approaching
                "######"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "      ",
                "   $##",
                "  r###",  // Going up slope
                "######"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    ' ",
                "   r##",
                "  /###",  // Approaching end
                "######"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    r>",  // At top
                "   /##",
                "  /###",
                "######"
            )
        );
    }


    @Test
    public void Climb_up_a_bridge_right()
    {
        World world = createWorld(
            "      ",
            "   (##",
            "r (###",
            "######"
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "      ",
                "   (##",
                " r~###",  // Approaching
                "######"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "      ",
                "   $##",
                "  r###",  // Going up slope
                "######"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    ' ",
                "   r##",
                "  (###",  // Approaching end
                "######"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    r>",  // At top
                "   (##",
                "  (###",
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
            renderWorld( world, true, false ),
            equalTo(
                "      ",
                "##\\   ",
                "###`j ",  // Approaching
                "######"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "      ",
                "##^   ",
                "###j  ",  // Going up slope
                "######"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                " !    ",
                "##j   ",
                "###\\  ",  // Approaching top
                "######"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "<j    ",  // At top
                "##\\   ",
                "###\\  ",
                "######"
            )
        );
    }

    @Test
    public void Climb_up_a_bridge_left()
    {
        World world = createWorld(
            "      ",
            "##)   ",
            "###) j",
            "######"
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "      ",
                "##)   ",
                "###`j ",  // Approaching
                "######"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "      ",
                "##^   ",
                "###j  ",  // Going up slope
                "######"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                " !    ",
                "##j   ",
                "###)  ",  // Approaching top
                "######"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "<j    ",  // At top
                "##)   ",
                "###)  ",
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
            renderWorld( world, true, false ),
            equalTo(
                " r    ",
                "##-   ", // Approaching
                "###\\  ",
                "######"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "      ",
                "##r   ", // Going down slope
                "###@  ",
                "######"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "      ",
                "##\\   ", // Approaching bottom
                "###r_ ",
                "######"
            )
        );
        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "      ",
                "##\\   ",
                "###\\r>", // At bottom
                "######"
            )
        );
    }

    @Test
    public void Climb_bridge_a_slope_right()
    {
        World world = createWorld(
            "r     ",
            "##)   ",
            "###)  ",
            "######"
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                " r    ",
                "##-   ", // Approaching
                "###)  ",
                "######"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "      ",
                "##r   ", // Going down slope
                "###@  ",
                "######"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "      ",
                "##)   ", // Approaching bottom
                "###r_ ",
                "######"
            )
        );
        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "      ",
                "##)   ",
                "###)r>", // At bottom
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
            renderWorld( world, true, false ),
            equalTo(
                "    j ",
                "   =##", // Approaching
                "  /###",
                "######"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "      ",
                "   j##", // Going down slope
                "  %###",
                "######"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "      ",
                "   /##", // Approaching bottom
                " +j###",
                "######"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "      ",
                "   /##",
                "<j/###", // At bottom
                "######"
            )
        );
    }

    @Test
    public void Climb_down_a_bridge_left()
    {
        World world = createWorld(
            "     j",
            "   (##",
            "  (###",
            "######"
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    j ",
                "   =##", // Approaching
                "  (###",
                "######"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "      ",
                "   j##", // Going down slope
                "  %###",
                "######"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "      ",
                "   (##", // Approaching bottom
                " +j###",
                "######"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "      ",
                "   (##",
                "<j(###", // At bottom
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
            renderWorld( world, true, false ),
            equalTo(
                "   #",
                " r~#",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   #",
                "  }#",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   #",
                " +j#",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   #",
                "<j/#",
                "####"
            )
        );
    }

    @Test
    public void Turn_on_a_bridge_up_right()
    {
        World world = createWorld(
            "   #",
            "r (#",
            "####"
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   #",
                " r~#",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   #",
                "  }#",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   #",
                " +j#",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   #",
                "<j(#",
                "####"
            )
        );
    }

    @Test
    public void Turn_because_of_backslope_on_a_slope_up_right()
    {
        World world = createWorld(
            "   \\",
            " r/#",
            "####"
        );

        world.step();

        assertThat(
            renderWorld( world, false, false ),
            equalTo(
                "   \\",
                "  r#",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, false, false ),
            equalTo(
                "   \\",
                "  j#",
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
            renderWorld( world, true, false ),
            equalTo(
                "#   ",
                "#`j ",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "#   ",
                "#{  ",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "#   ",
                "#r_ ",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "#   ",
                "#\\r>",
                "####"
            )
        );
    }

    @Test
    public void Turn_on_a_bridge_up_left()
    {
        World world = createWorld(
            "#   ",
            "#) j",
            "####"
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "#   ",
                "#`j ",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "#   ",
                "#{  ",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "#   ",
                "#r_ ",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "#   ",
                "#)r>",
                "####"
            )
        );
    }

    @Test
    public void Turn_because_of_backslope_on_a_slope_up_left()
    {
        World world = createWorld(
            "/   ",
            "#\\j ",
            "####"
        );

        world.step();

        assertThat(
            renderWorld( world, false, false ),
            equalTo(
                "/   ",
                "#j  ",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, false, false ),
            equalTo(
                "/   ",
                "#r  ",
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
            "###\\"
        );

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                " r  ",
                "##-#",
                "###\\"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    ",
                "##]#",
                "###\\"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                " !  ",
                "##j#",
                "###\\"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "<j  ",
                "##\\#",
                "###\\"
            )
        );
    }

    @Test
    public void Turn_on_a_bridge_down_right()
    {
        World world = createWorld(
            " r  ",
            "##)#",
            "###)"
        );

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                " r  ",
                "##-#",
                "###)"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    ",
                "##]#",
                "###)"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                " !  ",
                "##j#",
                "###)"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "<j  ",
                "##)#",
                "###)"
            )
        );
    }

    @Test
    public void Turn_because_of_backslope_on_a_slope_down_right()
    {
        World world = createWorld(
            " r  ",
            "##\\\\",
            "###\\"
        );

        world.step();

        assertThat(
            renderWorld( world, false, false ),
            equalTo(
                "    ",
                "##r\\",
                "###\\"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, false, false ),
            equalTo(
                "    ",
                "##j\\",
                "###\\"
            )
        );
    }

    @Test
    public void Turn_on_a_slope_down_left()
    {
        World world = createWorld(
            "  j ",
            "#/##",
            "/###"
        );

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "  j ",
                "#=##",
                "/###"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    ",
                "#[##",
                "/###"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "  ' ",
                "#r##",
                "/###"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "  r>",
                "#/##",
                "/###"
            )
        );
    }

    @Test
    public void Turn_on_a_bridge_down_left()
    {
        World world = createWorld(
            "  j ",
            "#(##",
            "(###"
        );

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "  j ",
                "#=##",
                "(###"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    ",
                "#[##",
                "(###"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "  ' ",
                "#r##",
                "(###"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "  r>",
                "#(##",
                "(###"
            )
        );
    }

    @Test
    public void Turn_because_of_blackslope_on_a_slope_down_left()
    {
        World world = createWorld(
            "  j ",
            "//##",
            "/###"
        );

        world.step();

        assertThat(
            renderWorld( world, false, false ),
            equalTo(
                "    ",
                "/j##",
                "/###"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, false, false ),
            equalTo(
                "    ",
                "/r##",
                "/###"
            )
        );
    }

    @Test
    public void Slope_down_then_immediately_up_right()
    {
        World world = createWorld(
            "r   ",
            "#\\/#",
            "####"
        );

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "r   ",
                "#-/#",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    ",
                "#r,#",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   '",
                "#\\r#",
                "####"
            )
        );
    }

    @Test
    public void Bridge_down_then_immediately_up_right()
    {
        World world = createWorld(
            "r   ",
            "#)(#",
            "####"
        );

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "r   ",
                "#-(#",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    ",
                "#r,#",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   '",
                "#)r#",
                "####"
            )
        );
    }

    @Test
    public void Slope_down_then_immediately_up_left()
    {
        World world = createWorld(
            "   j",
            "#\\/#",
            "####"
        );

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   j",
                "#\\=#",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    ",
                "#.j#",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "!   ",
                "#j/#",
                "####"
            )
        );
    }

    @Test
    public void Bridge_down_then_immediately_up_left()
    {
        World world = createWorld(
            "   j",
            "#)(#",
            "####"
        );

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   j",
                "#)=#",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    ",
                "#.j#",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "!   ",
                "#j(#",
                "####"
            )
        );
    }

    @Test
    public void Slope_up_then_immediately_down_right()
    {
        World world = createWorld(
            "    ",
            "r/\\ ",
            "####"
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    ",
                " r& ",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    ",
                " /r_",
                "####"
            )
        );
    }

    @Test
    public void Bridge_up_then_immediately_down_right()
    {
        World world = createWorld(
            "    ",
            "r() ",
            "####"
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    ",
                " r& ",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    ",
                " (r_",
                "####"
            )
        );
    }

    @Test
    public void Slope_up_then_immediately_down_left()
    {
        World world = createWorld(
            "    ",
            " /\\j",
            "####"
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    ",
                " *j ",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    ",
                "+j\\ ",
                "####"
            )
        );
    }

    @Test
    public void Bridge_up_then_immediately_down_left()
    {
        World world = createWorld(
            "    ",
            " ()j",
            "####"
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    ",
                " *j ",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    ",
                "+j) ",
                "####"
            )
        );
    }

    @Test
    public void Slope_up_right_nothing_under()
    {
        World world = createWorld(
            "   /",
            "  / ",
            "r/  ",
            "####"
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   /",
                "  $ ",
                " r  ",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   $",
                "  r ",
                " /  ",
                "####"
            )
        );
    }

    @Test
    public void Bridge_up_right_nothing_under()
    {
        World world = createWorld(
            "   (",
            "  ( ",
            "r(  ",
            "####"
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   (",
                "  $ ",
                " r  ",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   $",
                "  r ",
                " (  ",
                "####"
            )
        );
    }

    @Test
    public void Slope_up_left_nothing_under()
    {
        World world = createWorld(
            "\\   ",
            " \\  ",
            "  \\j",
            "####"
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "\\   ",
                " ^  ",
                "  j ",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "^   ",
                " j  ",
                "  \\ ",
                "####"
            )
        );
    }

    @Test
    public void Bridge_up_left_nothing_under()
    {
        World world = createWorld(
            ")   ",
            " )  ",
            "  )j",
            "####"
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                ")   ",
                " ^  ",
                "  j ",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "^   ",
                " j  ",
                "  ) ",
                "####"
            )
        );
    }

    @Test
    public void Bridge_up_right_slope_down_right()
    {
        World world = createWorld(
            "   (",
            "  ( ",
            "r(  ",
            "#\\  "
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   (",
                "  $ ",
                " r  ",
                "#\\  "
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   $",
                "  r ",
                " (  ",
                "#\\  "
            )
        );
    }

    @Test
    public void Bridge_up_left_slope_down_left()
    {
        World world = createWorld(
            ")   ",
            " )  ",
            "  )j",
            "  /#"
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                ")   ",
                " ^  ",
                "  j ",
                "  /#"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "^   ",
                " j  ",
                "  ) ",
                "  /#"
            )
        );
    }


    @Test
    public void Down_then_Bridge_up_right_slope_down_right()
    {
        World world = createWorld(
            "    (",
            "r  ( ",
            "#)(  ",
            "##\\  "
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    (",
                "   ( ",
                "#r,  ",
                "##\\  "
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    (",
                "   $ ",
                "#)r  ",
                "##\\  "
            )
        );
    }

    @Test
    public void Down_then_bridge_up_left_slope_down_left()
    {
        World world = createWorld(
            ")    ",
            " )  j",
            "  )(#",
            "  /##"
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                ")    ",
                " )   ",
                "  .j#",
                "  /##"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                ")    ",
                " ^   ",
                "  j(#",
                "  /##"
            )
        );
    }

    @Test
    public void Mid_bridge_up_right_slope_down_right()
    {
        World world = createWorld(
            "   (",
            "  ( ",
            "r(\\ ",
            "#   "
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   (",
                "  $ ",
                " r\\ ",
                "#   "
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   $",
                "  r ",
                " (\\ ",
                "#   "
            )
        );
    }

    @Test
    public void Mid_bridge_up_left_slope_down_left()
    {
        World world = createWorld(
            ")   ",
            " )  ",
            " /)j",
            "   #"
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                ")   ",
                " ^  ",
                " /j ",
                "   #"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "^   ",
                " j  ",
                " /) ",
                "   #"
            )
        );
    }
}

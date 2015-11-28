package rabbitescape.engine.logic;

import static org.hamcrest.MatcherAssert.*;
import static rabbitescape.engine.Tools.*;
import static rabbitescape.engine.textworld.TextWorldManip.*;
import static rabbitescape.engine.util.WorldAssertions.*;

import org.junit.Test;

import rabbitescape.engine.World;

public class TestBashing
{
    @Test
    public void Token_not_next_to_wall_makes_useless_bash()
    {
        World world = createWorld(
            " rb   bj ",
            "#########"
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "  rI Jj  ",
                "#########"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "  r> <j  ",
                "#########"
            )
        );
    }

    @Test
    public void Bash_through_single_wall()
    {
        World world = createWorld(
            " rb#     #bj ",
            "#############"
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "  rK     Wj  ",
                "#############"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "  r>     <j  ",
                "#############"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   r>   <j   ",
                "#############"
            )
        );
    }

    @Test
    public void Bash_through_longer_wall()
    {
        World world = createWorld(
            " rb###     ###bj ",
            "#################"
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "  rK##     ##Wj  ",
                "#################"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "  r>##     ##<j  ",
                "#################"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   rK#     #Wj   ",
                "#################"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   r>#     #<j   ",
                "#################"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    rK     Wj    ",
                "#################"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    r>     <j    ",
                "#################"
            )
        );


        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "     r>   <j     ",
                "#################"
            )
        );
    }

    @Test
    public void Bashing_doesnt_last()
    {
        World world = createWorld(
                " rb# # # #bj ",
                "#############"
        );

        world.step();

        assertThat(
                renderWorld( world, true, false ),
                equalTo(
                        "  rK # # Wj  ",
                        "#############"
                )
        );

        world.step();

        assertThat(
                renderWorld( world, true, false ),
                equalTo(
                        "  r> # # <j  ",
                        "#############"
                )
        );

        world.step();

        assertThat(
                renderWorld( world, true, false ),
                equalTo(
                        "   r># #<j   ",
                        "#############"
                )
        );

        world.step();

        assertThat(
                renderWorld( world, true, false ),
                equalTo(
                        "    ?# #|    ",
                        "#############"
                )
        );

        world.step();

        assertThat(
                renderWorld( world, true, false ),
                equalTo(
                        "   <j# #r>   ",
                        "#############"
                )
        );
    }

    @Test
    public void Bash_through_single_slope()
    {
        assertWorldEvolvesLike(
            " rb/   " + "\n" +
            "#######",

            "  rK   " + "\n" +
            "#######",

            "  r>   " + "\n" +
            "#######",

            "   r>  " + "\n" +
            "#######",

            "    r> " + "\n" +
            "#######"
        );
    }

    @Test
    public void Bash_through_slope_plus_blocks()
    {
        assertWorldEvolvesLike(
            " rb/#\\  " + "\n" +
            "########",

            "  rK#\\  " + "\n" +
            "########",

            "  r>#\\  " + "\n" +
            "########",

            "   rK\\  " + "\n" +
            "########",

            "   r>\\  " + "\n" +
            "########",

            "    rK  " + "\n" +
            "########",

            "    r>  " + "\n" +
            "########",

            "     r> " + "\n" +
            "########",

            "      r>" + "\n" +
            "########"
        );
    }

    @Test
    public void Bash_on_single_slope()
    {
        assertWorldEvolvesLike(
            "    /  " + "\n" +
            "  r*   " + "\n" + // Bash token on a slope
            "#######" + "\n" +
            ":*=b/",

            "    K  " + "\n" +
            "   r   " + "\n" +
            "#######",

            "   r>  " + "\n" +
            "   /   " + "\n" +
            "#######",

            "    r  " + "\n" +
            "   /f  " + "\n" +
            "#######"
        );
    }

    @Test
    public void Bash_on_single_bridge()
    {
        assertWorldEvolvesLike(
            "    (  " + "\n" +
            "  r*   " + "\n" + // Bash token on a slope
            "#######" + "\n" +
            ":*=b(",

            "    K  " + "\n" +
            "   r   " + "\n" +
            "#######",

            "   r>  " + "\n" +
            "   (   " + "\n" +
            "#######",

            "    r  " + "\n" +
            "   (f  " + "\n" +
            "#######"
        );
    }

    @Test
    public void Bash_on_slope_followed_by_blocks()
    {
        assertWorldEvolvesLike(
            "    /  " + "\n" +
            "  r*#  " + "\n" + // Bash token on a slope
            "#######" + "\n" +
            ":*=b/",

            "    K  " + "\n" +
            "   r#  " + "\n" +
            "#######",

            "   r>  " + "\n" +
            "   /#  " + "\n" +
            "#######",

            "    r> " + "\n" +
            "   /#  " + "\n" +
            "#######"
        );
    }

    @Test
    public void Bash_on_slope_plus_blocks()
    {
        assertWorldEvolvesLike(
            "    /# " + "\n" +
            "  r*## " + "\n" + // Bash token on a slope
            "#######" + "\n" +
            ":*=b/",

            "    K# " + "\n" +
            "   r## " + "\n" +
            "#######",

            "   r># " + "\n" +
            "   /## " + "\n" +
            "#######",

            "    rK " + "\n" +
            "   /## " + "\n" +
            "#######"
        );
    }

    @Test
    public void Bash_on_bridge_plus_blocks()
    {
        assertWorldEvolvesLike(
            "    (# " + "\n" +
            "  r*## " + "\n" + // Bash token on a slope
            "#######" + "\n" +
            ":*=b(",

            "    K# " + "\n" +
            "   r## " + "\n" +
            "#######",

            "   r># " + "\n" +
            "   (## " + "\n" +
            "#######",

            "    rK " + "\n" +
            "   (## " + "\n" +
            "#######"
        );
    }

    @Test
    public void Bash_purposefully_at_top_of_slope()
    {
        assertWorldEvolvesLike(
            "rb/" + "\n" +
            "#/#",

            " r/" + "\n" +
            "#h#",

            "  K" + "\n" +
            "#r#",

            " r>" + "\n" +
            "#/#",

            "  r" + "\n" +
            "#/#"
        );
    }

    @Test
    public void Bash_uselessly_at_top_of_slope()
    {
        assertWorldEvolvesLike(
            "rb " + "\n" +
            "#/#",

            " r " + "\n" +
            "#h#",

            "   " + "\n" +
            "#rI",

            " r>" + "\n" +
            "#/#",

            "  r" + "\n" +
            "#/#"
        );

        assertWorldEvolvesLike(
            " bj" + "\n" +
            "#\\#",

            " j " + "\n" +
            "#a#",

            "   " + "\n" +
            "Jj#",

            "<j " + "\n" +
            "#\\#",

            "j  " + "\n" +
            "#\\#"
        );
    }
}

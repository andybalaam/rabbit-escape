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

    @Test
    public void Bashing_fails_if_first_block_is_unbreakable()
    {
        assertWorldEvolvesLike(
            "rbM" + "\n" +
            "###",

            " rI" + "\n" +
            "###",

            " ?M" + "\n" +
            "###",

            "<jM" + "\n" +
            "###"
        );
    }

    @Test
    public void Bashing_fails_if_later_block_is_unbreakable()
    {
        assertWorldEvolvesLike(
            "rb#M" + "\n" +
            "####",

            " rKM" + "\n" +
            "####",

            " r>M" + "\n" +
            "####",

            "  rI" + "\n" +
            "####",

            "  ?M" + "\n" +
            "####",

            " <jM" + "\n" +
            "####"
        );
    }

    @Test
    public void Standing_on_slope_bashing_fails_if_first_block_is_unbreakable()
    {
        assertWorldEvolvesLike(
            "  bM" + "\n" +
            " r/#" + "\n" +
            "####",

            "   M" + "\n" +
            "  rI" + "\n" +
            "####",

            "  ?M" + "\n" +
            "  /#" + "\n" +
            "####",

            "  jM" + "\n" + // This is a bit glitchy
            "  s#" + "\n" + // because the rabbit floats then falls.
            "####",

            "   M" + "\n" +
            " +j#" + "\n" +
            "####"
        );
    }

    @Test
    public void Bashing_does_not_destroy_water()
    {
        World world = createWorld(
            "rb*#",
            "####",
            ":*=\\n"
        );

        // Check the water is kept
        assertWorldEvolvesLike(
            world,
            2,
            new String[]{
                " *n#",
                "####",
                ":*=r{Bashing.stepsOfBashing:1,index:1}"
            });

        // Check that the water spreads and the rabbit moves through it.
        assertWorldEvolvesLike(
            world,
            1,
            new String[]{
                " n*#",
                "####",
                ":*=nr{Bashing.stepsOfBashing:2,index:1}",
                ":n=1,0,256",
                ":n=2,0,256"
            });
    }

    @Test
    public void Basher_on_bridge_trans_to_digger_must_not_airwalk_after()
    {
        World world = createWorld(
            "   ",
            "r* ",
            "## ",
            "   ",
            ":*=(bd"
        );

        world.step();
        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   ",
                " rI",
                "## ",
                "   "
            )
        );

        world.step();
        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   ",
                " D ",
                "## ",
                "   "
            )
        );

        world.step();
        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   ",
                " r ",
                "#D ",
                "   "
            )
        );

        world.step();
        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   ",
                "   ",
                "#D ",
                "   "
            )
        );

        world.step();
        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   ",
                "   ",
                "#r ",
                " f "
            )
        );
    }
}

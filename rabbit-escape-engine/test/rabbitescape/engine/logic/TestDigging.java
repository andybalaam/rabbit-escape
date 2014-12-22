package rabbitescape.engine.logic;

import static org.hamcrest.MatcherAssert.*;
import static rabbitescape.engine.Tools.*;
import static rabbitescape.engine.textworld.TextWorldManip.*;
import static rabbitescape.engine.util.WorldAssertions.*;

import org.junit.Test;

import rabbitescape.engine.World;

public class TestDigging
{
    @Test
    public void Dig_through_single_floor()
    {
        World world = createWorld(
            "rd ",
            "###",
            "   ",
            "   ",
            "###"
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                " r ",
                "#D#",
                "   ",
                "   ",
                "###"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                " r ",
                "#f#",
                " f ",
                "   ",
                "###"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   ",
                "# #",
                " r ",
                " f ",
                "###"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   ",
                "# #",
                "   ",
                " r>",
                "###"
            )
        );
    }

    @Test
    public void Dig_through_multilevel_floor()
    {
        World world = createWorld(
            "rd ",
            "###",
            "###",
            "###",
            "   ",
            "   ",
            "###"
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                " r ",
                "#D#",
                "###",
                "###",
                "   ",
                "   ",
                "###"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                " r ",
                "#f#",
                "###",
                "###",
                "   ",
                "   ",
                "###"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   ",
                "#r#",
                "#D#",
                "###",
                "   ",
                "   ",
                "###"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   ",
                "#r#",
                "#f#",
                "###",
                "   ",
                "   ",
                "###"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   ",
                "# #",
                "#r#",
                "#D#",
                "   ",
                "   ",
                "###"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   ",
                "# #",
                "#r#",
                "#f#",
                " f ",
                "   ",
                "###"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   ",
                "# #",
                "# #",
                "# #",
                " r ",
                " f ",
                "###"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   ",
                "# #",
                "# #",
                "# #",
                "   ",
                " r>",
                "###"
            )
        );
    }

    @Test
    public void Stop_after_single_gap()
    {
        World world = createWorld(
            "rd ",
            "###",
            "   ",
            "###"
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                " r ",
                "#D#",
                "   ",
                "###"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                " r ",
                "#f#",
                " f ",
                "###"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   ",
                "# #",
                " r>",
                "###"
            )
        );
    }

    @Test
    public void Stop_after_single_slope()
    {
        assertWorldEvolvesLike(
            " dj" + "\n" +
            " /#" + "\n" +
            "   " + "\n" +
            "###",

            "   " + "\n" +
            " D#" + "\n" +
            "   " + "\n" +
            "###",

            "   " + "\n" +
            " j#" + "\n" +
            " f " + "\n" +
            "###",

            "   " + "\n" +
            "  #" + "\n" +
            "<j " + "\n" +
            "###"
        );
    }

    @Test
    public void Stop_after_single_gap_after_multilevel_dig()
    {
        World world = createWorld(
            "rd ",
            "###",
            "###",
            "###",
            "   ",
            "###"
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                " r ",
                "#D#",
                "###",
                "###",
                "   ",
                "###"
            )
        );

        world.step();
        world.step();
        world.step();
        world.step();
        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   ",
                "# #",
                "#r#",
                "#f#",
                " f ",
                "###"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   ",
                "# #",
                "# #",
                "# #",
                " r>",
                "###"
            )
        );
    }

    @Test
    public void Dig_through_single_slope()
    {
        assertWorldEvolvesLike(
            " r " + "\n" +
            " * " + "\n" +
            "   " + "\n" +
            "###" + "\n" +
            ":*=d/",

            "   " + "\n" +
            " D " + "\n" +
            "   " + "\n" +
            "###",

            "   " + "\n" +
            " r " + "\n" +
            " f " + "\n" +
            "###"
        );
    }

    @Test
    public void Dig_through_single_bridge()
    {
        assertWorldEvolvesLike(
            " r " + "\n" +
            " * " + "\n" +
            "   " + "\n" +
            "###" + "\n" +
            ":*=d(",

            "   " + "\n" +
            " D " + "\n" +
            "   " + "\n" +
            "###",

            "   " + "\n" +
            " r " + "\n" +
            " f " + "\n" +
            "###"
        );
    }

    @Test
    public void Dig_through_slope_plus_blocks()
    {
        assertWorldEvolvesLike(
            " r " + "\n" +
            " * " + "\n" +
            " # " + "\n" +
            " # " + "\n" +
            "   " + "\n" +
            "###" + "\n" +
            ":*=d/",

            "   " + "\n" +
            " D " + "\n" +
            " # " + "\n" +
            " # " + "\n" +
            "   " + "\n" +
            "###",

            "   " + "\n" +
            " r " + "\n" +
            " D " + "\n" +
            " # " + "\n" +
            "   " + "\n" +
            "###",

            "   " + "\n" +
            " r " + "\n" +
            " f " + "\n" +
            " # " + "\n" +
            "   " + "\n" +
            "###",

            "   " + "\n" +
            "   " + "\n" +
            " r " + "\n" +
            " D " + "\n" +
            "   " + "\n" +
            "###",

            "   " + "\n" +
            "   " + "\n" +
            " r " + "\n" +
            " f " + "\n" +
            " f " + "\n" +
            "###"
        );
    }

    @Test
    public void Dig_through_bridge_plus_blocks()
    {
        assertWorldEvolvesLike(
            " r " + "\n" +
            " * " + "\n" +
            " # " + "\n" +
            " # " + "\n" +
            "   " + "\n" +
            "###" + "\n" +
            ":*=d(",

            "   " + "\n" +
            " D " + "\n" +
            " # " + "\n" +
            " # " + "\n" +
            "   " + "\n" +
            "###",

            "   " + "\n" +
            " r " + "\n" +
            " D " + "\n" +
            " # " + "\n" +
            "   " + "\n" +
            "###",

            "   " + "\n" +
            " r " + "\n" +
            " f " + "\n" +
            " # " + "\n" +
            "   " + "\n" +
            "###",

            "   " + "\n" +
            "   " + "\n" +
            " r " + "\n" +
            " D " + "\n" +
            "   " + "\n" +
            "###",

            "   " + "\n" +
            "   " + "\n" +
            " r " + "\n" +
            " f " + "\n" +
            " f " + "\n" +
            "###"
        );
    }

    @Test
    public void Dig_through_bridge_plus_bridges()
    {
        assertWorldEvolvesLike(
            " r " + "\n" +
            " * " + "\n" +
            " ( " + "\n" +
            " ( " + "\n" +
            "   " + "\n" +
            "###" + "\n" +
            ":*=d(",

            "   " + "\n" +
            " D " + "\n" +
            " ( " + "\n" +
            " ( " + "\n" +
            "   " + "\n" +
            "###",

            "   " + "\n" +
            " r " + "\n" +
            " h " + "\n" +
            " ( " + "\n" +
            "   " + "\n" +
            "###",

            "   " + "\n" +
            "   " + "\n" +
            " D " + "\n" +
            " ( " + "\n" +
            "   " + "\n" +
            "###",

            "   " + "\n" +
            "   " + "\n" +
            " r " + "\n" +
            " h " + "\n" +
            "   " + "\n" +
            "###",

            "   " + "\n" +
            "   " + "\n" +
            "   " + "\n" +
            " D " + "\n" +
            "   " + "\n" +
            "###",

            "   " + "\n" +
            "   " + "\n" +
            "   " + "\n" +
            " r " + "\n" +
            " f " + "\n" +
            "###"
        );
    }
}

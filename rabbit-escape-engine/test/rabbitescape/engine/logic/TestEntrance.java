package rabbitescape.engine.logic;

import static org.hamcrest.MatcherAssert.*;
import static rabbitescape.engine.Tools.*;
import static rabbitescape.engine.textworld.TextWorldManip.*;
import static rabbitescape.engine.util.Util.*;

import org.junit.Test;

import rabbitescape.engine.World;

public class TestEntrance
{
    @Test
    public void Rabbit_comes_out_of_entrance()
    {
        World world = createWorld(
            ":num_rabbits=1",
            " Q   ",
            "     ",
            "     ",
            "#####"
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                " Q   ",
                " r   ",
                " f   ",
                "#####"
            )
        );
    }

    @Test
    public void Rabbits_come_out_every_other_step_when_delay_is_2()
    {
        World world = createWorld(
            ":num_rabbits=10",
            ":rabbit_delay=2",
            " Q              ",
            "                ",
            "################"
        );

        fiveSteps( world );

        assertThat(
            renderWorld( world, false, false ),
            equalTo(
                " Q              ",
                " r r r          ",
                "################"
            )
        );


        fiveSteps( world );

        assertThat(
            renderWorld( world, false, false ),
            equalTo(
                " Q              ",
                "  r r r r r     ",
                "################"
            )
        );
    }

    @Test
    public void Rabbits_come_out_every_5_when_delay_is_5()
    {
        World world = createWorld(
            ":num_rabbits=10",
            ":rabbit_delay=5",
            " Q              ",
            "                ",
            "################"
        );

        fiveSteps( world );

        assertThat(
            renderWorld( world, false, false ),
            equalTo(
                " Q              ",
                "     r          ",
                "################"
            )
        );


        world.step();

        assertThat(
            renderWorld( world, false, false ),
            equalTo(
                " Q              ",
                " r    r         ",
                "################"
            )
        );
    }

    @Test
    public void Limit_to_1_rabbit_works()
    {
        World world = createWorld(
            ":num_rabbits=1",
            ":rabbit_delay=2",
            " Q              ",
            "                ",
            "################"
        );

        fiveSteps( world );

        assertThat(
            renderWorld( world, false, false ),
            equalTo(
                " Q              ",
                "     r          ",
                "################"
            )
        );

        fiveSteps( world );

        assertThat(
            renderWorld( world, false, false ),
            equalTo(
                " Q              ",
                "          r     ",
                "################"
            )
        );
    }


    @Test
    public void Limit_to_4_rabbits_works()
    {
        World world = createWorld(
            ":num_rabbits=4",
            ":rabbit_delay=1",
            " Q              ",
            "                ",
            "################"
        );

        fiveSteps( world );

        assertThat(
            renderWorld( world, false, false ),
            equalTo(
                " Q              ",
                "  rrrr          ",
                "################"
            )
        );

        fiveSteps( world );

        assertThat(
            renderWorld( world, false, false ),
            equalTo(
                " Q              ",
                "       rrrr     ",
                "################"
            )
        );
    }

    // ---

    private void fiveSteps( World world )
    {
        for( @SuppressWarnings( "unused" ) int t : range( 5 ) )
        {
            world.step();
        }
    }
}

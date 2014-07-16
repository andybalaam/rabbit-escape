package rabbitescape.engine.logic;

import static org.hamcrest.MatcherAssert.*;
import static rabbitescape.engine.TextWorldManip.*;
import static rabbitescape.engine.Tools.*;

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
            renderWorld( world, false ),
            equalTo(
                " Q   ",
                " r   ",
                "     ",
                "#####"
            )
        );
    }
}

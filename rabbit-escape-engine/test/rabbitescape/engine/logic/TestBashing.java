package rabbitescape.engine.logic;

import static org.hamcrest.MatcherAssert.*;
import static rabbitescape.engine.Tools.*;
import static rabbitescape.engine.textworld.TextWorldManip.*;

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
            renderWorld( world, true ),
            equalTo(
                "  rI Jj  ",
                "#########"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true ),
            equalTo(
                "  r> <j  ",
                "#########"
            )
        );
    }
}

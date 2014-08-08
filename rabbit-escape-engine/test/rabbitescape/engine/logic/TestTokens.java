package rabbitescape.engine.logic;

import static org.hamcrest.MatcherAssert.*;
import static rabbitescape.engine.Tools.*;
import static rabbitescape.engine.textworld.TextWorldManip.*;

import org.junit.Test;

import rabbitescape.engine.World;

public class TestTokens
{
    @Test
    public void Tokens_fall_slowly_and_stop_on_ground()
    {
        World world = createWorld(
            " b ",
            "   ",
            "   ",
            "###"
        );

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                " b ",
                " z ",
                "   ",
                "###"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   ",
                " b ",
                " z ",
                "###"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   ",
                "   ",
                " b ",
                "###"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   ",
                "   ",
                " b ",
                "###"
            )
        );
    }
}

package rabbitescape.engine.logic;

import static org.hamcrest.MatcherAssert.*;
import static rabbitescape.engine.Tools.*;
import static rabbitescape.engine.textworld.TextWorldManip.*;

import org.junit.Test;

import rabbitescape.engine.World;

public class TestTokens
{
    // TODO: slopes and bridges

    @Test
    public void Tokens_fall_slowly_and_stop_on_ground()
    {
        World world = createWorld(
            "bdik",
            "    ",
            "    ",
            "####"
        );

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "bdik",
                "ffff",
                "    ",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    ",
                "bdik",
                "ffff",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    ",
                "    ",
                "bdik",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    ",
                "    ",
                "bdik",
                "####"
            )
        );
    }
}

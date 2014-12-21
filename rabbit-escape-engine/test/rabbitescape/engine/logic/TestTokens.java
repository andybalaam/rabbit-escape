package rabbitescape.engine.logic;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import static rabbitescape.engine.textworld.TextWorldManip.*;
import static rabbitescape.engine.util.WorldAssertions.*;

import org.junit.Test;

import rabbitescape.engine.World;

public class TestTokens
{
    // TODO: slopes and bridges

    @Test
    public void Tokens_fall_slowly_and_stop_on_ground()
    {
        assertWorldEvolvesLike(
            "bdikc" + "\n" +
            "     " + "\n" +
            "     " + "\n" +
            "     " + "\n" +
            "#####",

            "     " + "\n" +
            "bdikc" + "\n" +
            "fffff" + "\n" +
            "     " + "\n" +
            "#####",

            "     " + "\n" +
            "     " + "\n" +
            "bdikc" + "\n" +
            "fffff" + "\n" +
            "#####",

            "     " + "\n" +
            "     " + "\n" +
            "     " + "\n" +
            "bdikc" + "\n" +
            "#####",

            "     " + "\n" +
            "     " + "\n" +
            "     " + "\n" +
            "bdikc" + "\n" +
            "#####"
        );
    }

    @Test
    public void Tokens_disappear_when_they_drop_outside_world()
    {
        World world = createWorld(
            "bdikc",
            "     "
        );

        // Sanity - we have 5 things
        assertThat( world.things.size(), equalTo( 5 ) );

        world.step();

        // Still 5 things, not off bottom yet
        assertThat( world.things.size(), equalTo( 5 ) );

        world.step();

        // Now off bottom - all gone
        assertThat( world.things.size(), equalTo( 0 ) );
    }
}

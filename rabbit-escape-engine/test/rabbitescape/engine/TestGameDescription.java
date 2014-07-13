package rabbitescape.engine;

import static org.hamcrest.MatcherAssert.*;
import static rabbitescape.engine.TextWorldManip.*;
import static rabbitescape.engine.Tools.*;

import org.junit.Test;

public class TestGameDescription
{
    @Test
    public void Walking_right()
    {
        World world = createWorld(
            "     ",
            "     ",
            " r   ",
            "#####"
        );

        ChangeDescription desc = world.describeChanges();

        assertThat(
            renderChangeDescription( world, desc ),
            equalTo(
                "     ",
                "     ",
                " >   ",
                "     "
            )
        );
    }

}

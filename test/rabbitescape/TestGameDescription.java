package rabbitescape;

import static org.hamcrest.MatcherAssert.*;
import static rabbitescape.TextWorldManip.*;
import static rabbitescape.Tools.*;

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

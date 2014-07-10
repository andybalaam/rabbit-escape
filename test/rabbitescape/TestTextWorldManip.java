package rabbitescape;

import static org.hamcrest.MatcherAssert.*;
import static rabbitescape.ChangeDescription.State.*;
import static rabbitescape.TextWorldManip.*;
import static rabbitescape.Tools.*;

import org.junit.Test;

public class TestTextWorldManip
{
    @Test
    public void Round_trip_basic_world()
    {
        String[] lines = {
            "#####",
            "#   #",
            "#   #",
            "#   #",
            "#r j#",
            "#####"
        };

        assertThat(
            renderWorld( createWorld( lines ) ),
            equalTo( lines )
        );
    }

    @Test
    public void Render_walking_rabbits()
    {
        World world = createWorld(
            "   ",
            "   ",
            "   "
        );

        ChangeDescription desc = new ChangeDescription();
        desc.add( 0, 0, RABBIT_WALKING_RIGHT );
        desc.add( 2, 1, RABBIT_WALKING_LEFT  );
        desc.add( 1, 2, RABBIT_WALKING_RIGHT );

        assertThat(
            renderChangeDescription( world, desc ),
            equalTo(
                ">  ",
                "  <",
                " > "
            )
        );
    }
}

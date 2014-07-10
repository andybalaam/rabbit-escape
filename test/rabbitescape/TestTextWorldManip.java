package rabbitescape;

import static org.hamcrest.MatcherAssert.*;
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
}

package rabbitescape.engine.util;

import static org.hamcrest.MatcherAssert.*;
import static rabbitescape.engine.Tools.*;
import static rabbitescape.engine.textworld.TextWorldManip.*;
import static rabbitescape.engine.util.Util.*;
import rabbitescape.engine.World;

public class WorldAssertions
{
    public static void assertWorldEvolvesLike(
        String initialState, String... laterStates )
    {
        World world = createWorld( split( initialState, "\n" ) );

        for ( String state : laterStates )
        {
            world.step();

            assertThat(
                renderWorld( world, true, false ),
                equalTo( split( state, "\n" ) )
            );
        }
    }
}

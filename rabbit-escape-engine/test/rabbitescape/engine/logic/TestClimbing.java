package rabbitescape.engine.logic;

import static rabbitescape.engine.util.WorldAssertions.*;

import org.junit.Test;

public class TestClimbing
{
    @Test
    public void Climb_start()
    {
        assertWorldEvolvesLike(
            "   #" + "\n" +
            "   #" + "\n" +
            "####",

            "   #" + "\n" +
            "   #" + "\n" +
            "####"
        );
    }
}

package rabbitescape.engine.logic;

import static rabbitescape.engine.util.WorldAssertions.*;

import org.junit.Test;

public class TestExploding
{
    @Test
    public void Rabbit_explodes_when_picks_up_token()
    {
        assertWorldEvolvesLike(
            " rp " + "\n" +
            "####",

            "  P " + "\n" +
            "####",

            "    " + "\n" +
            "####"
        );
    }
}

package rabbitescape.engine.logic;

import static rabbitescape.engine.util.WorldAssertions.*;

import org.junit.Test;

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
}

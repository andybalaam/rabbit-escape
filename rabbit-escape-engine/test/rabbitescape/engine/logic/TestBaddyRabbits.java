package rabbitescape.engine.logic;

import static rabbitescape.engine.util.WorldAssertions.*;

import org.junit.Test;

public class TestBaddyRabbits
{
    @Test
    public void If_a_rabbit_meets_a_baddy_rabbit_they_both_explode()
    {
        assertWorldEvolvesLike(
            "r   y" + "\n" +
            "#####",

            " r<y " + "\n" +
            "#####",

            "  Z  " + "\n" +
            "#####",

            "     " + "\n" +
            "#####"
        );
    }
}
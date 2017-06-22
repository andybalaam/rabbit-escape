package rabbitescape.engine.logic;

import static rabbitescape.engine.util.WorldAssertions.*;

import org.junit.Test;

public class TestRabbots
{
    @Test
    public void If_a_rabbit_meets_a_rabbot_they_both_explode()
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
    @Test
    public void If_a_rabbot_gets_a_bridging_token_it_still_explodes()
    {
        assertWorldEvolvesLike(
            "ti j" + "\n" +
            "####",

            " <B " + "\n" +
            "####",

            " Z  " + "\n" +
            "####",

            "    " + "\n" +
            "####"
        );
    }

    @Test
    public void If_a_rabbit_will_cross_over_with_a_rabbot_the_rabbot_will_stop()
    {
        assertWorldEvolvesLike(
            "t  j" + "\n" +
            "####",

            " <j " + "\n" +  // t is waiting
            "####",

            " Z  " + "\n" +
            "####",

            "    " + "\n" +
            "####"
        );
    }
}
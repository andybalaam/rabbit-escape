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

    @Test
    public void Stopping_behaviour_is_suppressed_if_rabbot_is_a_blocker()
    {
        assertWorldEvolvesLike(
            "r  ky j  r tk  j" + "\n" +
            "################",

            " r>H<j    r>H<j " + "\n" +  // Both rabbots are now blocking
            "################",

            "  ?H|      ?H|  " + "\n" +  // The rabbits turn
            "################",

            " <jHr>    <jHr> " + "\n" +
            "################",

            "<j H r>  <j H r>" + "\n" +  // The rabbots are still blockers
            "################"
        );
    }

    @Test
    public void Stopping_behaviour_is_suppressed_if_rabbit_is_a_blocker()
    {
        assertWorldEvolvesLike(
            "t  kj y  t rk  y" + "\n" +
            "################",

            " t>H<y    t>H<y " + "\n" +  // Both rabbits are now blocking
            "################",

            "  ?H|      ?H|  " + "\n" +  // The rabbots turn
            "################",

            " <yHt>    <yHt> " + "\n" +
            "################",

            "<y H t>  <y H t>" + "\n" +  // The rabbits are still blockers
            "################"
        );
    }

    @Test
    public void Stopping_behaviour_is_suppressed_if_rabbot_is_falling()
    {
        assertWorldEvolvesLike(
            "ry" + "\n" +
            "  " + "\n" +
            "  " + "\n" +
            "  " + "\n" +
            "  " + "\n" +
            "  " + "\n" +
            "  ",

            "  " + "\n" +
            "  " + "\n" +
            "ry" + "\n" +
            "ff" + "\n" +
            "ff" + "\n" +
            "  " + "\n" +
            "  ",

            "  " + "\n" +
            "  " + "\n" +
            "  " + "\n" +
            "  " + "\n" +
            "ry" + "\n" +
            "ff" + "\n" +
            "ff"
        );
    }

    @Test
    public void Stopping_behaviour_is_suppressed_if_rabbot_is_digging()
    {
        assertWorldEvolvesLike(
            "ry" + "\n" +
            "  " + "\n" +
            " d" + "\n" +
            " #" + "\n" +
            "  " + "\n" +
            "  " + "\n" +
            "  ",

            "  " + "\n" +
            "  " + "\n" +
            "ry" + "\n" +
            "fD" + "\n" +
            "f " + "\n" +
            "  " + "\n" +
            "  ",

            "  " + "\n" +
            "  " + "\n" +
            "  " + "\n" +
            " D" + "\n" +
            "r " + "\n" +
            "f " + "\n" +
            "f ",

            "  " + "\n" +
            "  " + "\n" +
            "  " + "\n" +
            " y" + "\n" +
            " f" + "\n" +
            " f" + "\n" +
            "r "
        );
    }
}
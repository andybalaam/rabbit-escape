package rabbitescape.engine.logic;

import static rabbitescape.engine.util.WorldAssertions.assertWorldEvolvesLike;

import org.junit.Test;

public class TestDrowning
{
    @Test
    public void rabbits_drown_when_pipes_pump_water_on_them()
    {
        assertWorldEvolvesLike(
            "#P#" + "\n" +
            "#r#" + "\n" +
            "###",

            "#P#" + "\n" +
            "#|#" + "\n" +
            "###",

            "#P#" + "\n" +
            "#~#" + "\n" +
            "###",

            "#P#" + "\n" +
            "#N#" + "\n" +
            "###"
        );
    }

    @Test
    public void rabbits_drown_2_fails()
    {
        assertWorldEvolvesLike(
            "#P#" + "\n" +
            "#r#" + "\n" +
            "###",

            "#P#" + "\n" +
            "#|#" + "\n" +
            "###",

            "#P#" + "\n" +
            "#~#" + "\n" +
            "###"
        );
    }

    @Test
    public void rabbits_drown_3_fails()
    {
        assertWorldEvolvesLike(
            "#P#" + "\n" +
            "#r#" + "\n" +
            "###",

            "#P#" + "\n" +
            "#|#" + "\n" +
            "###",

            "#P#" + "\n" +
            "#`#" + "\n" +
            "###"
        );
    }

    @Test
    public void rabbits_drown_4_passes()
    {
        assertWorldEvolvesLike(
            "#P#" + "\n" +
            "#r#" + "\n" +
            "###",

            "#P#" + "\n" +
            "#|#" + "\n" +
            "###"
        );
    }
}

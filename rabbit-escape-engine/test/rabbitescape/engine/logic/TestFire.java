package rabbitescape.engine.logic;

import static rabbitescape.engine.util.WorldAssertions.*;

import org.junit.Test;

public class TestFire
{

    @Test
    public void Fire_falls()
    {
        assertWorldEvolvesLike(
            "AA " + "\n" +
            "  A" + "\n" +
            "   " + "\n" +
            "\\/ " + "\n" +
            "###",

            "   " + "\n" +
            "AA " + "\n" +
            "ggA" + "\n" +
            "\\/g" + "\n" +
            "###",

            "   " + "\n" +
            "   " + "\n" +
            "AA " + "\n" +
            "ggA" + "\n" +
            "###",

            "   " + "\n" +
            "   " + "\n" +
            "   " + "\n" +
            "AAA" + "\n" +
            "###",

            "   " + "\n" +
            "   " + "\n" +
            "   " + "\n" +
            "AAA" + "\n" +
            "###"
            );
    }

    @Test
    public void Rabbit_burns_on_slope()
    {
        assertWorldEvolvesLike(
            "  A" + "\n" +
            "r /" + "\n" +
            "###",

            "   " + "\n" +
            " r~" + "\n" +
            "###",

            "   " + "\n" +
            "  X" + "\n" +
            "###",

            "   " + "\n" +
            "  A" + "\n" +
            "###"
        );
    }

    @Test
    public void Rabbits_fall_faster_than_fire_and_fire_falls_from_world()
    {
        assertWorldEvolvesLike(
            "rr" + "\n" +
            "A " + "\n" +
            " A" + "\n" +
            "  " + "\n" +
            "  " + "\n" +
            "  ",

            "  " + "\n" +
            "  " + "\n" +
            "Xr" + "\n" +
            "gf" + "\n" +
            " g" + "\n" +
            "  ",

            "  " + "\n" +
            "  " + "\n" +
            "  " + "\n" +
            "A " + "\n" +
            "gX" + "\n" +
            " g",

            "  " + "\n" +
            "  " + "\n" +
            "  " + "\n" +
            "  " + "\n" +
            "A " + "\n" +
            "gA",

            "  " + "\n" +
            "  " + "\n" +
            "  " + "\n" +
            "  " + "\n" +
            "  " + "\n" +
            "A ",

            "  " + "\n" +
            "  " + "\n" +
            "  " + "\n" +
            "  " + "\n" +
            "  " + "\n" +
            "  "
        );
    }

    @Test
    public void Fire_falls_on_bridger()
    {
        assertWorldEvolvesLike(
            "   Ar A" + "\n" +
            " rb# b#" + "\n" +
            "### ## " + "\n" +
            "       " + "\n" +
            "       " + "\n" +
            "ri ri  " + "\n" +
            "#######",

            "   A  A" + "\n" +
            "  rKr>#" + "\n" +
            "### ## " + "\n" +
            "       " + "\n" +
            "       " + "\n" +
            " rB rB " + "\n" +
            "#######",

            "   A  A" + "\n" +
            "  rg rK" + "\n" +
            "### ## " + "\n" +
            "       " + "\n" +
            "       " + "\n" +
            " r[ r[ " + "\n" +
            "#######",

            "      A" + "\n" +
            "   X rg" + "\n" +
            "###g## " + "\n" +
            "       " + "\n" +
            "       " + "\n" +
            " r{ r{ " + "\n" +
            "#######",

            "       " + "\n" +
            "      X" + "\n" +
            "###A##g" + "\n" +
            "   g   " + "\n" +
            "   B  B" + "\n" +
            "  r  r " + "\n" +
            "#######",

            "       " + "\n" +
            "       " + "\n" +
            "### ##A" + "\n" +
            "   A  g" + "\n" +
            "   g  [" + "\n" +
            "  r  r " + "\n" +
            "#######",

            "       " + "\n" +
            "       " + "\n" +
            "### ## " + "\n" +
            "      A" + "\n" +
            "   {  g" + "\n" +
            "  rg r " + "\n" +
            "#######",

            "       " + "\n" +
            "       " + "\n" +
            "### ## " + "\n" +
            "    '  " + "\n" +
            "   r  X" + "\n" +
            "  (A ( " + "\n" +
            "#######",

            "       " + "\n" +
            "       " + "\n" +
            "### ## " + "\n" +
            "    r  " + "\n" +
            "   (f A" + "\n" +
            "  (Af( " + "\n" +
            "#######",

            "       " + "\n" +
            "       " + "\n" +
            "### ## " + "\n" +
            "       " + "\n" +
            "   (  A" + "\n" +
            "  (Ar~ " + "\n" +
            "#######",

            "       " + "\n" +
            "       " + "\n" +
            "### ## " + "\n" +
            "       " + "\n" +
            "   (  $" + "\n" +
            "  (A r " + "\n" +
            "#######",

            "       " + "\n" +
            "       " + "\n" +
            "### ## " + "\n" +
            "       " + "\n" +
            "   (  X" + "\n" +
            "  (A ( " + "\n" +
            "#######",

            "       " + "\n" +
            "       " + "\n" +
            "### ## " + "\n" +
            "       " + "\n" +
            "   (  A" + "\n" +
            "  (A ( " + "\n" +
            "#######",

            "       " + "\n" +
            "       " + "\n" +
            "### ## " + "\n" +
            "       " + "\n" +
            "   (  A" + "\n" +
            "  (A ( " + "\n" +
            "#######"
        );
    }
}

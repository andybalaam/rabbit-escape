package rabbitescape.engine.logic;

import static rabbitescape.engine.util.WorldAssertions.*;

import org.junit.Test;

public class TestBlocking
{
    @Test
    public void Blocker_stands_still()
    {
        assertWorldEvolvesLike(
            " rk " + "\n" +
            "####",

            "  H " + "\n" +
            "####",

            "  H " + "\n" +
            "####",

            "  H " + "\n" +
            "####"
        );
    }

    @Test
    public void Blocker_prevents_others_passing()
    {
        assertWorldEvolvesLike(
            "r rk " + "\n" +
            "#####",

            " r>H " + "\n" +
            "#####",

            "  ?H " + "\n" +
            "#####",

            " <jH " + "\n" +
            "#####"
        );
    }

    @Test
    public void Blocker_prevents_others_passing_on_slope_up_start()
    {
        assertWorldEvolvesLike(
            "r r* " + "\n" +
            "#####" + "\n" +
            ":*=k/",

            " r>H " + "\n" +
            "#####",

            "  ?H " + "\n" +
            "#####",

            " <jH " + "\n" +
            "#####"
        );
    }

    @Test
    public void Blocker_prevents_others_passing_on_slope_up_mid()
    {
        assertWorldEvolvesLike(
            "     /" + "\n" +
            "    *#" + "\n" +
            "r r/##" + "\n" +
            "######" + "\n" +
            ":*=k/",

            "     /" + "\n" +
            "    $#" + "\n" +
            " r>r##" + "\n" +
            "######",

            "     /" + "\n" +
            "    H#" + "\n" +
            "  r~##" + "\n" +
            "######",

            "     /" + "\n" +
            "    H#" + "\n" +
            "   ?##" + "\n" +
            "######",

            "     /" + "\n" +
            "    H#" + "\n" +
            "  +j##" + "\n" +
            "######"
        );
    }

    @Test
    public void Blocker_prevents_others_passing_on_slope_up_end()
    {
        assertWorldEvolvesLike(
            "     k" + "\n" +
            "    /#" + "\n" +
            "r r/##" + "\n" +
            "######",

            "     k" + "\n" +
            "    $#" + "\n" +
            " r>r##" + "\n" +
            "######",

            "     '" + "\n" +
            "    r#" + "\n" +
            "  r~##" + "\n" +
            "######",

            "     H" + "\n" +
            "    $#" + "\n" +
            "   r##" + "\n" +
            "######",

            "     H" + "\n" +
            "    ?#" + "\n" +
            "   /##" + "\n" +
            "######",

            "     H" + "\n" +
            "    j#" + "\n" +
            "   %##" + "\n" +
            "######"
        );
    }

    @Test
    public void Blocker_prevents_others_passing_on_slope_down_start()
    {
        assertWorldEvolvesLike(
            "r r  " + "\n" +
            "###* " + "\n" +
            "#####" + "\n" +
            ":*=k\\",

            " r>  " + "\n" +
            "###H " + "\n" +
            "#####",

            "  ?  " + "\n" +
            "###H " + "\n" +
            "#####",

            " <j  " + "\n" +
            "###H " + "\n" +
            "#####"
        );
    }

    @Test
    public void Blocker_prevents_others_passing_on_slope_down_mid()
    {
        assertWorldEvolvesLike(
            "r r  " + "\n" +
            "###\\ " + "\n" +
            "####*" + "\n" +
            ":*=k\\",

            " r>  " + "\n" +
            "###r " + "\n" +
            "####@",

            "  r  " + "\n" +
            "###- " + "\n" +
            "####H",

            "     " + "\n" +
            "###] " + "\n" +
            "####H",

            "  !  " + "\n" +
            "###j " + "\n" +
            "####H"
        );
    }

    @Test
    public void Blocker_prevents_others_passing_on_slope_down_end()
    {
        assertWorldEvolvesLike(
            "  j j" + "\n" +
            "k/###" + "\n" +
            "#####",

            "  <j " + "\n" +
            "+j###" + "\n" +
            "#####",

            "  j  " + "\n" +
            "H=###" + "\n" +
            "#####",

            "     " + "\n" +
            "H[###" + "\n" +
            "#####",

            "  '  " + "\n" +
            "Hr###" + "\n" +
            "#####"
        );
    }
}

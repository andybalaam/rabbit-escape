package rabbitescape.engine.logic;

import static org.hamcrest.MatcherAssert.*;
import static rabbitescape.engine.Tools.*;
import static rabbitescape.engine.textworld.TextWorldManip.*;
import static rabbitescape.engine.util.WorldAssertions.*;

import org.junit.Test;

import rabbitescape.engine.World;

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

    @Test
    public void Blocker_on_bridge_and_slope()
    {
        assertWorldEvolvesLike(
            "       j" + "\n" +
            "        " + "\n" +
            "  jr   #" + "\n" +
            "k  #   #" + "\n" +
            ")j   k(j" + "\n" +
            " ) rr(  " + "\n" +
            "k ) (   " + "\n" +
            "\\jj  k #" + "\n" +
            "#\\  r/##" + "\n" +
            "##\\(/###" + "\n" +
            "###O####" + "\n" +
            "########",

            "        " + "\n" +
            "      <j" + "\n" +
            "    r  #" + "\n" +
            "   #f  #" + "\n" +
            "^ j f ( " + "\n" +
            " jf  $  " + "\n" +
            "  a r <j" + "\n" +
            "^  r   #" + "\n" +
            "#^ f $##" + "\n" +
            "##jhr###" + "\n" +
            "###O####" + "\n" +
            "########",

            "        " + "\n" +
            "      j " + "\n" +
            "      f#" + "\n" +
            "   #  f#" + "\n" +
            "H   r ( " + "\n" +
            " ^  fH  " + "\n" +
            "  j h j " + "\n" +
            "H     f#" + "\n" +
            "#|  'H##" + "\n" +
            "##\\r/###" + "\n" +
            "###O####" + "\n" +
            "########",

            "        " + "\n" +
            "        " + "\n" +
            "       #" + "\n" +
            "   #  j#" + "\n" +
            "H     s " + "\n" +
            " |   H  " + "\n" +
            "  ) ?   " + "\n" +
            "H     |#" + "\n" +
            "#r  rH##" + "\n" +
            "##@(h###" + "\n" +
            "###O####" + "\n" +
            "########",

            "        " + "\n" +
            "        " + "\n" +
            "       #" + "\n" +
            "   #   #" + "\n" +
            "H     [ " + "\n" +
            " r   H  " + "\n" +
            "  @+j   " + "\n" +
            "H     ?#" + "\n" +
            "#\\   H##" + "\n" +
            "##r,?###" + "\n" +
            "###O####" + "\n" +
            "########",

            "        " + "\n" +
            "        " + "\n" +
            "       #" + "\n" +
            "   #   #" + "\n" +
            "H     ? " + "\n" +
            " )   H  " + "\n" +
            "  r_(   " + "\n" +
            "H  f  |#" + "\n" +
            "#\\ f'H##" + "\n" +
            "##\\+j###" + "\n" +
            "###O####" + "\n" +
            "########",

            "        " + "\n" +
            "        " + "\n" +
            "       #" + "\n" +
            "   #   #" + "\n" +
            "H     [ " + "\n" +
            " )   H  " + "\n" +
            "  )r(   " + "\n" +
            "H  f  ?#" + "\n" +
            "#\\ frH##" + "\n" +
            "##\\sh###" + "\n" +
            "###f####" + "\n" +
            "########",

            "        " + "\n" +
            "        " + "\n" +
            "       #" + "\n" +
            "   #   #" + "\n" +
            "H     ? " + "\n" +
            " )   H  " + "\n" +
            "  ) (   " + "\n" +
            "H     |#" + "\n" +
            "#\\ r H##" + "\n" +
            "##.h?###" + "\n" +
            "###R####" + "\n" +
            "########",

            "        " + "\n" +
            "        " + "\n" +
            "       #" + "\n" +
            "   #   #" + "\n" +
            "H     [ " + "\n" +
            " )   H  " + "\n" +
            "  ) (   " + "\n" +
            "H     ?#" + "\n" +
            "#^  'H##" + "\n" +
            "##j+j###" + "\n" +
            "###O####" + "\n" +
            "########",

            "        " + "\n" +
            "        " + "\n" +
            "       #" + "\n" +
            "   #   #" + "\n" +
            "H     ? " + "\n" +
            " )   H  " + "\n" +
            "  ) (   " + "\n" +
            "H     |#" + "\n" +
            "#|  rH##" + "\n" +
            "##\\jh###" + "\n" +
            "###f####" + "\n" +
            "########",

            "        " + "\n" +
            "        " + "\n" +
            "       #" + "\n" +
            "   #   #" + "\n" +
            "H     [ " + "\n" +
            " )   H  " + "\n" +
            "  ) (   " + "\n" +
            "H     ?#" + "\n" +
            "#r   H##" + "\n" +
            "##@(?###" + "\n" +
            "###R####" + "\n" +
            "########",

            "        " + "\n" +
            "        " + "\n" +
            "       #" + "\n" +
            "   #   #" + "\n" +
            "H     ? " + "\n" +
            " )   H  " + "\n" +
            "  ) (   " + "\n" +
            "H     |#" + "\n" +
            "#\\   H##" + "\n" +
            "##r,j###" + "\n" +
            "###O####" + "\n" +
            "########",

            "        " + "\n" +
            "        " + "\n" +
            "       #" + "\n" +
            "   #   #" + "\n" +
            "H     [ " + "\n" +
            " )   H  " + "\n" +
            "  ) (   " + "\n" +
            "H     ?#" + "\n" +
            "#\\  'H##" + "\n" +
            "##\\r/###" + "\n" +
            "###f####" + "\n" +
            "########",

            "        " + "\n" +
            "        " + "\n" +
            "       #" + "\n" +
            "   #   #" + "\n" +
            "H     ? " + "\n" +
            " )   H  " + "\n" +
            "  ) (   " + "\n" +
            "H     |#" + "\n" +
            "#\\  rH##" + "\n" +
            "##\\(h###" + "\n" +
            "###R####" + "\n" +
            "########"
        );
    }

    @Test
    public void Cresting_rabbits_are_blocked()
    {
        assertWorldEvolvesLike(
            "r r" + "\n" +
            " (*" + "\n" +
            "(  " + "\n" +
            ":*=)k" + "\n" +
            "r r" + "\n" +
            " /*" + "\n" +
            "/##" + "\n" +
            ":*=\\k",

            "   " + "\n" +
            " $H" + "\n" +
            "r  " + "\n" +
            "   " + "\n" +
            " $H" + "\n" +
            "r##",

            "   " + "\n" +
            " ?H" + "\n" +
            "(  " + "\n" +
            "   " + "\n" +
            " ?H" + "\n" +
            "/##",

            "   " + "\n" +
            " jH" + "\n" +
            "%  " + "\n" +
            "   " + "\n" +
            " jH" + "\n" +
            "%##",

            "   " + "\n" +
            " (H" + "\n" +
            "j  " + "\n" +
            "   " + "\n" +
            " /H" + "\n" +
            "j##"
        );
    }

    @Test
    public void Valleying_rabbits_are_blocked()
    {
        assertWorldEvolvesLike(
            "r  " + "\n" +
            "   " + "\n" +
            ") r" + "\n" +
            " )*" + "\n" +
            ":*=(k" + "\n" +
            "r  " + "\n" +
            "\\ r" + "\n" +
            "#\\*" + "\n" +
            ":*=/k",

            "   " + "\n" +
            "   " + "\n" +
            "r  " + "\n" +
            " @H" + "\n" +
            "   " + "\n" +
            "r  " + "\n" +
            "#@H",

            "   " + "\n" +
            "   " + "\n" +
            ")  " + "\n" +
            " ]H" + "\n" +
            "   " + "\n" +
            "\\  " + "\n" +
            "#]H",

            "   " + "\n" +
            "   " + "\n" +
            "^  " + "\n" +
            " jH" + "\n" +
            "   " + "\n" +
            "^  " + "\n" +
            "#jH",

            "   " + "\n" +
            "   " + "\n" +
            "j  " + "\n" +
            " )H" + "\n" +
            "   " + "\n" +
            "j  " + "\n" +
            "#\\H",

            "   " + "\n" +
            "   " + "\n" +
            ")  " + "\n" +
            " )H" + "\n" +
            "   " + "\n" +
            "\\  " + "\n" +
            "#\\H"
        );

    }

    @Test
    public void Basher_on_slope_transitions_to_blocker_on_slope()
    {
        World world = createWorld(
            "  #",
            "r* ",
            "#  ",
            ":*=(bkd"
        );

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "  #",
                "r~ ",
                "#  "
            )
        );

        world.step();
        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "  K",
                " d ",
                "#  "
            )
        );

        world.step();
        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   ",
                " H ",
                "#  "
            )
        );

        world.step();
        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   ",
                " D ",
                "#  "
            )
        );

        world.step();
        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   ",
                " r ",
                "#f "
            )
        );

        world.step();
        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   ",
                "   ",
                "#  "
            )
        );
    }

}

package rabbitescape.engine.util;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static rabbitescape.engine.Tools.*;

import org.junit.Test;

public class TestWorldAssertions
{
    @Test
    public void Mirrored_world_has_each_line_reflected()
    {
        assertThat(
            WorldAssertions.mirror(
                  "rj  \n"
                + "/\\  \n"
                + "()  \n"
                + "<>|?\n"
                + "[]  \n"
                + "$^  \n"
                + "'!  \n"
                + "-=  \n"
                + "@%  \n"
                + "_+  \n"
                + ",.  \n"
                + "&m  \n"
                + "es  \n"
                + "ha  \n"
                + "KW  \n"
                + "IJ  \n"
                + "BE  \n"
                + "{}  \n"
                + "~`  "
            ),
            equalTo(
                  "  rj\n"
                + "  /\\\n"
                + "  ()\n"
                + "|?<>\n"
                + "  []\n"
                + "  $^\n"
                + "  '!\n"
                + "  -=\n"
                + "  @%\n"
                + "  _+\n"
                + "  ,.\n"
                + "  &m\n"
                + "  es\n"
                + "  ha\n"
                + "  KW\n"
                + "  IJ\n"
                + "  BE\n"
                + "  {}\n"
                + "  ~`"
            )
        );
    }

    @Test
    public void Mirrored_states_are_all_reflected()
    {
        assertThat(
            WorldAssertions.mirror(
                  "rrrr\n"
                + "jjjj\n"
                + "ffff",

                  "012\n"
                + "345\n",

                  "1  9"
            ),
            equalTo(
                  "jjjj\n"
                + "rrrr\n"
                + "ffff",

                  "210\n"
                + "543\n",

                  "9  1"
            )
        );
    }

    @Test
    public void Colon_star_lines_are_preserved()
    {
        assertThat(
            WorldAssertions.mirror(
                  "*   \n"
                + "    \n"
                + ":*=bi"
            ),
            equalTo(
                  "   *\n"
                + "    \n"
                + ":*=bi"
            )
        );
    }

    @Test
    public void Colon_star_lines_are_mirrored()
    {
        assertThat(
            WorldAssertions.mirror(
                  "*   \n"
                + "    \n"
                + ":*=br"
            ),
            equalTo(
                  "   *\n"
                + "    \n"
                + ":*=bj"
            )
        );
    }
}

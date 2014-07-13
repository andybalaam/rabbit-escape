package rabbitescape.engine;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.IsNot.*;
import static rabbitescape.engine.Tools.*;

import org.junit.Test;

public class TestTools
{
    @Test
    public void Empty_string_lines_matcher_equals_empty()
    {
        assertThat( new String[] {}, equalTo() );
    }

    @Test
    public void Empty_string_lines_matcher_not_equals_nonempty()
    {
        assertThat( new String[] { "x" }, not( equalTo() ) );
    }

    @Test
    public void Nonempty_string_lines_matcher_not_equals_empty()
    {
        assertThat( new String[] {}, not( equalTo( "x", "y" ) ) );
    }

    @Test
    public void Complex_equal_lines()
    {
        assertThat(
            new String[] {
                "#####",
                "#   #",
                "#   #",
                "#   #",
                "#r  #",
                "#####"
            },
            equalTo(
                "#####",
                "#   #",
                "#   #",
                "#   #",
                "#r  #",
                "#####"
            )
        );
    }

    @Test
    public void Same_length_but_different()
    {
        assertThat(
            new String[] {
                "#####",
                "#   #",
                "#XXX#",
                "#   #",
                "#r  #",
                "#####"
            },
            not( equalTo(
                "#####",
                "#   #",
                "#   #",
                "#   #",
                "#r  #",
                "#####"
            ) )
        );
    }

    @Test
    public void Different_num_lines()
    {
        assertThat(
            new String[] {
                "#####",
                "#   #",
                "#   #",
                "#   #",
                "#r  #",
                "#####"
            },
            not( equalTo(
                "#####",
                "#   #",
                "#   #"
            ) )
        );
    }

    @Test
    public void Different_length_lines()
    {
        assertThat(
            new String[] {
                "#####",
                "#   #",
                "#   ##",
                "#   #",
                "#r  #",
                "#####"
            },
            not( equalTo(
                "#####",
                "#   #",
                "#   #",
                "#   #",
                "#r  #",
                "#####"
            ) )
        );
    }
}

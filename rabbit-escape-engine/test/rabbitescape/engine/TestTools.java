package rabbitescape.engine;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
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

    @Test
    public void Larger_is_greater_than_smaller()
    {
        assertThat(  0, greaterThan(  -1 ) );
        assertThat( 10, greaterThan(   1 ) );
        assertThat(  1, greaterThan(   0 ) );
        assertThat( 10, greaterThan( -50 ) );
    }

    @Test( expected = AssertionError.class )
    public void Smaller_is_not_greater_than_larger()
    {
        assertThat( -1, greaterThan( 0 ) );
    }

    @Test( expected = AssertionError.class )
    public void Equal_is_not_greater_than()
    {
        assertThat( 10, greaterThan( 10 ) );
    }


    @Test
    public void Samller_is_less_than_larger()
    {
        assertThat(  -1, lessThan(  0 ) );
        assertThat(   1, lessThan( 10 ) );
        assertThat(   0, lessThan(  1 ) );
        assertThat( -50, lessThan( 10 ) );
    }

    @Test( expected = AssertionError.class )
    public void Larger_is_not_less_than_smaller()
    {
        assertThat( 0, lessThan( -1 ) );
    }

    @Test( expected = AssertionError.class )
    public void Equal_is_not_less_than()
    {
        assertThat( 10, lessThan( 10 ) );
    }
}

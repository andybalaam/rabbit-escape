package rabbitescape.engine.util;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.Arrays;

import org.junit.Test;

import static rabbitescape.engine.util.SimpleIterator.*;
import static rabbitescape.engine.util.Util.*;

public class TestSimpleIterator
{
    @Test
    public void Empty_iterator_has_no_next()
    {
        Nextable<Integer> n = new Nextable<Integer>()
        {
            @Override
            public Integer next()
            {
                return null;
            }
        };

        assertThat( simpleIterator( n ).hasNext(), is( false ) );
    }

    @Test
    public void Counting_iterator_counts_then_stops()
    {
        Nextable<Integer> n = new Nextable<Integer>()
        {
            private int count = 0;

            @Override
            public Integer next()
            {
                ++count;

                if ( count < 4 )
                {
                    return count;
                }
                else
                {
                    return null;
                }
            }
        };

        assertThat(
            list( simpleIterator( n ) ),
            equalTo( Arrays.asList( 1, 2, 3 ) )
        );
    }
}

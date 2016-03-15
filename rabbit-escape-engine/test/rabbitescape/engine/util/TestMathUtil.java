package rabbitescape.engine.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class TestMathUtil
{
    @Test
    public void sum_a_set()
    {
        Set<Integer> set = new HashSet<>();
        set.add( -2 );
        set.add( 8 );
        set.add( 3 );
        assertThat( MathUtil.sum( set ), equalTo( 9 ) );
    }

    @Test
    public void sum_empty_list()
    {
        List<Integer> emptyList = Collections.emptyList();
        assertThat( MathUtil.sum( emptyList ), equalTo( 0 ) );
    }

    @Test
    public void min_works()
    {
        assertThat( MathUtil.min( 1, 8 ), equalTo( 1 ) );
        assertThat( MathUtil.min( 2, -4 ), equalTo( -4 ) );
        assertThat( MathUtil.min( 5, 5 ), equalTo( 5 ) );
    }

    @Test
    public void max_works()
    {
        assertThat( MathUtil.max( 1, 8 ), equalTo( 8 ) );
        assertThat( MathUtil.max( 2, -4 ), equalTo( 2 ) );
        assertThat( MathUtil.max( 5, 5 ), equalTo( 5 ) );
    }

    @Test
    public void constrain_works()
    {
        assertThat( MathUtil.constrain( 1, 0, 2 ), equalTo( 1 ) );
        assertThat( MathUtil.constrain( -1, 0, 2 ), equalTo( 0 ) );
        assertThat( MathUtil.constrain( 3, 0, 2 ), equalTo( 2 ) );
    }

    /**
     * If {@link MathUtil#constrain} is called with min larger than max the
     * output is undefined. Since this is such a low level utility it seems
     * wrong to validate that the min is not more than the max, so here we
     * confirm the current behaviour. If new behaviour is desired in the future
     * this test can be updated.
     */
    @Test
    public void constrain_with_min_and_max_wrong()
    {
        assertThat( MathUtil.constrain( 3, 4, 2 ), equalTo( 4 ) );
    }
}

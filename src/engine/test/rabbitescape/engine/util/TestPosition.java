package rabbitescape.engine.util;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.equalTo;

import org.junit.Test;


public class TestPosition
{

    @Test
    public void Compare_to_compares()
    {
        int[] x = { -1,  0,  1, -1, 0,  1, -1,  0,  1 };
        int[] y = { -1, -1, -1,  0, 0,  0,  1,  1,  1 };
        int[] e = {  1,  1,  1,  1, 0, -1, -1, -1, -1 };
        Position p1 = new Position( 0, 0 );

        for ( int i = 0; i < 9; i++ )
        {
            Position p2 = new Position( x[i], y[i] );
            assertThat( sign( p1.compareTo( p2 ) ),
                        equalTo( sign( e[i] ) ) );
        }
    }

    /**
     * @see Java API spec for Comparable
     */
    @Test
    public void Comparison_is_reversible()
    {
        int[] x = { -1,  0,  2, -3, 0,  4, -5,  0,  6 };
        int[] y = { -7, -8, -9,  0, 0,  0,  1,  2,  3 };
        Position p1 = new Position( 0, 0 );

        for ( int i = 0; i < 9; i++ )
        {
            Position p2 = new Position( x[i], y[i] );
            assertThat(   sign( p1.compareTo( p2 ) ),
               equalTo( - sign( p2.compareTo( p1 ) ) ) );
        }
    }

    /**
     * @see Java API spec for Comparable
     */
    @Test
    public void Comparison_is_transitive()
    {
        Position p1 = new Position( 10, 12 );
        Position p2 = new Position( 9, 8 );
        Position p3 = new Position( 8, 8 );
        assertThat( p1.compareTo( p2 ) > 0 , equalTo( true ) );
        assertThat( p2.compareTo( p3 ) > 0 , equalTo( true ) );
        assertThat( p1.compareTo( p3 ) > 0 , equalTo( true ) );
    }

    /**
     * @see Java API spec for Comparable
     */
    @Test
    public void Comparison_three_way_sign_correct()
    {
        Position p1 = new Position( 10, 12 );
        Position p2 = new Position( 10, 12 );
        Position p3 = new Position( 8, 8 );
        assertThat( p1.compareTo( p2 ) , equalTo( 0 ) );
        assertThat( sign( p1.compareTo( p3 ) ) , equalTo( sign( p2.compareTo( p3 ) ) ) );
    }

    /**
     * @see Java API spec for Comparable
     */
    @Test
    public void Comparison_and_equals_agree()
    {
        int[] x = { -1,  0,  2, -3, 0,  4, -5,  0,  6 };
        int[] y = { -7, -8, -9,  0, 0,  0,  1,  2,  3 };
        Position p1 = new Position( 0, 0 );

        for ( int i = 0; i < 9; i++ )
        {
            Position p2 = new Position( x[i], y[i] );
            assertThat( p1.compareTo( p2 ) == 0,
               equalTo( p1.equals( p2 ) ) );
        }
    }

    // ----------------

    private int sign( int i )
    {
        if ( i == 0 )
        {
            return 0;
        }
        return i / Math.abs( i );
    }
}

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

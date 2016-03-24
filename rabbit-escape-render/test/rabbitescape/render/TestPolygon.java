package rabbitescape.render;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

public class TestPolygon
{
    @Test
    public void Scale_and_offset_is_correct()
    {
        Polygon p = unitSquare();
        int[] x = p.getX( 2.0, 3 );
        int[] y = p.getY( 2.0, 3 );
        assertThat( x, equalTo( new int[]{ 3, 3, 5, 5 } ) );
        assertThat( y, equalTo( new int[]{ 3, 5, 5, 3 } ) );
    }

    // ------------------------

    private Polygon unitSquare()
    {
        Polygon p = new Polygon();
        p.x.add( 0 ); p.x.add( 0 ); p.x.add( 1 ); p.x.add( 1 );
        p.y.add( 0 ); p.y.add( 1 ); p.y.add( 1 ); p.y.add( 0 );
        return p;
    }
}

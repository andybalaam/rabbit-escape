package rabbitescape.render;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

public class TestPolygon
{
    @Test
    public void Scale_and_offset_is_correct()
    {
        PolygonBuilder pb = unitSquare();
        Polygon p = pb.polygon( 2.0f, 3, 3 );
        assertThat( p.x, equalTo( new int[]{ 3, 3, 5, 5 } ) );
        assertThat( p.y, equalTo( new int[]{ 3, 5, 5, 3 } ) );
    }

    // ------------------------

    private PolygonBuilder unitSquare()
    {
        PolygonBuilder p = new PolygonBuilder();
        p.add( 0, 0 );
        p.add( 0, 1 );
        p.add( 1, 1 );
        p.add( 1, 0 );
        return p;
    }
}

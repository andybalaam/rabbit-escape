package rabbitescape.render;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.util.List;

import org.junit.Test;

import rabbitescape.render.androidlike.Path;

public class TestPath
{
    @Test
    public void Scale_and_offset_are_correct()
    {
        PolygonBuilder pb = unitSquare();
        Path p = pb.path( 2.0f, new Vertex( 3.0f, 3.0f ) );
        List<Vertex> vertices = p.getVertices();
        assertThat( vertices.get( 0 ), equalTo( new Vertex( 3.0f, 3.0f ) ) );
        assertThat( vertices.get( 1 ), equalTo( new Vertex( 3.0f, 5.0f ) ) );
        assertThat( vertices.get( 2 ), equalTo( new Vertex( 5.0f, 5.0f ) ) );
        assertThat( vertices.get( 3 ), equalTo( new Vertex( 5.0f, 3.0f ) ) );
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

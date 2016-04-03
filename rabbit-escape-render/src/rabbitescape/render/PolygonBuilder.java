package rabbitescape.render;

import java.util.ArrayList;

public class PolygonBuilder
{

    private final ArrayList<Vertex> vertices;

    public PolygonBuilder()
    {
        vertices = new ArrayList<Vertex>();
    }

    public boolean isEmpty()
    {
        return vertices.size() == 0;
    }

    public void add( int x, int y )
    {
        vertices.add( new Vertex( (float)x, (float)y ) );
    }

    public void add( Vertex v )
    {
        vertices.add( v );
    }

    /**
     * return Polygon scaled by factor, f, and offset by ( oX, oY ).
     */
    public Polygon polygon( float f, int oX, int oY )
    {
        int[] x = new int[vertices.size()];
        int[] y = new int[vertices.size()];
        for ( int i = 0; i < vertices.size(); i++ )
        {
            Vertex v = vertices.get( i );
            x[i] = (int)( f * v.x + oX );
            y[i] = (int)( f * v.y + oY );
        }
        return new Polygon( x, y );
    }

}

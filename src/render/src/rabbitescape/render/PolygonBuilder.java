package rabbitescape.render;

import java.util.ArrayList;

import rabbitescape.render.androidlike.Path;

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
    public Path path( float f, Vertex offset )
    {
        Path p = new Path();
        for ( int i = 0; i < vertices.size(); i++ )
        {
            Vertex v = vertices.get( i );
            p.lineTo( f * v.x + offset.x, f * v.y + offset.y );
        }
        return p;
    }

}

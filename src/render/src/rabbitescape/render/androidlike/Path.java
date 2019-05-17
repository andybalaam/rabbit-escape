package rabbitescape.render.androidlike;

import java.util.ArrayList;
import java.util.List;

import rabbitescape.render.Vertex;

public class Path
{
    private List<Vertex> vertices;

    public Path()
    {
        vertices = new ArrayList<Vertex>();
    }

    public void moveTo( float x, float y )
    {
        vertices.add( new Vertex( x, y ) );
    }

    public void lineTo( float x, float y )
    {
        moveTo( x, y );
    }

    public List<Vertex> getVertices()
    {
        return new ArrayList<Vertex>( vertices );
    }
}

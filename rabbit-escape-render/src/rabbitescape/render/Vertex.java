package rabbitescape.render;

public final class Vertex
{
    public final float x, y;

    public Vertex( float x, float y )
    {
        this.x = x;
        this.y = y;
    }

    public Vertex( int x, int y )
    {
        this.x = (float)x;
        this.y = (float)y;
    }
}

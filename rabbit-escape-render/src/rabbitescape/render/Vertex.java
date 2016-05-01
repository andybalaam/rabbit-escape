package rabbitescape.render;

public final class Vertex
{
    public final float x, y;
    public final float delta = 0.0000001f;

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

    @Override
    public boolean equals( Object o )
    {
        if ( !( o instanceof Vertex ) )
        {
            return false;
        }
        Vertex v = (Vertex)o;
        return Math.abs( v.x - this.x ) < delta &&
            Math.abs( v.y - this.y ) < delta;
    }

    @Override
    public int hashCode()
    {
        return (int)( x * 32000f + y );
    }
}

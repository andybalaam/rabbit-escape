package rabbitescape.render;

public final class Vertex
{
    public final float x, y;
    public static final float delta = 0.0000001f;

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

    public Vertex add( Vertex operand )
    {
        return new Vertex( this.x + operand.x, this.y + operand.y );
    }

    public Vertex subtract( Vertex operand )
    {
        return new Vertex( this.x - operand.x, this.y - operand.y );
    }

    /**
     * Return the vector to the vertex rotated by 90 degrees.
     */
    public Vertex rot90()
    {
        return new Vertex( -y, x );
    }

    /**
     * @return The square of the vector to this vertex' magnitude.
     */
    public float magnitude2()
    {
        return x * x + y * y;
    }

    public float magnitude()
    {
        return (float)Math.sqrt( magnitude2() );
    }

    public Vertex multiply( float f )
    {
        return new Vertex( x * f, y * f);
    }

    @Override
    public int hashCode()
    {
        return (int)( x * 32000f + y );
    }
}

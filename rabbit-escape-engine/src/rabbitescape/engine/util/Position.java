package rabbitescape.engine.util;

public class Position implements Comparable<Position>
{
    public final int x;
    public final int y;

    public Position( int x, int y )
    {
        this.x = x;
        this.y = y;
    }

    public Position plus( Position p )
    {
        return new Position( this.x + p.x, this.y + p.y );
    }

    /**
     * Lexical comparison: y then x.
     */
    @Override
    public int compareTo( Position other )
    {
        if ( y != other.y )
        {
            return y - other.y ;
        }
        return x - other.x;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( null == o )
        {
            return false;
        }
        if ( !( o instanceof Position ) )
        {
            return false;
        }
        Position p = (Position)o;
        return this.x == p.x && this.y == p.y;
    }

    @Override
    public int hashCode()
    {
        return 32 * 1000 * y + x;
    }

}

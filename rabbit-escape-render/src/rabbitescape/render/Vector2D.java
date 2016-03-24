package rabbitescape.render;

import rabbitescape.engine.CellularDirection;

public class Vector2D
{
    private final static double radiansToDegrees = 180.0 / Math.PI;

    public static final Vector2D origin = new Vector2D( 0, 0);

    final public int x;
    final public int y;

    public Vector2D( int x, int y )
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Adds 2 Vector2D objects.
     */
    public Vector2D add( Vector2D operand )
    {
        return new Vector2D( this.x + operand.x, this.y + operand.y );
    }

    /**
     * Subtract a Vector2D.
     */
    public Vector2D subtract( Vector2D operand )
    {
        return new Vector2D( this.x - operand.x, this.y - operand.y );
    }

    /**
     * Multiply with a scalar.
     */
    public Vector2D multiply( int operand )
    {
        return new Vector2D( this.x * operand, this.y * operand );
    }

    /**
     * Multiply with a scalar.
     */
    public Vector2D multiply( double operand )
    {
        return new Vector2D( (int)( (double)this.x * operand ),
                             (int)( (double)this.y * operand ) );
    }

    /**
     * Divide by a scalar.
     */
    public Vector2D divide( int operand )
    {
        return new Vector2D( this.x / operand, this.y / operand );
    }

    public static Vector2D cellularDirection( CellularDirection d )
    {
        return new Vector2D( d.xOffset, d.yOffset );
    }

    public int angle()
    {
        return (int) ( radiansToDegrees * Math.atan2( (double)y, (double)x ) );
    }

    /**
     * @return The square of the vector's magnitude.
     */
    public int magnitude2()
    {
        return x * x + y * y;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( o instanceof Vector2D )
        {
            Vector2D v = (Vector2D)o;
            return v.y == this.y && v.x == this.x;
        }
        else
        {
            return false;
        }
    }

    @Override
    public String toString()
    {
        return String.format( "(%04d,%04d)", x, y );
    }

}

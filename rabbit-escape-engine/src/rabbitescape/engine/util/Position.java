package rabbitescape.engine.util;

public class Position
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
}

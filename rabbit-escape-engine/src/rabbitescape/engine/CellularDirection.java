package rabbitescape.engine;

import rabbitescape.engine.util.Position;

/** The directions that can be affected by a cell during cellular automation. */
public enum CellularDirection
{
    UP( 0, -1 ),
    RIGHT( 1, 0 ),
    DOWN( 0, 1 ),
    LEFT( -1, 0 ),
    HERE( 0, 0 );

    public final int xOffset;
    public final int yOffset;

    private CellularDirection( int xOffset, int yOffset )
    {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public static CellularDirection opposite( CellularDirection input )
    {
        switch( input )
        {
            case UP:    return DOWN;
            case RIGHT: return LEFT;
            case DOWN:  return UP;
            case LEFT:  return RIGHT;
            case HERE:  throw new IllegalStateException(
                "Here has no opposite" );
            default: throw new IllegalArgumentException( input.name() );
        }
    }

    public Position offset( Position position )
    {
        return new Position( position.x + xOffset, position.y + yOffset );
    }
}

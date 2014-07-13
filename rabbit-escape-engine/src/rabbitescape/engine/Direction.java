package rabbitescape.engine;


public class Direction
{
    private enum D
    {
        UP, RIGHT, DOWN, LEFT
    }

    public static final Direction UP    = new Direction( D.UP );
    public static final Direction RIGHT = new Direction( D.RIGHT );
    public static final Direction DOWN  = new Direction( D.DOWN );
    public static final Direction LEFT  = new Direction( D.LEFT );

    private final D dir;

    public Direction( D dir )
    {
        this.dir = dir;
    }

    public static Direction opposite( Direction input )
    {
        switch( input.dir )
        {
            case UP:    return DOWN;
            case RIGHT: return LEFT;
            case DOWN:  return UP;
            case LEFT:  return RIGHT;
            default: throw new IllegalArgumentException( input.dir.name() );
        }
    }
}

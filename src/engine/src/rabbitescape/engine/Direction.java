package rabbitescape.engine;

public enum Direction {
    UP,
    RIGHT,
    DOWN,
    LEFT;

    public static Direction opposite( Direction input )
    {
        switch( input )
        {
            case UP:    return DOWN;
            case RIGHT: return LEFT;
            case DOWN:  return UP;
            case LEFT:  return RIGHT;
            default: throw new IllegalArgumentException( input.name() );
        }
    }
}

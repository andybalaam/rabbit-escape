package rabbitescape.engine;

public class Block
{
    public enum Type
    {
        solid,
        bridge,
    }

    public final int x;
    public final int y;
    public final Direction riseDir;  // DOWN for a flat block,
                                     // RIGHT for sloping /
                                     // LEFT  for sloping \

    public final Type type;

    public Block( int x, int y, Direction riseDir )
    {
        this( x, y, riseDir, Type.solid );
    }

    public Block( int x, int y, Direction riseDir, Type type )
    {
        this.x = x;
        this.y = y;
        this.riseDir = riseDir;
        this.type = type;
    }
}

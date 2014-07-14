package rabbitescape.engine;

public class Block
{
    public final int x;
    public final int y;
    public final Direction riseDir;  // DOWN for a flat block,
                                     // RIGHT for sloping /
                                     // LEFT  for sloping \

    public Block( int x, int y, Direction riseDir )
    {
        this.x = x;
        this.y = y;
        this.riseDir = riseDir;
    }
}

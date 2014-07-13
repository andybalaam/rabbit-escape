package rabbitescape.engine;

public abstract class Thing
{
    public boolean alive;
    public int x;
    public int y;

    public Thing( int x, int y )
    {
        alive = true;
        this.x = x;
        this.y = y;
    }

    public abstract void step( World world );
    public abstract void describeChanges( World world, ChangeDescription ret );
}

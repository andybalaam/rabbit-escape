package rabbitescape;

public abstract class Thing
{
    public int x;
    public int y;

    public Thing( int x, int y )
    {
        this.x = x;
        this.y = y;
    }

    public abstract void step( World world );
    public abstract void describeChanges( World world, ChangeDescription ret );
}

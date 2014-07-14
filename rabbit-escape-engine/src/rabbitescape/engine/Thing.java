package rabbitescape.engine;

import rabbitescape.engine.ChangeDescription.State;

public abstract class Thing
{
    public boolean alive;
    public State state;
    public int x;
    public int y;

    public Thing( int x, int y )
    {
        alive = true;
        this.x = x;
        this.y = y;
    }

    public abstract void init( World world );
    public abstract void step( World world );
}

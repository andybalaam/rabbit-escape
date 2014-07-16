package rabbitescape.engine;

import static rabbitescape.engine.ChangeDescription.State.*;

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
        state = NOT_CHANGING;
        this.x = x;
        this.y = y;
    }

    public abstract void init( World world );
    public abstract void step( World world );
}

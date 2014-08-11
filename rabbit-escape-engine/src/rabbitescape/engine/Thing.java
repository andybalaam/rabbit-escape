package rabbitescape.engine;

import rabbitescape.engine.ChangeDescription.State;

public abstract class Thing
{
    public State state;
    public int x;
    public int y;

    public Thing( int x, int y, State state )
    {
        this.state = state;
        this.x = x;
        this.y = y;
    }

    public abstract void calcNewState( World world );
    public abstract void step( World world );
}

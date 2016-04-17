package rabbitescape.engine;

import java.util.Map;

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
    public abstract Map<String, String> saveState();
    public abstract void restoreFromState( Map<String, String> state );

    @Override
    public abstract String toString();
}

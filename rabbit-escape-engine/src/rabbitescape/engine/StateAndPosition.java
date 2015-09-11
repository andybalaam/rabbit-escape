package rabbitescape.engine;

public class StateAndPosition
{
    public final ChangeDescription.State state;
    public final int x;
    public final int y;

    public StateAndPosition( ChangeDescription.State state, int x, int y )
    {
        this.state = state;
        this.x = x;
        this.y = y;
    }
}

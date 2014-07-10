package rabbitescape;

import java.util.ArrayList;
import java.util.List;

public class ChangeDescription
{
    public enum State
    {
        RABBIT_WALKING_LEFT,
        RABBIT_WALKING_RIGHT
    }

    public static class Change
    {
        public final int x;
        public final int y;
        public final State state;

        public Change( int x, int y, State state )
        {
            this.x = x;
            this.y = y;
            this.state = state;
        }
    }

    public List<Change> changes = new ArrayList<Change>();

    public void add( int x, int y, State state )
    {
        changes.add( new Change( x, y, state ) );
    }
}

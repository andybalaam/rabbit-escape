package rabbitescape.engine;

import java.util.HashMap;
import java.util.Map;

import rabbitescape.engine.util.LookupItem2D;
import rabbitescape.engine.util.Position;

public class WaterRegion implements LookupItem2D
{
    public final int x;
    public final int y;
    /** The amount of water stored here. */
    public int contents;
    /** The water being transferred from here this tick. */
    private Map<Direction, Integer> flow = new HashMap<>();

    public WaterRegion( int x, int y )
    {
        this( x, y, 0 );
    }

    public WaterRegion( int x, int y, int contents )
    {
        this.x = x;
        this.y = y;
        this.contents = contents;
    }

    @Override
    public Position getPosition()
    {
        return new Position( x, y );
    }

    /**
     * Get the amount of water being transferred from here in a given direction
     * this tick.
     * 
     * @param direction
     *            The direction of interest.
     * @return The amount of water.
     */
    public int getFlow( Direction direction )
    {
        if ( flow.containsKey( direction ) )
        {
            return flow.get( direction );
        }
        return 0;
    }
}

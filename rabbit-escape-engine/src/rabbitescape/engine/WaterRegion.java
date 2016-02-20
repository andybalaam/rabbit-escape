package rabbitescape.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rabbitescape.engine.util.LookupItem2D;
import rabbitescape.engine.util.Position;

public class WaterRegion implements LookupItem2D
{
    public final int x;
    public final int y;
    /**
     * The list of directions that this region is connected in. Note that this
     * does not mean that water can necessarily flow that way, because the cell
     * in that direction may have no water region, or it may have a region that
     * is not connected to here (e.g. two adjacent left ramps).
     */
    private final List<Direction> connections;
    /** The amount of water that can stay here without being under pressure. */
    public int capacity;
    /** The amount of water stored here. */
    public int contents;
    /** The water being transferred from here this tick. */
    private Map<Direction, Integer> flow = new HashMap<>();

    public WaterRegion( int x, int y, List<Direction> connections, int capacity )
    {
        this( x, y, connections, capacity, 0 );
    }

    public WaterRegion( int x, int y, List<Direction> connections, int capacity, int contents )
    {
        this.x = x;
        this.y = y;
        this.connections = connections;
        this.capacity = capacity;
        this.contents = contents;
    }

    @Override
    public Position getPosition()
    {
        return new Position( x, y );
    }

    /**
     * Is this region connected in the specified direction? Note that this does
     * not mean that water can necessarily flow that way, because the cell in
     * that direction may have no water region, or it may have a region that is
     * not connected to here (e.g. two adjacent left ramps).
     */
    public boolean isConnected( Direction direction )
    {
        return connections.contains( direction );
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

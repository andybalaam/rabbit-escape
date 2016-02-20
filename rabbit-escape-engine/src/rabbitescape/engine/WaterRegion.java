package rabbitescape.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
    private final Set<Direction> connections;
    /** The amount of water that can stay here without being under pressure. */
    public int capacity;
    /** The amount of water stored here. */
    public int contents;
    /** The water being transferred from here this tick. */
    private Map<Direction, Integer> flow = new HashMap<>();

    public WaterRegion( int x, int y, Set<Direction> connections, int capacity )
    {
        this( x, y, connections, capacity, 0 );
    }

    public WaterRegion( int x, int y, Set<Direction> connections, int capacity, int contents )
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
    
    @Override
    public int hashCode()
    {
        int hash = x;
        hash = 31 * hash + y;
        hash = 31 * hash + connections.hashCode();
        hash = 31 * hash + capacity;
        hash = 31 * hash + contents;
        hash = 31 * hash + flow.hashCode();
        return hash;
    }
    
    @Override
    public boolean equals( Object obj )
    {
        if ( !( obj instanceof WaterRegion ) )
        {
            return false;
        }
        if ( obj == this )
        {
            return true;
        }
        WaterRegion other = ( WaterRegion ) obj;
        return x == other.x
            && y == other.y
            && connections.equals( other.connections )
            && capacity == other.capacity
            && contents == other.contents
            && flow.equals( other.flow );
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append( "WaterRegion: " )
            .append( x ).append( ", " )
            .append( y ).append( ", " )
            .append( connections ).append( ", " )
            .append( capacity ).append( ", " )
            .append( contents ).append( ", " )
            .append( flow );
        return sb.toString();
    }
}

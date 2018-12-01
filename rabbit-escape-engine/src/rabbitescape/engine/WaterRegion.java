package rabbitescape.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import rabbitescape.engine.ChangeDescription.State;
import rabbitescape.engine.util.LookupItem2D;
import rabbitescape.engine.util.Position;
import rabbitescape.engine.util.Util;
import rabbitescape.engine.util.WaterUtil;

public class WaterRegion extends Thing implements LookupItem2D
{
    /**
     * The list of directions that this region is connected in. Note that this
     * does not mean that water can necessarily flow that way, because the cell
     * in that direction may have no water region, or it may have a region that
     * is not connected to here (e.g. two adjacent left ramps).
     */
    private Set<CellularDirection> connections;
    /** The amount of water that can stay here without being under pressure. */
    public int capacity;
    /** The amount of water stored here. */
    private int contents;
    /** The water being transferred from here this tick. */
    private Map<CellularDirection, Integer> flow = new HashMap<>();
    /** Does this region need updating? */
    public final boolean outsideWorld;

    public WaterRegion( 
        int x, 
        int y, 
        Set<CellularDirection> connections,
        int capacity 
    )
    {
        this( x, y, connections, capacity, false );
    }

    public WaterRegion( 
        int x, 
        int y, 
        Set<CellularDirection> connections,
        int capacity, 
        boolean outsideWorld 
    )
    {
        this( x, y, connections, capacity, 0, outsideWorld );
    }

    public WaterRegion( 
        int x, 
        int y, 
        Set<CellularDirection> connections,
        int capacity, 
        int contents, 
        boolean outsideWorld 
    )
    {
        super( x, y, State.WATER_REGION_EMPTY );
        this.connections = connections;
        this.capacity = capacity;
        // This also sets the water region state.
        setContents(contents);
        this.outsideWorld = outsideWorld;
    }

    @Override
    public Position getPosition()
    {
        return new Position( x, y );
    }

    public int getContents()
    {
        return contents;
    }

    public void setContents( int contents )
    {
        this.contents = contents;
        if ( contents == 0 )
        {
            state = State.WATER_REGION_EMPTY;
        }
        else if ( contents < WaterUtil.MAX_CAPACITY )
        {
            state = State.WATER_REGION_HALF;
        }
        else
        {
            state = State.WATER_REGION;
        }
    }

    public void addContents( int delta )
    {
        setContents( contents + delta );
    }

    /**
     * Is this region connected in the specified direction? Note that this does
     * not mean that water can necessarily flow that way, because the cell in
     * that direction may have no water region, or it may have a region that is
     * not connected to here (e.g. two adjacent left ramps).
     */
    public boolean isConnected( CellularDirection direction )
    {
        return connections.contains( direction );
    }

    public Iterator<CellularDirection> getConnectionsIterator()
    {
        return connections.iterator();
    }

    /**
     * Get the amount of water being transferred from here in a given direction
     * this tick. Note that it is always positive. For example if water is
     * flowing into a cell from the left, its LEFT will give zero.
     * However the cell to its LEFT will yield a positive number on its
     * RIGHT.
     *
     * @param direction
     *            The direction of interest.
     * @return The amount of water.
     */
    public int getFlow( CellularDirection direction )
    {
        if ( flow.containsKey( direction ) )
        {
            return flow.get( direction );
        }
        return 0;
    }

    @Override
    public void calcNewState( World world )
    {
        if ( outsideWorld || contents <= 0 )
        {
            return;
        }
        Map<CellularDirection, WaterRegion> neighbourhood = WaterUtil
            .findNeighbourhood( this, world.waterTable );
        flow = WaterUtil.calculateFlow( neighbourhood );
    }

    @Override
    public void step( World world )
    {
        if ( flow.size() > 0 )
        {
            Map<CellularDirection, WaterRegion> neighbourhood = WaterUtil
                .findNeighbourhood( this, world.waterTable );
            for ( Entry<CellularDirection, Integer> entry : flow.entrySet() )
            {
                CellularDirection direction = entry.getKey();
                if ( entry.getValue() > 0 )
                {
                    if ( neighbourhood.keySet().contains( direction ) )
                    {
                        WaterRegion neighbour = neighbourhood.get( direction );
                        if ( !neighbour.outsideWorld )
                        {
                            neighbour.addContents( entry.getValue() );
                        }
                        setContents( contents - entry.getValue() );
                    }
                    else
                    {
                        System.out.println(
                            "Something went wrong when " +
                             "calculating water moving " +
                             direction + " from " + this );
                    }
                }
            }
            flow = new HashMap<>();
        }
    }

    @Override
    public Map<String, String> saveState( boolean runtimeMeta )
    {
        Map<String, String> ret = new HashMap<String, String>();
        ret.put( "WaterRegion.connections", Util.join( ",", connections ) );
        BehaviourState.addToStateIfNotDefault( 
            ret, 
            "WaterRegion.capacity",
            String.valueOf( capacity ),
            "0" 
        );
        BehaviourState.addToStateIfNotDefault( 
            ret, 
            "WaterRegion.contents",
            String.valueOf( contents ),
            "0" );
        List<String> flowBits = new ArrayList<>();
        for ( Entry<CellularDirection, Integer> entry : flow.entrySet() )
        {
            flowBits.add( entry.getKey().toString() );
            flowBits.add( entry.getValue().toString() );
        }
        ret.put( "WaterRegion.flow", Util.join( ",", flowBits ) );
        return ret;
    }

    @Override
    public void restoreFromState( Map<String, String> state )
    {
        connections = new HashSet<>();
        for ( String connection :
            Util.split( state.get( "WaterRegion.connections" ), "," ) )
        {
            connections.add( CellularDirection.valueOf( connection ) );
        }
        capacity = BehaviourState.restoreFromState(
            state, "WaterRegion.capacity", 0 );
        contents = BehaviourState.restoreFromState(
            state, "WaterRegion.contents", 0 );
        flow = new HashMap<>();
        String[] flowBits = Util.split( state.get( "WaterRegion.flow" ), "," );
        for ( int i : Util.range( flowBits.length / 2 ))
        {
            CellularDirection direction =
                CellularDirection.valueOf( flowBits[i * 2] );
            Integer amount = Integer.valueOf( flowBits[i * 2 + 1] );
            flow.put( direction, amount );
        }
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

    @Override
    public String overlayText()
    {
        return 0 == contents ? "" : "~" + contents;
    }
}

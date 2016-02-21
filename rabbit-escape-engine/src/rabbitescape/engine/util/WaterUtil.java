package rabbitescape.engine.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import rabbitescape.engine.Direction;
import rabbitescape.engine.WaterRegion;

public class WaterUtil
{
    /** The maximum capacity of water that can be held in a quarter empty tile without it overflowing. */
    public static final int QUARTER_CAPACITY = 25;
    /** The maximum capacity of water that can be held in a half empty tile without it overflowing. */
    public static final int HALF_CAPACITY = QUARTER_CAPACITY * 2;
    /** The maximum capacity of water that can be held in an empty tile without it overflowing. */
    public static final int MAX_CAPACITY = HALF_CAPACITY * 2;
    /** The default rate at which pipes produce water. */
    public static final int SOURCE_RATE = 50;

    /** Find all WaterRegions connected to the current region. */
    public static Map<Direction, WaterRegion> findNeighbourhood(
        WaterRegion region,
        LookupTable2D<WaterRegion> waterTable )
    {
        Position position = region.getPosition();
        Map<Direction, WaterRegion> neighbourhood = new HashMap<>();
        neighbourhood.put( Direction.HERE, region );
        Iterator<Direction> connectionsIterator = region
            .getConnectionsIterator();
        while ( connectionsIterator.hasNext() )
        {
            Direction connection = connectionsIterator.next();
            Position otherPosition = connection.offset( position );
            for ( WaterRegion otherRegion : waterTable
                .getItemsAt( otherPosition.x, otherPosition.y ) )
            {
                if ( otherRegion
                    .isConnected( Direction.opposite( connection ) ) )
                {
                    if ( neighbourhood.containsKey( connection ) )
                    {
                        throw new IllegalStateException( "There are two water regions connected on the same side of " + region );
                    }
                    neighbourhood.put( connection, otherRegion );
                }
            }
        }
        return neighbourhood;
    }
}

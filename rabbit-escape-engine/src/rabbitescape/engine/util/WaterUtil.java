package rabbitescape.engine.util;

import static rabbitescape.engine.CellularDirection.DOWN;
import static rabbitescape.engine.CellularDirection.HERE;
import static rabbitescape.engine.CellularDirection.LEFT;
import static rabbitescape.engine.CellularDirection.RIGHT;
import static rabbitescape.engine.CellularDirection.UP;
import static rabbitescape.engine.util.MathUtil.constrain;
import static rabbitescape.engine.util.MathUtil.max;
import static rabbitescape.engine.util.MathUtil.min;
import static rabbitescape.engine.util.MathUtil.sum;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import rabbitescape.engine.CellularDirection;
import rabbitescape.engine.WaterRegion;

public class WaterUtil
{
    /** The maximum capacity of water that can be held in a quarter empty tile without it overflowing. */
    public static final int QUARTER_CAPACITY = 256;
    /** The maximum capacity of water that can be held in a half empty tile without it overflowing. */
    public static final int HALF_CAPACITY = QUARTER_CAPACITY * 2;
    /** The maximum capacity of water that can be held in an empty tile without it overflowing. */
    public static final int MAX_CAPACITY = HALF_CAPACITY * 2;
    /** The default rate at which pipes produce water. */
    public static final int SOURCE_RATE = 512;
    /** A cell can hold 1/COMPRESSION_FACTOR extra if the cell above is full (assuming both have same capacity). */
    private static final int COMPRESSION_FACTOR = 4;
    /** A magic constant for encouraging water to flow upwards. */
    private static final int MAGIC_UP_NUMERATOR = 11;
    private static final int MAGIC_UP_DENOMINATOR = 20;
    /** A fake water region, for referencing in situations where there is no water region in a given direction. */
    private static final WaterRegion FAKE_REGION = new WaterRegion(0, 0, null, 0);

    /** Find all WaterRegions connected to the current region. */
    public static Map<CellularDirection, WaterRegion> findNeighbourhood(
        WaterRegion region,
        LookupTable2D<WaterRegion> waterTable )
    {
        Position position = region.getPosition();
        Map<CellularDirection, WaterRegion> neighbourhood = new HashMap<>();
        neighbourhood.put( CellularDirection.HERE, region );
        Iterator<CellularDirection> connectionsIterator = region
            .getConnectionsIterator();
        while ( connectionsIterator.hasNext() )
        {
            CellularDirection connection = connectionsIterator.next();
            Position otherPosition = connection.offset( position );
            for ( WaterRegion otherRegion : waterTable
                .getItemsAt( otherPosition.x, otherPosition.y ) )
            {
                if ( otherRegion
                    .isConnected( CellularDirection.opposite( connection ) ) )
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

    /** Update the flow given some contents to split between some directions in ratio with the capacities. */
    private static int updateFlow(
        Map<CellularDirection, Integer> flow,
        Map<CellularDirection, Integer> relevantContents,
        Map<CellularDirection, Integer> relevantCapacity )
    {
        int totalCapacity = sum( relevantCapacity.values() );
        int totalContents = sum( relevantContents.values() );
        Map<CellularDirection, Integer> targetFlow = new HashMap<>();
        Set<CellularDirection> connectedOrdinals = new HashSet<>();
        for ( CellularDirection direction : relevantCapacity.keySet() )
        {
            int target = relevantCapacity.get( direction ) * totalContents / totalCapacity;
            targetFlow.put( direction, max( target - relevantContents.get( direction ), 0 ) );
            if ( direction != HERE )
            {
                connectedOrdinals.add( direction );
            }
        }
        int totalTargetOutFlow = 0;
        for ( CellularDirection direction : connectedOrdinals )
        {
            totalTargetOutFlow += targetFlow.get( direction );
        }
        if ( totalTargetOutFlow <= 0 )
        {
            return 0;
        }
        int actualOutFlow = min( totalTargetOutFlow, relevantContents.get( HERE ) );
        int totalFlowed = 0;
        for ( CellularDirection ordinal : connectedOrdinals )
        {
            int amount = ( targetFlow.get( ordinal ) * actualOutFlow ) / totalTargetOutFlow;
            flow.put( ordinal, flow.get( ordinal ) + amount );
            totalFlowed += amount;
        }
        return totalFlowed;
    }

    private static int updateFlowDown(
        Map<CellularDirection, Integer> flow,
        int contentsHere,
        Map<CellularDirection, WaterRegion> neighbourhood )
    {
        WaterRegion down = neighbourhood.get( DOWN );
        int flowDown = constrain( down.capacity - down.getContents(), 0, contentsHere );
        flow.put( DOWN, flow.get( DOWN ) + flowDown );
        return contentsHere - flowDown;
    }

    /** Update flow across and a bit down to simulate pressure at this level. */
    private static int updateFlowAcross( Map<CellularDirection, Integer> flow,
        int contentsHere,
        Map<CellularDirection, WaterRegion> neighbourhood )
    {
        Map<CellularDirection, Integer> relevantCapacity = new HashMap<>();
        relevantCapacity.put( LEFT, neighbourhood.get( LEFT ).capacity );
        relevantCapacity.put( HERE, neighbourhood.get( HERE ).capacity );
        relevantCapacity.put( RIGHT, neighbourhood.get( RIGHT ).capacity );
        relevantCapacity.put( DOWN, neighbourhood.get( DOWN ).capacity / COMPRESSION_FACTOR );

        Map<CellularDirection, Integer> relevantContents = new HashMap<>();
        relevantContents.put(LEFT, constrain(neighbourhood.get( LEFT ).getContents(), 0, neighbourhood.get( LEFT ).capacity));
        relevantContents.put(HERE, constrain(contentsHere, 0, neighbourhood.get( HERE ).capacity));
        relevantContents.put(RIGHT, constrain(neighbourhood.get( RIGHT ).getContents(), 0, neighbourhood.get( RIGHT ).capacity));
        relevantContents.put(DOWN, max(neighbourhood.get( DOWN ).getContents() - neighbourhood.get( DOWN ).capacity, 0));

        int totalFlowed = updateFlow(flow, relevantContents, relevantCapacity);
        return contentsHere - totalFlowed;
    }

    /** Create a 'flow' to the current cell. Any remaining can be pushed upwards. */
    private static int updateFlowHere( Map<CellularDirection, Integer> flow,
        int contentsHere,
        Map<CellularDirection, WaterRegion> neighbourhood )
    {
        int constrained = constrain( contentsHere, 0, neighbourhood.get( HERE ).capacity );
        // The water will not actually leave the cell, so no need to add an explicit flow
        return contentsHere - constrained;
    }

    /** Update flow up and a bit across and down to simulate pressure at the level above. */
    private static int updateFlowUp( Map<CellularDirection, Integer> flow,
        int contentsHere,
        Map<CellularDirection, WaterRegion> neighbourhood )
    {
        Map<CellularDirection, Integer> relevantCapacity = new HashMap<>();
        relevantCapacity.put( UP, ( neighbourhood.get( UP ).capacity * MAGIC_UP_NUMERATOR ) / MAGIC_UP_DENOMINATOR );
        relevantCapacity.put( LEFT, neighbourhood.get( LEFT ).capacity / COMPRESSION_FACTOR );
        relevantCapacity.put( HERE, neighbourhood.get( HERE ).capacity / COMPRESSION_FACTOR );
        relevantCapacity.put( RIGHT, neighbourhood.get( RIGHT ).capacity / COMPRESSION_FACTOR );
        relevantCapacity.put( DOWN, ( neighbourhood.get( DOWN ).capacity * ( COMPRESSION_FACTOR + 1 ) / ( COMPRESSION_FACTOR * COMPRESSION_FACTOR ) ) );

        Map<CellularDirection, Integer> relevantContents = new HashMap<>();
        relevantContents.put( UP, neighbourhood.get( UP ).getContents() );
        relevantContents.put( LEFT, max( neighbourhood.get( LEFT ).getContents() - neighbourhood.get( LEFT ).capacity, 0 ) );
        relevantContents.put( HERE, contentsHere );
        relevantContents.put( RIGHT, max( neighbourhood.get( RIGHT ).getContents() - neighbourhood.get( RIGHT ).capacity, 0 ) );
        relevantContents.put( DOWN, max(neighbourhood.get( DOWN ).getContents() - ( neighbourhood.get( DOWN ).capacity * ( COMPRESSION_FACTOR + 1 ) ) / COMPRESSION_FACTOR, 0) );

        int totalFlowed = updateFlow(flow, relevantContents, relevantCapacity);
        return contentsHere - totalFlowed;
    }

    public static Map<CellularDirection, Integer> calculateFlow(
        Map<CellularDirection, WaterRegion> neighbourhood )
    {
        Map<CellularDirection, Integer> flow = new HashMap<>();
        for ( CellularDirection direction : CellularDirection.values() )
        {
            flow.put( direction, 0 );
            if ( !neighbourhood.keySet().contains( direction ) )
            {
                neighbourhood.put( direction, FAKE_REGION );
            }
        }
        int contentsHere = neighbourhood.get( HERE ).getContents();

        contentsHere = updateFlowDown( flow, contentsHere, neighbourhood );
        if ( contentsHere > 0 )
        {
            contentsHere = updateFlowAcross( flow, contentsHere, neighbourhood );
            if ( contentsHere > 0 )
            {
                contentsHere = updateFlowHere( flow, contentsHere, neighbourhood );
                if ( contentsHere > 0 )
                {
                    contentsHere = updateFlowUp( flow, contentsHere, neighbourhood );
                }
            }
        }

        return flow;
    }
}

package rabbitescape.engine;

import static rabbitescape.engine.CellularDirection.DOWN;
import static rabbitescape.engine.CellularDirection.LEFT;
import static rabbitescape.engine.CellularDirection.RIGHT;
import static rabbitescape.engine.CellularDirection.UP;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rabbitescape.engine.Block.Shape;
import rabbitescape.engine.util.LookupTable2D;
import rabbitescape.engine.util.Position;
import rabbitescape.engine.util.WaterUtil;

public class WaterRegionFactory
{
    /**
     * Generate a 2D lookup table of water regions based on a 2D table of
     * blocks.
     *
     * @param blockTable
     *            The table of blocks.
     * @param waterAmounts
     *            Any water region contents that are currently known.
     */
    public static LookupTable2D<WaterRegion> generateWaterTable(
        LookupTable2D<Block> blockTable, 
        Map<Position, 
        Integer> waterAmounts 
    )
    {
        LookupTable2D<WaterRegion> waterTable =
            new LookupTable2D<>( blockTable.size );
        for ( int x = -1; x <= blockTable.size.width; x++ )
        {
            for ( int y = -1; y <= blockTable.size.height; y++ )
            {
                Integer waterAmount = waterAmounts.get( new Position( x, y ) );
                if ( waterAmount == null )
                {
                    waterAmount = 0;
                }
                createWaterRegionsAtPoint( 
                    blockTable, 
                    waterTable,
                    x, 
                    y, 
                    waterAmount 
                );
            }
        }
        return waterTable;
    }

    /** Create water regions at a given point based on a 2D table of blocks. */
    public static void createWaterRegionsAtPoint(
        LookupTable2D<Block> blockTable,
        LookupTable2D<WaterRegion> waterTable,
        int x,
        int y,
        int contents )
    {
        List<Block> blocks = blockTable.getItemsAt( x, y );
        Shape[] shapes = new Shape[blocks.size()];
        for ( int i = 0; i < blocks.size(); i++ )
        {
            shapes[i] = blocks.get( i ).shape;
        }

        boolean outsideWorld = ( x == -1 || x == blockTable.size.width
            || y == -1 || y == blockTable.size.height );

        List<WaterRegion> waterRegions =
            makeWaterRegion( x, y, shapes, contents, outsideWorld );
        waterTable.addAll( waterRegions );
    }

    /** Create a set of empty water regions from the given shaped blocks. */
    public static List<WaterRegion> makeWaterRegion( 
        int x, 
        int y,
        Shape[] shapes,
        boolean outsideWorld 
    )
    {
        return makeWaterRegion( x, y, shapes, 0, outsideWorld );
    }

    /** Create a set of water regions from the given shaped blocks. */
    public static List<WaterRegion> makeWaterRegion( 
        int x, 
        int y,
        Shape[] shapes,
        int contents,
        boolean outsideWorld 
    )
    {
        Set<CellularDirection> connections = new HashSet<>(
            Arrays.asList( UP, LEFT, RIGHT, DOWN ) );
        for ( Shape shape : shapes )
        {
            switch ( shape )
            {
                case FLAT:
                    // This shape fills the whole space.
                    return Collections.<WaterRegion>emptyList();
                case UP_RIGHT:
                    connections.remove( DOWN );
                    connections.remove( RIGHT );
                    break;
                case UP_LEFT:
                    connections.remove( DOWN );
                    connections.remove( LEFT );
                    break;
                 // Bridges allow water to pass through them.
                case BRIDGE_UP_LEFT:
                case BRIDGE_UP_RIGHT:
                    break;
                default:
                    throw new IllegalArgumentException(
                        "Unrecognised shape: " + shape );
            }
        }

        int capacity = findCapacity( connections );

        return Arrays.asList(
            new WaterRegion( 
                x, 
                y,
                connections,
                capacity, 
                contents, 
                outsideWorld 
        ) );
    }

    private static int findCapacity( Set<CellularDirection> connections )
    {
        if ( connections.size() == 4 )
        {
            return WaterUtil.MAX_CAPACITY;
        }
        else if ( connections.size() == 3 )
        {
            System.out.println(
                "Unexpected combination of shapes has " +
                "produced the connections: " + connections );
            // Grudgingly we can calculate the amount.
            return WaterUtil.HALF_CAPACITY + WaterUtil.QUARTER_CAPACITY;
        }
        else if ( connections.size() == 2 )
        {
            return WaterUtil.HALF_CAPACITY;
        }
        else if ( connections.size() == 1 )
        {
            return WaterUtil.QUARTER_CAPACITY;
        }
        return 0;
    }
}

package rabbitescape.engine;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;
import static rabbitescape.engine.CellularDirection.DOWN;
import static rabbitescape.engine.CellularDirection.LEFT;
import static rabbitescape.engine.CellularDirection.RIGHT;
import static rabbitescape.engine.CellularDirection.UP;
import static rabbitescape.engine.util.WaterUtil.HALF_CAPACITY;
import static rabbitescape.engine.util.WaterUtil.MAX_CAPACITY;
import static rabbitescape.engine.util.WaterUtil.QUARTER_CAPACITY;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import rabbitescape.engine.Block.Material;
import rabbitescape.engine.Block.Shape;
import rabbitescape.engine.util.Dimension;
import rabbitescape.engine.util.LookupTable2D;
import rabbitescape.engine.util.Position;
import rabbitescape.engine.util.Util;
import rabbitescape.engine.util.WaterUtil;

public class TestWaterRegionFactory
{
    /**
     * Create a table containing a block and two ramps. Check the generated water table.
     */
    @Test
    public void Generate_water_table()
    {
        Block block = new Block( 0, 0, Material.EARTH, Shape.FLAT, 0 );
        Block leftRamp = new Block( 0, 1, Material.EARTH, Shape.UP_LEFT, 0 );
        Block rightRamp = new Block( 1, 0, Material.EARTH, Shape.UP_RIGHT, 0 );
        List<Block> blocks = Arrays.asList( block , leftRamp, rightRamp );
        LookupTable2D<Block> blockTable = new LookupTable2D<>( blocks , new Dimension( 2, 2 ) );

        LookupTable2D<WaterRegion> waterTable = WaterRegionFactory
            .generateWaterTable( blockTable, new HashMap<Position, Integer>() );

        assertThat( waterTable.size, equalTo( blockTable.size ) );
        assertNull( waterTable.getItemAt( 0, 0 ) );
        assertThat( waterTable.getItemAt( 0, 1 ), equalTo( new WaterRegion( 0, 1, Util.newSet( UP, RIGHT ), HALF_CAPACITY ) ) );
        assertThat( waterTable.getItemAt( 1, 0 ), equalTo( new WaterRegion( 1, 0, Util.newSet( UP, LEFT ), HALF_CAPACITY ) ) );
        assertThat( waterTable.getItemAt( 1, 1 ), equalTo( new WaterRegion( 1, 1, Util.newSet( UP, LEFT, RIGHT, DOWN ), MAX_CAPACITY ) ) );

        // Also check the perimeter.
        for ( int i = -1; i <= 2; i++ )
        {
            assertThat( waterTable.getItemAt( -1, i ), equalTo( new WaterRegion( -1, i, Util.newSet( UP, LEFT, RIGHT, DOWN ), MAX_CAPACITY ) ) );
            assertThat( waterTable.getItemAt( i, -1 ), equalTo( new WaterRegion( i, -1, Util.newSet( UP, LEFT, RIGHT, DOWN ), MAX_CAPACITY ) ) );
            assertThat( waterTable.getItemAt( i, 2 ), equalTo( new WaterRegion( i, 2, Util.newSet( UP, LEFT, RIGHT, DOWN ), MAX_CAPACITY ) ) );
            assertThat( waterTable.getItemAt( 2, i ), equalTo( new WaterRegion( 2, i, Util.newSet( UP, LEFT, RIGHT, DOWN ), MAX_CAPACITY ) ) );
        }
    }

    /**
     * Test creating regions for a cell with both types of bridges in it.
     */
    @Test
    public void Make_water_region_two_bridges()
    {
        List<WaterRegion> waterRegions = WaterRegionFactory.makeWaterRegion( 0, 0, new Shape[]{ Shape.BRIDGE_UP_RIGHT, Shape.BRIDGE_UP_LEFT }, false );

        List<WaterRegion> expected = Arrays.asList( new WaterRegion( 0, 0, Util.newSet( UP, LEFT, RIGHT, DOWN ), MAX_CAPACITY ) );
        assertThat( waterRegions, equalTo( expected ) );
    }

    /**
     * Test creating regions for a cell with both types of ramps in it.
     */
    @Test
    public void Make_water_region_two_ramps()
    {
        List<WaterRegion> waterRegions = WaterRegionFactory.makeWaterRegion( 0, 0, new Shape[]{ Shape.UP_RIGHT, Shape.UP_LEFT }, false );

        List<WaterRegion> expected = Arrays.asList( new WaterRegion( 0, 0, Util.newSet( UP ), QUARTER_CAPACITY ) );
        assertThat( waterRegions, equalTo( expected ) );
    }

    /**
     * Test creating regions for a cell with a ramp and a block in it.
     */
    @Test
    public void Make_water_region_block_and_ramp()
    {
        List<WaterRegion> waterRegions = WaterRegionFactory.makeWaterRegion( 0, 0, new Shape[]{ Shape.UP_LEFT, Shape.FLAT }, false );
        assertThat( waterRegions.size(), equalTo( 0 ) );
    }

    /**
     * Test creating regions for a cell with a bridge and a ramp in it.
     */
    @Test
    public void Make_water_region_bridge_and_ramp()
    {
        List<WaterRegion> waterRegions = WaterRegionFactory.makeWaterRegion( 0, 0, new Shape[]{ Shape.BRIDGE_UP_LEFT, Shape.UP_LEFT }, false );

        List<WaterRegion> expected = Arrays.asList( new WaterRegion( 0, 0, Util.newSet( UP, RIGHT ), HALF_CAPACITY ) );
        assertThat( waterRegions, equalTo( expected ) );
    }

    /**
     * Create a table containing a half-full and completely full cell.
     */
    @Test
    public void Water_table_can_contain_standing_water()
    {
        List<Block> blocks = Collections.emptyList();
        LookupTable2D<Block> blockTable = new LookupTable2D<>( blocks , new Dimension( 2, 2 ) );

        HashMap<Position, Integer> waterAmounts = new HashMap<Position, Integer>();
        waterAmounts.put( new Position(0, 0), WaterUtil.HALF_CAPACITY );
        waterAmounts.put( new Position(1, 1), WaterUtil.MAX_CAPACITY );
        
        LookupTable2D<WaterRegion> waterTable = WaterRegionFactory
            .generateWaterTable( blockTable, waterAmounts );

        assertThat( waterTable.size, equalTo( blockTable.size ) );
        Set<CellularDirection> allDirs = Util.newSet( UP, LEFT, RIGHT, DOWN );
        // The cell at 0,0 should be half-full.
        assertThat( waterTable.getItemAt( 0, 0 ), equalTo( new WaterRegion( 0, 0, allDirs, MAX_CAPACITY, HALF_CAPACITY, false ) ) );
        // These two cells should be empty.
        assertThat( waterTable.getItemAt( 0, 1 ), equalTo( new WaterRegion( 0, 1, allDirs, MAX_CAPACITY ) ) );
        assertThat( waterTable.getItemAt( 1, 0 ), equalTo( new WaterRegion( 1, 0, allDirs, MAX_CAPACITY ) ) );
        // The cell at 1,1 should be full.
        assertThat( waterTable.getItemAt( 1, 1 ), equalTo( new WaterRegion( 1, 1, allDirs, MAX_CAPACITY, MAX_CAPACITY, false ) ) );
    }
}

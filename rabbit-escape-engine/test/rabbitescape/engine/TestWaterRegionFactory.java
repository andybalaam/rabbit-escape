package rabbitescape.engine;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;
import static rabbitescape.engine.Direction.DOWN;
import static rabbitescape.engine.Direction.LEFT;
import static rabbitescape.engine.Direction.RIGHT;
import static rabbitescape.engine.Direction.UP;
import static rabbitescape.engine.util.WaterUtil.HALF_CAPACITY;
import static rabbitescape.engine.util.WaterUtil.MAX_CAPACITY;
import static rabbitescape.engine.util.WaterUtil.QUARTER_CAPACITY;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import rabbitescape.engine.Block.Material;
import rabbitescape.engine.Block.Shape;
import rabbitescape.engine.util.Dimension;
import rabbitescape.engine.util.LookupTable2D;
import rabbitescape.engine.util.Util;

public class TestWaterRegionFactory
{
    /**
     * Create a table containing a block and two ramps. Check the generated water table.
     */
    @Test
    public void generateWaterTable()
    {
        Block block = new Block( 0, 0, Material.EARTH, Shape.FLAT, 0 );
        Block leftRamp = new Block( 0, 1, Material.EARTH, Shape.UP_LEFT, 0 );
        Block rightRamp = new Block( 1, 0, Material.EARTH, Shape.UP_RIGHT, 0 );
        List<Block> blocks = Arrays.asList( block , leftRamp, rightRamp );
        LookupTable2D<Block> blockTable = new LookupTable2D<>( blocks , new Dimension( 2, 2 ) );
        
        LookupTable2D<WaterRegion> waterTable = WaterRegionFactory.generateWaterTable( blockTable );
        
        assertThat( waterTable.size, equalTo( blockTable.size ) );
        assertNull( waterTable.getItemAt( 0, 0 ) );
        assertThat( waterTable.getItemAt( 0, 1 ), equalTo( new WaterRegion( 0, 1, Util.newSet( UP, RIGHT ), HALF_CAPACITY ) ) );
        assertThat( waterTable.getItemAt( 1, 0 ), equalTo( new WaterRegion( 1, 0, Util.newSet( UP, LEFT ), HALF_CAPACITY ) ) );
        assertThat( waterTable.getItemAt( 1, 1 ), equalTo( new WaterRegion( 1, 1, Util.newSet( UP, LEFT, RIGHT, DOWN ), MAX_CAPACITY ) ) );
    }
    
    /**
     * Test creating regions for a cell with both types of bridges in it.
     */
    @Test
    public void makeWaterRegion_twoBridges()
    {
        List<WaterRegion> waterRegions = WaterRegionFactory.makeWaterRegion( 0, 0, new Shape[]{ Shape.BRIDGE_UP_RIGHT, Shape.BRIDGE_UP_LEFT } );
        
        List<WaterRegion> expected = Arrays.asList( new WaterRegion( 0, 0, Util.newSet( UP, LEFT, RIGHT, DOWN ), MAX_CAPACITY ) );
        assertThat( waterRegions, equalTo( expected ) );
    }
    
    /**
     * Test creating regions for a cell with both types of ramps in it.
     */
    @Test
    public void makeWaterRegion_twoRamps()
    {
        List<WaterRegion> waterRegions = WaterRegionFactory.makeWaterRegion( 0, 0, new Shape[]{ Shape.UP_RIGHT, Shape.UP_LEFT } );
        
        List<WaterRegion> expected = Arrays.asList( new WaterRegion( 0, 0, Util.newSet( UP ), QUARTER_CAPACITY ) );
        assertThat( waterRegions, equalTo( expected ) );
    }
    
    /**
     * Test creating regions for a cell with a ramp and a block in it.
     */
    @Test
    public void makeWaterRegion_blockAndRamp()
    {
        List<WaterRegion> waterRegions = WaterRegionFactory.makeWaterRegion( 0, 0, new Shape[]{ Shape.UP_LEFT, Shape.FLAT } );
        assertThat( waterRegions.size(), equalTo( 0 ) );
    }
    
    /**
     * Test creating regions for a cell with a bridge and a ramp in it.
     */
    @Test
    public void makeWaterRegion_bridgeAndRamp()
    {
        List<WaterRegion> waterRegions = WaterRegionFactory.makeWaterRegion( 0, 0, new Shape[]{ Shape.BRIDGE_UP_LEFT, Shape.UP_LEFT } );

        List<WaterRegion> expected = Arrays.asList( new WaterRegion( 0, 0, Util.newSet( UP, RIGHT ), HALF_CAPACITY ) );
        assertThat( waterRegions, equalTo( expected ) );
    }
}

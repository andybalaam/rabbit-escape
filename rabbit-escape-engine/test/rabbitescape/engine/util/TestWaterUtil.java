package rabbitescape.engine.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static rabbitescape.engine.CellularDirection.DOWN;
import static rabbitescape.engine.CellularDirection.HERE;
import static rabbitescape.engine.CellularDirection.LEFT;
import static rabbitescape.engine.CellularDirection.RIGHT;
import static rabbitescape.engine.CellularDirection.UP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import rabbitescape.engine.CellularDirection;
import rabbitescape.engine.WaterRegion;

public class TestWaterUtil
{
    @Test
    public void empty_neighbourhood()
    {
        // Create a water table containing just one region.
        WaterRegion region = new WaterRegion( 0, 0, Util.newSet( UP, LEFT, RIGHT, DOWN ), 100 );
        LookupTable2D<WaterRegion> waterTable = new LookupTable2D<>( Arrays.asList( region ), new Dimension( 1, 1 ) );

        Map<CellularDirection, WaterRegion> neighbourhood = WaterUtil.findNeighbourhood( region , waterTable );

        assertThat( neighbourhood.keySet(), equalTo( Util.newSet( HERE ) ) );
        assertThat( neighbourhood.get( HERE ), equalTo( region ) );
    }

    @Test
    public void neighbourhood_includes_perimeter()
    {
        // Create a 1x1 water table with a perimeter of size 1 all the way around.
        List<WaterRegion> allRegions = new ArrayList<>();
        for ( int x = -1; x <= 1; x++ )
        {
            for ( int y = -1; y <= 1; y++ )
            {
                allRegions.add( new WaterRegion( x, y, Util.newSet( UP, LEFT, RIGHT, DOWN ), 100 ) );
            }
        }
        LookupTable2D<WaterRegion> waterTable = new LookupTable2D<>( allRegions, new Dimension( 1, 1 ) );
        WaterRegion region = waterTable.getItemAt( 0, 0 );

        Map<CellularDirection, WaterRegion> neighbourhood = WaterUtil.findNeighbourhood( region , waterTable );

        assertThat( neighbourhood.keySet(), equalTo( Util.newSet( UP, LEFT, RIGHT, DOWN, HERE ) ) );
    }

    /**
     * Calculate the neighbourhood of a ramp in a water table of the form:
     * <pre>
     *        [/RAMP]       
     * [SPACE][/RAMP][BLOCK]
     *        [SPACE]       
     * </pre>
     * Check that the only neighbour returned is the space to the left.
     */
    @Test
    public void connection_has_to_be_in_both_directions()
    {
        WaterRegion middle = new WaterRegion( 1, 1, Util.newSet( UP, LEFT ), 10 );
        WaterRegion top = new WaterRegion( 1, 0, Util.newSet( UP, LEFT ), 20 );
        WaterRegion left = new WaterRegion( 0, 1, Util.newSet( UP, LEFT, RIGHT, DOWN ), 30 );
        // Right has no WaterRegion, because it's a solid block.
        WaterRegion bottom = new WaterRegion( 1, 2, Util.newSet( UP, LEFT, RIGHT, DOWN ), 40 );
        LookupTable2D<WaterRegion> waterTable = new LookupTable2D<>(
            Arrays.asList( middle, top, left, bottom ), new Dimension( 3, 3 ) );

        Map<CellularDirection, WaterRegion> neighbourhood = WaterUtil.findNeighbourhood( middle , waterTable );

        assertThat( neighbourhood.keySet(), equalTo( Util.newSet( LEFT, HERE ) ) );
        assertThat( neighbourhood.get( LEFT ), equalTo( left ) );
        assertThat( neighbourhood.get( HERE ), equalTo( middle ) );
    }

    @Test( expected = IllegalStateException.class )
    public void exception_if_two_regions_connected_on_same_side()
    {
        WaterRegion region = new WaterRegion( 0, 0, Util.newSet( DOWN ), 100 );
        WaterRegion bottomA = new WaterRegion( 0, 1, Util.newSet( UP ), 100 );
        WaterRegion bottomB = new WaterRegion( 0, 1, Util.newSet( UP ), 100 );
        LookupTable2D<WaterRegion> waterTable = new LookupTable2D<>(
            Arrays.asList( region, bottomA, bottomB ), new Dimension( 2, 2 ) );

        WaterUtil.findNeighbourhood( region , waterTable );
    }

    @Test
    public void no_exception_if_two_regions_on_same_side_arent_both_connected()
    {
        WaterRegion region = new WaterRegion( 0, 0, Util.newSet( DOWN ), 100 );
        WaterRegion bottomA = new WaterRegion( 0, 1, Util.newSet( UP ), 100 );
        WaterRegion bottomB = new WaterRegion( 0, 1, Util.newSet( LEFT ), 100 );
        LookupTable2D<WaterRegion> waterTable = new LookupTable2D<>(
            Arrays.asList( region, bottomA, bottomB ), new Dimension( 2, 2 ) );

        Map<CellularDirection, WaterRegion> neighbourhood = WaterUtil.findNeighbourhood( region , waterTable );
        assertThat( neighbourhood.keySet(), equalTo( Util.newSet( DOWN, HERE ) ) );
        assertThat( neighbourhood.get( DOWN ), equalTo( bottomA ) );
        assertThat( neighbourhood.get( HERE ), equalTo( region ) );
    }
}

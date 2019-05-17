package rabbitescape.engine;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static rabbitescape.engine.CellularDirection.DOWN;
import static rabbitescape.engine.CellularDirection.LEFT;
import static rabbitescape.engine.CellularDirection.RIGHT;
import static rabbitescape.engine.CellularDirection.UP;
import static rabbitescape.engine.textworld.TextWorldManip.createWorld;

import java.util.Map;

import org.junit.Test;

import rabbitescape.engine.util.Util;

public class TestWaterRegion
{
    @Test
    public void round_trip_persistance()
    {
        WaterRegion start = new WaterRegion( 0, 0, Util.newSet( UP, LEFT, RIGHT, DOWN ), 25 );
        start.addContents( 10 );

        Map<String, String> persisted = start.saveState( true );
        WaterRegion end = new WaterRegion( 0, 0, null, 0 );
        end.restoreFromState( persisted );

        assertThat( end, equalTo( start ) );
    }

    @Test
    public void no_need_to_calc_new_state_outside_world()
    {
        WaterRegion waterRegion = new WaterRegion( -1, 0, null, 25, 25, true );
        // Pass in a null world and check that there's no NPE.
        waterRegion.calcNewState( null );
    }

    @Test
    public void no_need_to_calc_new_state_for_empty_cell()
    {
        WaterRegion waterRegion = new WaterRegion( 1, 1, null, 25, 0, false );
        // Pass in a null world and check that there's no NPE.
        waterRegion.calcNewState( null );
    }

    @Test
    public void empty_flow_causes_noop_step()
    {
        WaterRegion waterRegion = new WaterRegion( 0, 0, null, 25 );
        // Pass in a null world and check that there's no NPE.
        waterRegion.step( null );
    }

    @Test
    public void flow_is_set_and_then_reset()
    {
        String[] worldString = {
            "# #",
            "# #",
            "###"
        };
        World world = createWorld( worldString );
        // Add one unit of water to the top cell.
        WaterRegion topWaterRegion = world.waterTable.getItemAt( 1, 0 );
        topWaterRegion.addContents( 1 );
        WaterRegion bottomWaterRegion = world.waterTable.getItemAt( 1, 1 );

        // After calcNewState the flow should be set, but the bottom cell should still be empty.
        topWaterRegion.calcNewState( world );
        assertThat( topWaterRegion.getFlow( DOWN ), equalTo( 1 ) );
        assertThat( bottomWaterRegion.getContents(), equalTo( 0 ) );

        // After step the bottom cell should contain water and the flow should be reset.
        topWaterRegion.step( world );
        assertThat( topWaterRegion.getFlow( DOWN ), equalTo( 0 ) );
        assertThat( bottomWaterRegion.getContents(), equalTo( 1 ) );
    }

    @Test
    public void water_disappears_off_the_bottom_of_the_world()
    {
        String[] worldString = {
            "# #"
        };
        World world = createWorld( worldString );
        // Add one unit of water to the empty cell.
        WaterRegion waterRegion = world.waterTable.getItemAt( 1, 0 );
        waterRegion.addContents( 1 );
        WaterRegion waterRegionOffBottom = world.waterTable.getItemAt( 1, 1 );

        // After calcNewState the flow should be set.
        waterRegion.calcNewState( world );
        assertThat( waterRegion.getFlow( DOWN ), equalTo( 1 ) );

        // After step the water should have disappeared.
        waterRegion.step( world );
        assertThat( waterRegion.getContents(), equalTo( 0 ) );
        assertThat( waterRegion.getFlow( DOWN ), equalTo( 0 ) );
        assertThat( "The water should disappear off the bottom of the world",
            waterRegionOffBottom.getContents(), equalTo( 0 ) );
    }
}

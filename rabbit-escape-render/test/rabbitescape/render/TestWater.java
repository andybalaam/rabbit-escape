package rabbitescape.render;

import static rabbitescape.engine.textworld.TextWorldManip.createWorld;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.Test;

import rabbitescape.engine.World;
import rabbitescape.render.gameloop.WaterAnimation;


public class TestWater
{
    @Test
    public void Water_falls_from_pipe()
    {
        World world = createWorld(
            " P ",
            "# /",
            "###"
        );
        WaterAnimation animation = new WaterAnimation( world );
        waterStep( 1, world, animation );
        WaterRegionRenderer pipeRR = animation.lookupRenderer.getItemAt( 1, 0 );
        assertThat( pipeRR.isFalling(), equalTo( true ) );
    }

    @Test
    public void Region_below_pipe_fills_in_5()
    {
        World world = createWorld(
            " P ",
            "# #",
            "###"
        );
        WaterAnimation animation = new WaterAnimation( world );
        waterStep( 4, world, animation );
        WaterRegionRenderer poolRR = animation.lookupRenderer.getItemAt( 1, 1 );
        assertThat( poolRR.isFull( 1, 1 ), equalTo( false ) );
        waterStep( 1, world, animation );
        assertThat( poolRR.isFull( 1, 1 ), equalTo( true ) );
    }

    @Test
    public void Pipe_region_starts_filling_in_5()
    {
        World world = createWorld(
            " P ",
            "# #",
            "###"
        );
        WaterAnimation animation = new WaterAnimation( world );
        waterStep( 4, world, animation );
        WaterRegionRenderer pipeRR = animation.lookupRenderer.getItemAt( 1, 0 );
        assertThat( pipeRR.isFalling(), equalTo( true ) );
        waterStep( 1, world, animation );
        assertThat( pipeRR.isFalling(), equalTo( false ) );
    }

    // -----------

    private static void waterStep( int n, World w, WaterAnimation d )
    {
        for ( int i = 0 ; i < n; i++ )
        {
            w.step();
            d.step( w );
            for ( int j = 0 ; j < 10 ; j++ )
            {
                d.update( j );
            }
        }
    }
}

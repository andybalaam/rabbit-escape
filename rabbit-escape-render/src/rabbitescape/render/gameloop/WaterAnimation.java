package rabbitescape.render.gameloop;

import java.util.ArrayList;
import java.util.List;

import static rabbitescape.engine.CellularDirection.*;

import rabbitescape.engine.WaterRegion;
import rabbitescape.engine.World;
import rabbitescape.engine.util.CellDebugPrint;
import rabbitescape.engine.util.Dimension;
import rabbitescape.engine.util.LookupTable2D;
import rabbitescape.render.PolygonBuilder;
import rabbitescape.render.WaterRegionRenderer;

/**
 * Manages animation of polygons for water.
 */
public class WaterAnimation
{
    public final LookupTable2D<WaterRegionRenderer> lookupRenderer;
    private int lastFramenumber = 10;
    public final Dimension worldSize;

    public WaterAnimation( World world )
    {
        worldSize = world.size;
        lookupRenderer = new LookupTable2D<WaterRegionRenderer>( worldSize );
    }

    private WaterAnimation()
    {
        lookupRenderer = null;
        worldSize = null;
    }

    public static WaterAnimation getDummyWaterAnimation()
    {
        return new WaterAnimation();
    }

    /**
     * Animation step update. Several may have passed before this is called.
     */
    public void update( int frameNumber )
    {
        if ( null == lookupRenderer )
        {
            return;
        }

        int interval = frameNumber - lastFramenumber;
        interval = ( interval < 0 ) ? ( interval + 10 ) : ( interval );
        lastFramenumber = frameNumber;
        for ( int i = 0 ; i < interval ; i++ )
        {
            for( WaterRegionRenderer wrr: lookupRenderer )
            {
                wrr.setWaterHeight();
            }
            for( WaterRegionRenderer wrr: lookupRenderer )
            {
                wrr.removeHeightGaps();
            }
        }
    }

    public List<PolygonBuilder> calculatePolygons()
    {
        ArrayList<PolygonBuilder> polygons = new ArrayList<PolygonBuilder>();
        for ( int y = 0; y < worldSize.height ; y++ )
        {
            PolygonBuilder p = new PolygonBuilder();
            WaterRegionRenderer start = null;
            for ( int x = 0; x < worldSize.width; x++ )
            {
                WaterRegionRenderer wrr = lookupRenderer.getItemAt( x, y );
                if ( null == wrr || wrr.adjacentWaterIsFalling( HERE ) )
                {
                    continue;
                }
                p.add( wrr.topVertex( LEFT ) );
                if ( null == start )
                {
                    start = wrr;
                }
                if ( !wrr.region.isConnected( RIGHT ) || wrr.adjacentIsNull( RIGHT ) || wrr.adjacentWaterIsFalling( RIGHT ) )
                {
                    p.add( wrr.topVertex( RIGHT ) );
                    p.add( wrr.bottomVertex( RIGHT ) );
                    p.add( start.bottomVertex( LEFT ) );
                    start = null;
                    polygons.add( p );
                    p = new PolygonBuilder();
                }
            }
        }

        return polygons;
    }

    /**
     * Game step. Usually ten animation steps per game step.
     */
    public void step( World world )
    {
        if ( null == world || null == lookupRenderer )
        {
            return;
        }

        List<WaterRegionRenderer> currentRR = lookupRenderer.getListCopy();
        for ( WaterRegion w: world.waterTable )
        {
            if ( w.getContents() == 0 )
            { // Empty regions do not need renderers.
                continue;
            }
            WaterRegionRenderer r = lookupRenderer.getItemAt( w.x, w.y );
            if ( null == r)
            { // Create renderer for region that does not have one
                lookupRenderer.add( new WaterRegionRenderer( w, world, this ) );
            }
            else
            { // Remove renderers that are being used from temporary list.
                currentRR.remove( r );
            }
        }
        // Remove renderers that no longer have regions
        lookupRenderer.removeAll( currentRR );

        for( WaterRegionRenderer wrr: lookupRenderer )
        {
            wrr.setTargetWaterHeight();
        }
    }

    public void debugPrint()
    {
        CellDebugPrint p = new CellDebugPrint();
        for ( WaterRegionRenderer wrr: lookupRenderer )
        {
            wrr.debugCellPrint( p );
        }
        p.print();
    }



}

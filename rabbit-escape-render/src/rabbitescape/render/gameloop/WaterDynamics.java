package rabbitescape.render.gameloop;

import java.util.List;
import java.util.Vector;

import static rabbitescape.engine.CellularDirection.*;

import rabbitescape.engine.WaterRegion;
import rabbitescape.engine.World;
import rabbitescape.engine.util.CellDebugPrint;
import rabbitescape.engine.util.Dimension;
import rabbitescape.engine.util.LookupTable2D;
import rabbitescape.render.Polygon;
import rabbitescape.render.WaterRegionRenderer;

/**
 * Manages animation of polygons for water.
 */
public class WaterDynamics
{
    public final LookupTable2D<WaterRegionRenderer> lookupRenderer ;
    public final Vector<Polygon> polygons;
    private int lastFramenumber = 10;
    public final Dimension worldSize;

    public WaterDynamics( World world )
    {
        worldSize = world.size;
        lookupRenderer = new LookupTable2D<WaterRegionRenderer>( worldSize );
        polygons = new Vector<Polygon>();
    }

    private WaterDynamics()
    {
        lookupRenderer = null;
        polygons = null;
        worldSize = null;
    }

    public static WaterDynamics getDummyWaterDynamics()
    {
        return new WaterDynamics();
    }

    /**
     * Animation step update. Several may have passed before this is called.
     */
    public void update( int frameNumber )
    {
        if ( null == polygons )
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
        calculatePolygons();
    }

    private void calculatePolygons()
    {
        polygons.clear();
        for ( WaterRegionRenderer wrr: lookupRenderer )
        {
            wrr.drawnLB = wrr.drawnLT = wrr.drawnR = false;
        }
        for ( int y = 0; y < worldSize.height ; y++ )
        {
            Polygon p = new Polygon();
            WaterRegionRenderer start = null;
            for ( int x = 0; x < worldSize.width; x++ )
            {
                WaterRegionRenderer wrr = lookupRenderer.getItemAt( x, y );
                if ( null == wrr || wrr.adjacentWaterIsFalling( HERE ) )
                {
                    continue;
                }
                wrr.topVertex( p.x, p.y, LEFT );
                wrr.drawnLT = true;
                if ( null == start )
                {
                    start = wrr;
                }
                if ( !wrr.region.isConnected( RIGHT ) || wrr.adjacentNull( RIGHT ) || wrr.adjacentWaterIsFalling( RIGHT ) )
                {
                    wrr.topVertex( p.x, p.y, RIGHT );
                    wrr.bottomVertex( p.x, p.y, RIGHT );
                    start.bottomVertex( p.x, p.y, LEFT );
                    start.drawnLB = wrr.drawnR = true;
                    start = null;
                    polygons.add( p );
                    p = new Polygon();
                }
            }
        }
    }

    /**
     * Game step. Usually ten animation steps per game step.
     */
    public void step( World world)
    {
        if ( null == world || null == polygons )
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
            // Items left are not matched to a region. Remove them.
            // TODO should they disappear more slowly
        }
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

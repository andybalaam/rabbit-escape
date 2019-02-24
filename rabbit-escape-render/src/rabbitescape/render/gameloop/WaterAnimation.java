package rabbitescape.render.gameloop;

import rabbitescape.engine.WaterRegion;
import rabbitescape.engine.World;
import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.ConfigKeys;
import rabbitescape.engine.config.ConfigTools;
import rabbitescape.engine.util.CellDebugPrint;
import rabbitescape.engine.util.Dimension;
import rabbitescape.engine.util.LookupTable2D;
import rabbitescape.render.PolygonBuilder;
import rabbitescape.render.WaterRegionRenderer;

import java.util.ArrayList;
import java.util.List;

import static rabbitescape.engine.CellularDirection.HERE;
import static rabbitescape.render.CellPosition.*;

/**
 * Manages animation of polygons for water.
 */
public class WaterAnimation
{
    public final LookupTable2D<WaterRegionRenderer> lookupRenderer;
    private int lastFramenumber = 10;
    public final Dimension worldSize;
    private GameLoop gameLoop = null;
    public int contentsPerParticle;
    public final boolean dynCPP;

    public WaterAnimation( World world, Config config )
    {
        worldSize = world.size;
        lookupRenderer = new LookupTable2D<WaterRegionRenderer>( worldSize );
        dynCPP = ConfigTools.getBool( config,
            ConfigKeys.CFG_WATER_DYN_CONTENTS_PER_PARTICLE );
        contentsPerParticle = ConfigTools.getInt( config,
            ConfigKeys.CFG_WATER_CONTENTS_PER_PARTICLE );
    }

    /**
     * Constructor for tests that won't be rendering particles.
     */
    public WaterAnimation( World world )
    {
        worldSize = world.size;
        lookupRenderer = new LookupTable2D<WaterRegionRenderer>( worldSize );
        dynCPP = false;
        contentsPerParticle = 4;
    }

    private WaterAnimation()
    {
        lookupRenderer = null;
        worldSize = null;
        dynCPP = false;
        contentsPerParticle = 4;
    }

    public static WaterAnimation getDummyWaterAnimation()
    {
        return new WaterAnimation();
    }

    public void setGameLoop( GameLoop gl )
    {
        gameLoop = gl;
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
            for( WaterRegionRenderer wrr: lookupRenderer )
            {
                wrr.stepParticles();
            }
        }
    }

    public List<PolygonBuilder> calculatePolygons()
    {
        ArrayList<PolygonBuilder> polygons = new ArrayList<PolygonBuilder>();
        for ( int y = 0; y < worldSize.height ; y++ )
        {
            WaterRegionRenderer start = null;
            for ( int x = 0; x < worldSize.width; x++ )
            {
                WaterRegionRenderer wrr = lookupRenderer.getItemAt( x, y );
                if ( null == wrr || wrr.adjacentWaterIsFalling( HERE ) )
                {
                    continue;
                }
                PolygonBuilder p = new PolygonBuilder();
                p.add( wrr.topVertex( TOP_LEFT ) );
                if ( null == start )
                {
                    start = wrr;
                }
                p.add( wrr.topVertex( TOP_MIDDLE ) );
                p.add( wrr.topVertex( TOP_RIGHT ) );
                p.add( wrr.bottomVertex( BOTTOM_RIGHT ) );
                p.add( start.bottomVertex( BOTTOM_LEFT ) );
                start = null;
                polygons.add( p );
            }
        }

        return polygons;
    }

    /**
     * Uses fewer particles if the computer is struggling.
     */
    private void moderateParticleCount()
    {
        if ( !dynCPP || null == gameLoop )
        {
            return;
        }
        long waitTime = gameLoop.getWaitTime();
        if ( GameLoop.WAIT_TIME_UNAVAILABLE == waitTime )
        {
            return;
        }
        if ( waitTime < 20l )
        {
            contentsPerParticle += 2 + contentsPerParticle / 3;
        }
        if ( waitTime > 30l )
        {
            contentsPerParticle -= 2 + contentsPerParticle / 3;
            contentsPerParticle = contentsPerParticle < 4 ?
                                  4 :
                                  contentsPerParticle;
        }
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

        moderateParticleCount();

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
                lookupRenderer.add( new WaterRegionRenderer( w.x, w.y, world, this ) );
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

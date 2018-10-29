package rabbitescape.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import rabbitescape.engine.Block;
import rabbitescape.engine.CellularDirection;
import static rabbitescape.engine.CellularDirection.UP;
import static rabbitescape.engine.CellularDirection.DOWN;
import static rabbitescape.engine.CellularDirection.LEFT;
import static rabbitescape.engine.CellularDirection.RIGHT;
import rabbitescape.engine.Pipe;
import rabbitescape.engine.Thing;
import rabbitescape.engine.WaterRegion;
import rabbitescape.engine.World;
import rabbitescape.engine.textworld.BlockRenderer;
import rabbitescape.engine.util.CellDebugPrint;
import rabbitescape.engine.util.LookupItem2D;
import rabbitescape.engine.util.MathUtil;
import rabbitescape.engine.util.Position;
import rabbitescape.render.gameloop.WaterAnimation;

import static rabbitescape.engine.BehaviourTools.*;
import static rabbitescape.engine.CellularDirection.*;

public class WaterRegionRenderer implements LookupItem2D
{

    public static final int contentsPerParticle = 4;
    private static final int maxHeightChange = 1;

    public WaterRegion region;

    private int targetWaterHeight = 0;
    private int height = 0;
    private int lastHeight = 0;

    private int targetParticleCount = 0;
    private int particleCount = 0;
    public final ArrayList<WaterParticle> particles = new ArrayList<WaterParticle>();

    private final World world;
    private final WaterAnimation waterAnimation;

    public WaterRegionRenderer(WaterRegion region, World world,
                               WaterAnimation waterAnimation )
    {
        this.region = region;
        this.world = world;
        this.waterAnimation = waterAnimation;
    }

    public WaterRegionRenderer adjacentRenderer( CellularDirection d )
    {
        return waterAnimation.lookupRenderer.getItemAt( region.x + d.xOffset, region.y + d.yOffset );
    }

    public boolean adjacentIsNull( CellularDirection d )
    {
        return null == adjacentRenderer( d );
    }

    public boolean adjacentWaterIsFalling( CellularDirection d )
    {
        WaterRegionRenderer adj = adjacentRenderer( d );
        if ( null == adj )
        {
            return false;
        }
        return adj.isFalling();
    }

    /**
     * Called once per game step
     * Calculates height of water in region (as an ambition - need to change
     * actual height smoothly). Also calculates target number of splash
     * particles.
     */
    public void setTargetWaterHeight()
    {
        if ( isFalling() )
        {
            targetWaterHeight = 0;
            // Falling water is represented by particles instead.
            adjustParticleCount();
            return;
        }
        targetParticleCount = 0;
        Block block = world.getBlockAt( region.x, region.y );
        if ( null == block || isBridge( block ))
        {
            targetWaterHeight = region.getContents() / 32;
        }
        else if ( isSlopeNotBridge( block ) )
        {
            targetWaterHeight = triangleHeight( region.getContents() );
        }
        else
        {
            throw new RuntimeException( "Unexpected block type" );
        }
        targetWaterHeight = targetWaterHeight > 32 ? 32 : targetWaterHeight;
    }

    /**
     * Fades splash particles in/out to approximate
     * correct anount of falling water. Called once per game step
     */
    private void adjustParticleCount()
    {
        targetParticleCount = region.getContents() / contentsPerParticle;
        int particleDeficit = targetParticleCount  - particles.size();
        if ( particleDeficit < 0 ) // Need to remove some: start fading
        {
            for ( int i = 0 ; i < -particleDeficit ; i++)
            {
                particles.get( i ).alphaStep =
                    -WaterParticle.alphaStepMagnitude;
            }
        }
        else if (particleDeficit > 0 ) // Need to add some
        {
            for ( int i = 0 ; i < particleDeficit ; i++)
            {
                particles.add( new WaterParticle( this ) );
            }
        }
    }

    /**
     * Called once per animation step.
     * Smoothly tweaks height in the direction of targetWaterHeight.
     */
    public void setWaterHeight()
    {
        height = MathUtil.constrain( targetWaterHeight,
                                     lastHeight - maxHeightChange ,
                                     lastHeight + maxHeightChange );
        lastHeight = height;
    }

    /**
     * Particles may be passed to an adjacent (or further) region.
     * Called once per animation step
     */
    private void checkParticlesInRegion()
    {
        // make temporary list to iterate, so we can transfer ownership of items
        ArrayList<WaterParticle> tmpList = new ArrayList<WaterParticle>(particles);
        for ( WaterParticle p : tmpList )
        {
            if ( p.outOfRegion( this ) )
            {
                int newX = (int)Math.floor(p.x), newY = (int)Math.floor(p.y);
                WaterRegionRenderer newRend =
                    waterAnimation.lookupRenderer.getItemAt( newX, newY );
                if ( newRend == null )
                { // no water here: accelerate fading
                    p.alphaStep = p.alphaStep - WaterParticle.alphaStepMagnitude;
                    continue;
                }
                if ( isFull( newX, newY ) )
                {   // particle has moved to full cell: delete immediately
                    // block cells count as full too
                    p.delete = true;
                    continue;
                }
                // transfer ownership to other renderer
                particles.remove( p );
                newRend.particles.add( p );
            }
        }
    }

    /**
     * Updates particle position. Called onces per animation step.
     */
    public void stepParticles()
    {
        // Some particles may have left the region: update this first.
        checkParticlesInRegion();

        for ( WaterParticle p : particles )
        {
            p.step();
            p.alpha += p.alphaStep;
            // If particle has finished waxing, stop fading it in.
            if ( p.alpha > 255 )
            {
                p.alpha = 255;
                p.alphaStep = 0;
            }
        }
        // fully waned particles are removed
        // TODO not sure deleted or faded particles are going
        Iterator<WaterParticle> i = particles.listIterator();
        while ( i.hasNext() )
        {
            WaterParticle p = i.next();
            if ( p.alpha < 0 || p.delete)
            {
                i.remove();
            }
        }
    }

    /**
     * Called once per animation step, after all heights set. Checks if cell above has something in, and makes this cell
     * look full, possibly breaking maxHeightChange.
     */
    public void removeHeightGaps()
    {
        WaterRegionRenderer above = adjacentRenderer( CellularDirection.UP );
        if ( null == above )
        {
            return;
        }
        if ( above.height > 0 )
        {
            height = 32;
        }
    }

    /**
     * A = 0.5 * l^2
     */
    private int triangleHeight( int area )
    {
        return (int)Math.sqrt( (double)( 2 * area ) );
    }

    /**
     * Adds an upper vertex for the polygon for this region to the
     * supplied ArrayLists of coordinates. Supplied vertex is towards the
     * cell in the supplied direction.
     */
    public Vertex topVertex( CellPosition d )
    {
        int x = region.x * 32, y = region.y * 32; // Local cell origin in nominal pixels.
        if ( 0 == height )
        {
            switch ( d )
            {
            case TOP_LEFT:
                return new Vertex( x, y + 32 );
            case TOP_MIDDLE:
                return new Vertex( x + 16, y + 32 );
            case TOP_RIGHT:
                return new Vertex( x + 32, y + 32 );
            default:
                throw new RuntimeException( "Can only add vertices for TOP_LEFT, TOP_MIDDLE or TOP_RIGHT cells.");
            }
        }
        Block block = world.getBlockAt( region.x, region.y );
        int boundaryHeight = calcBoundaryHeight( d );
        int xOffset, yOffset;

        switch ( d )
        {
        case TOP_LEFT:
            xOffset = 0;
            if ( shapeEquals ( block, Block.Shape.UP_LEFT ) )
            {
                yOffset = 0;
            }
            else
            {
                yOffset = 32 - boundaryHeight;
            }
            break;
        case TOP_MIDDLE:
            xOffset = 16;
            if ( shapeEquals ( block, Block.Shape.UP_LEFT ) || shapeEquals ( block, Block.Shape.UP_RIGHT ) )
            {
                yOffset = ( 32 - height ) / 2;
            }
            else
            {
                yOffset = 32 - height;
            }
            break;
        case TOP_RIGHT:
            xOffset = 32;
            if ( shapeEquals ( block, Block.Shape.UP_RIGHT ) )
            {
                yOffset = 0;
            }
            else
            {
                yOffset = 32 - boundaryHeight;
            }
            break;
        default:
            throw new RuntimeException( "Can only add vertices for LEFT or RIGHT cells." );
        }
        return new Vertex( x + xOffset, y + yOffset );
    }

    /**
     * For matching up heights in left/right cells.
     */
    public int adjacentRendererWaterHeight( CellularDirection d )
    {
        if ( region.isConnected( d ) )
        {
            WaterRegionRenderer wrr = adjacentRenderer ( d );
            if ( null == wrr )
            {
                return height;
            }
            return wrr.height;
        }
        return height;
    }

    private int calcBoundaryHeight( CellPosition cellPosition )
    {
        if ( cellPosition.leftRightness == null || !region.isConnected( cellPosition.leftRightness ) || isFull( region.x, region.y ) )
        { // The cell in that direction is not relevant
            return height;
        }
        WaterRegionRenderer adjWrr = adjacentRenderer( cellPosition.leftRightness );
        if ( null == adjWrr )
        { // Adjacent is probably empty, and this cell is probably a low level
            if ( region.isConnected( cellPosition.leftRightness ) )
            {
                WaterRegion adjReg = world.waterTable.getItemAt( region.x + cellPosition.leftRightness.xOffset, region.y + cellPosition.leftRightness.yOffset );
                if ( adjReg != null && adjReg.isConnected( CellularDirection.opposite( cellPosition.leftRightness ) ) )
                {
                    return 0;
                }
            }
            return height;
        }
        else if ( isFull( adjWrr.region.x, adjWrr.region.y ) )
        {
            return adjWrr.height;
        }
        return ( height + adjWrr.height ) / 2;
    }

    public Vertex bottomVertex( CellPosition d )
    {
        int x = region.x * 32, y = region.y * 32; // Local cell origin in nominal pixels.
        int xOffset;
        Block block = world.getBlockAt( region.x, region.y );
        switch ( d )
        {
        case BOTTOM_LEFT:
            if ( shapeEquals ( block, Block.Shape.UP_LEFT ) )
            {
                xOffset = 32;
            }
            else
            {
                xOffset = 0;
            }
            break;
        case BOTTOM_RIGHT:
            if ( shapeEquals ( block, Block.Shape.UP_RIGHT ) )
            {
                xOffset = 0;
            }
            else
            {
                xOffset = 32;
            }
            break;
        default:
            throw new RuntimeException( "Can only add bottom vertices for BOTTOM_LEFT or BOTTOM_RIGHT cells." );
        }
        return new Vertex( x + xOffset, y + 32 );
    }

    boolean isFalling()
    {
        if ( isSlopeNotBridge( world.getBlockAt( region.x, region.y ) ) )
        { // Non bridge slopes are not connected down.
            return false;
        }

        return !isFull( region.x, region.y + 1 );
    }

    boolean isFull( int x, int y )
    {
        Block b = world.getBlockAt( x, y );
        if ( s_isFlat( b ) )
        { // Flat block cells have zero capacity: always full.
            return true;
        }
        WaterRegionRenderer wrr = waterAnimation.lookupRenderer.getItemAt( x, y );
        if ( null == wrr )
        { // Not full of flat block, but no region yet, empty
            return false;
        }
        return 32 == wrr.height && 32 == wrr.targetWaterHeight;
    }

    public boolean hasPipe()
    {
        List<Thing> things = world.getThingsAt( region.x, region.y );
        for ( Thing t : things )
        {
            if ( t instanceof Pipe )
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the ney flow across a cell's edge considering water flowing
     * in and out. A posotive value indicates net flow out. A negative
     * value indicates net flow in.
     */
    public int edgeNetFlow( CellularDirection edge )
    {
        WaterRegionRenderer adj = adjacentRenderer( edge );
        if ( null == adj )
        {
            return region.getFlow( edge );
        }
        return region.getFlow( edge ) -
               adj.region.getFlow( CellularDirection.opposite( edge ) );
    }

    public Vertex netFlow()
    {
        float netX = (float)( edgeNetFlow( CellularDirection.RIGHT ) -
                              edgeNetFlow( CellularDirection.LEFT ) );
        float netY = (float)( edgeNetFlow( CellularDirection.DOWN ) -
                              edgeNetFlow( CellularDirection.UP ) );
        return new Vertex( netX, netY );
    }

    public Vector2D cellPosition()
    {
        return new Vector2D( region.x, region.y );
    }

    @Override
    public Position getPosition()
    {
        return new Position( region.x, region.y );
    }

    public void debugCellPrint( CellDebugPrint p )
    {
        Block b = world.getBlockAt( region.x, region.y );
        String s = null == b ? " " : "" + BlockRenderer.charForBlock( b );

        String connStr = "U" + bool01( region.isConnected( UP ) ) + " " +
                         "D" + bool01( region.isConnected( DOWN ) ) + " " +
                         "L" + bool01( region.isConnected( LEFT ) ) + " " +
                         "R" + bool01( region.isConnected( RIGHT ) );

        p.addString( region.x, region.y, 0,
            s  );

        p.addString( region, 1, "cont %04d", region.getContents() );

        p.addString( region, 4, connStr  );

        p.addString( region, 5,
            "falling" + bool01( isFalling() ) + " full" + isFull( region.x, region.y )  );

        p.addString( region, 6, "height(targ) %02d(%02d)", height, targetWaterHeight );

        p.addString(  region, 7, "(%d,%d)", region.x, region.y );
    }

    private String bool01( boolean b )
    {
        return b ? "1" : "0";
    }


}

package rabbitescape.render;

import rabbitescape.engine.*;
import rabbitescape.engine.textworld.BlockRenderer;
import rabbitescape.engine.util.*;
import rabbitescape.render.gameloop.WaterAnimation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static rabbitescape.engine.BehaviourTools.*;
import static rabbitescape.engine.CellularDirection.*;

public class WaterRegionRenderer implements LookupItem2D
{
    private static final int maxHeightChange = 1;

    private int x, y;
    private int targetWaterHeight = 0;
    private int height = 0;
    private int lastHeight = 0;

    private int targetParticleCount = 0;
    public final ArrayList<WaterParticle> particles =
        new ArrayList<WaterParticle>();

    private final World world;
    private final WaterAnimation waterAnimation;

    public WaterRegionRenderer(
        int x,
        int y,
        World world,
        WaterAnimation waterAnimation
    )
    {
        this.x = x;
        this.y = y;
        this.world = world;
        this.waterAnimation = waterAnimation;
    }

    public WaterRegionRenderer adjacentRenderer( CellularDirection d )
    {
        return waterAnimation.lookupRenderer.getItemAt(
            x + d.xOffset,
            y + d.yOffset
        );
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
        Block block = world.getBlockAt( x, y );
        if ( null == block || isBridge( block ) )
        {
            targetWaterHeight = getRegion().getContents() / 32;
        }
        else if ( isSlopeNotBridge( block ) )
        {
            targetWaterHeight = triangleHeight( getRegion().getContents() );
        }
        else
        {
            throw new RuntimeException( "Unexpected block type" );
        }
        targetWaterHeight = targetWaterHeight > 32 ? 32 : targetWaterHeight;
    }

    /**
     * Fades splash particles in/out to approximate
     * correct amount of falling water. Called once per game step
     */
    private void adjustParticleCount()
    {
        targetParticleCount = getRegion().getContents() /
            waterAnimation.contentsPerParticle;
        int particleDeficit = targetParticleCount - particles.size();
        if ( particleDeficit < 0 ) // Need to remove some: start fading
        {
            for ( int i = 0; i < -particleDeficit; i++ )
            {
                WaterParticle p = particles.get( i );
                p.alphaStep = -p.alphaStepMagnitude;
            }
        }
        else if ( particleDeficit > 0 ) // Need to add some
        {
            for ( int i = 0; i < particleDeficit; i++ )
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
        height = MathUtil.constrain(
            targetWaterHeight,
            lastHeight - maxHeightChange,
            lastHeight + maxHeightChange
        );
        lastHeight = height;
    }

    /**
     * Particles may be passed to an adjacent (or further) region.
     * Called once per animation step
     */
    private void checkParticlesInRegion()
    {
        // make temporary list to iterate,
        // so we can transfer ownership of items
        for ( WaterParticle p : new ArrayList<WaterParticle>( particles ) )
        {
            if ( p.outOfRegion( this ) )
            {
                WaterRegionRenderer newRend =
                    p.rendererByPosition( waterAnimation );
                if ( newRend == null )
                { // no water here: accelerate fading
                    p.alphaStep = p.alphaStep - p.alphaStepMagnitude;
                    continue;
                }
                if ( isFull( newRend.x, newRend.y ) )
                {   // particle has moved to full cell: delete immediately
                    // block cells count as full too
                    particles.remove( p );
                    continue;
                }
                // transfer ownership to other renderer
                particles.remove( p );
                newRend.particles.add( p );
            }
        }
        // now particles are in the correct cell remove those that have
        // fallen below the water level.
        for ( WaterParticle p : new ArrayList<WaterParticle>( particles ) )
        {
            WaterRegionRenderer r = p.rendererByPosition( waterAnimation );
            if ( r == null )
            { // particle is not in a cell with a renderer:leave it
                continue;
            }
            // convert to nominal pixels (32 in a cell)
            int heightInCell = ( int )(
                ( 1.0f + Math.floor( p.y ) - p.y ) * 32.0f
            );
            if ( heightInCell < r.targetWaterHeight )
            { // below water level: delete immediately
                particles.remove( p );
            }
        }
    }

    /**
     * Updates particle position. Called once per animation step.
     */
    public void stepParticles()
    {
        // Some particles may have left the region: update this first.
        checkParticlesInRegion();

        for ( WaterParticle p : particles )
        {
            p.step();
            p.alpha += p.alphaStep;
            // If particle has finished waxing, start in waning.
            if ( p.alpha > p.maxAlpha )
            {
                p.alpha = p.maxAlpha;
                p.alphaStep = -p.alphaStepMagnitude;
            }
        }
        // fully waned particles are removed
        Iterator<WaterParticle> i = particles.listIterator();
        while ( i.hasNext() )
        {
            WaterParticle p = i.next();
            if ( p.alpha < 0 )
            {
                i.remove();
            }
        }
    }

    /**
     * Called once per animation step, after all heights set.
     * Checks if cell above has something in, and makes this cell
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
        return ( int )Math.sqrt( ( double )( 2 * area ) );
    }

    /**
     * Adds an upper vertex for the polygon for this region to the
     * supplied ArrayLists of coordinates. Supplied vertex is towards the
     * cell in the supplied direction.
     */
    public Vertex topVertex( CellPosition d )
    {
        // Local cell origin in nominal pixels.
        int xPixel = x * 32, yPixel = y * 32;
        if ( 0 == height )
        {
            switch ( d )
            {
                case TOP_LEFT:
                    return new Vertex( xPixel, yPixel + 32 );
                case TOP_MIDDLE:
                    return new Vertex( xPixel + 16, yPixel + 32 );
                case TOP_RIGHT:
                    return new Vertex( xPixel + 32, yPixel + 32 );
                default:
                    throw new RuntimeException(
                        "Can only add vertices for TOP_LEFT, " +
                            "TOP_MIDDLE or TOP_RIGHT cells." );
            }
        }
        Block block = world.getBlockAt( x, y );
        int boundaryHeight = calcBoundaryHeight( d );
        int xOffset, yOffset;

        switch ( d )
        {
            case TOP_LEFT:
                xOffset = 0;
                if ( shapeEquals( block, Block.Shape.UP_LEFT ) )
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
                if ( shapeEquals( block, Block.Shape.UP_LEFT ) ||
                    shapeEquals( block, Block.Shape.UP_RIGHT ) )
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
                if ( shapeEquals( block, Block.Shape.UP_RIGHT ) )
                {
                    yOffset = 0;
                }
                else
                {
                    yOffset = 32 - boundaryHeight;
                }
                break;
            default:
                throw new RuntimeException(
                    "Can only add vertices for LEFT or RIGHT cells." );
        }
        return new Vertex( xPixel + xOffset, yPixel + yOffset );
    }

    /**
     * For matching up heights in left/right cells.
     */
    public int adjacentRendererWaterHeight( CellularDirection d )
    {
        if ( getRegion().isConnected( d ) )
        {
            WaterRegionRenderer wrr = adjacentRenderer( d );
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
        if (
            cellPosition.leftRightness == null ||
                !getRegion().isConnected( cellPosition.leftRightness ) ||
                isFull( x, y )
        )
        { // The cell in that direction is not relevant
            return height;
        }
        WaterRegionRenderer adjWrr =
            adjacentRenderer( cellPosition.leftRightness );
        if ( null == adjWrr )
        { // Adjacent is probably empty, and this cell is probably a low level
            if ( getRegion().isConnected( cellPosition.leftRightness ) )
            {
                WaterRegion adjReg =
                    world.waterTable.getItemAt(
                        x + cellPosition.leftRightness.xOffset,
                        y + cellPosition.leftRightness.yOffset
                    );
                if (
                    adjReg != null &&
                        adjReg.isConnected(
                            CellularDirection.opposite(
                                cellPosition.leftRightness
                            )
                        )
                )
                {
                    return 0;
                }
            }
            return height;
        }
        else if ( isFull( adjWrr.x, adjWrr.y ) )
        {
            return adjWrr.height;
        }
        return ( height + adjWrr.height ) / 2;
    }

    public Vertex bottomVertex( CellPosition d )
    {
        // Local cell origin in nominal pixels.
        int xPixel = x * 32, yPixel = y * 32;
        int xOffset;
        Block block = world.getBlockAt( x, y );
        switch ( d )
        {
            case BOTTOM_LEFT:
                if ( shapeEquals( block, Block.Shape.UP_LEFT ) )
                {
                    xOffset = 32;
                }
                else
                {
                    xOffset = 0;
                }
                break;
            case BOTTOM_RIGHT:
                if ( shapeEquals( block, Block.Shape.UP_RIGHT ) )
                {
                    xOffset = 0;
                }
                else
                {
                    xOffset = 32;
                }
                break;
            default:
                throw new RuntimeException(
                    "Can only add bottom vertices for BOTTOM_LEFT or " +
                        "BOTTOM_RIGHT cells."
                );
        }
        return new Vertex( xPixel + xOffset, yPixel + 32 );
    }

    boolean isFalling()
    {
        if ( isSlopeNotBridge( world.getBlockAt( x, y ) ) )
        { // Non bridge slopes are not connected down.
            return false;
        }

        return !isFull( x, y + 1 );
    }

    boolean isFull( int x, int y )
    {
        Block b = world.getBlockAt( x, y );
        if ( s_isFlat( b ) )
        { // Flat block cells have zero capacity: always full.
            return true;
        }
        WaterRegionRenderer wrr =
            waterAnimation.lookupRenderer.getItemAt( x, y );
        if ( null == wrr )
        { // Not full of flat block, but no region yet, empty
            return false;
        }
        return 32 == wrr.height && 32 == wrr.targetWaterHeight;
    }

    /**
     * returns opacity for whole cell water background.
     * depends on contents and slope influence on capacity
     */
    public int backShadeAlpha()
    {
        Block b = world.getBlockAt( x, y );
        // slope cells are half full of block to start with
        int baselineContent = isSlopeNotBridge( b ) ?
            WaterUtil.HALF_CAPACITY : 0;
        int alpha = 255 * ( baselineContent + getRegion().getContents() ) /
            WaterUtil.MAX_CAPACITY;
        // divide by 4 to tame it, as particles and polygons should show
        // the same thing.
        return MathUtil.constrain( alpha, 0, 255 ) / 4;
    }

    public boolean hasPipe()
    {
        List<Thing> things = world.getThingsAt( x, y );
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
     * Returns the net flow across a cell's edge considering water flowing
     * in and out. A posotive value indicates net flow out. A negative
     * value indicates net flow in.
     */
    public int edgeNetFlow( CellularDirection edge )
    {
        WaterRegionRenderer adj = adjacentRenderer( edge );
        if ( null == adj )
        {
            return getRegion().getFlow( edge );
        }
        return getRegion().getFlow( edge ) -
            adj.getRegion().getFlow( CellularDirection.opposite( edge ) );
    }

    public Vertex netFlow()
    {
        float netX = ( float )(
            edgeNetFlow( CellularDirection.RIGHT ) -
                edgeNetFlow( CellularDirection.LEFT )
        );
        float netY = ( float )(
            edgeNetFlow( CellularDirection.DOWN ) -
                edgeNetFlow( CellularDirection.UP )
        );
        return new Vertex( netX, netY );
    }

    public Vector2D cellPosition()
    {
        return new Vector2D( x, y );
    }

    @Override
    public Position getPosition()
    {
        return new Position( x, y );
    }

    public void debugCellPrint( CellDebugPrint p )
    {
        Block b = world.getBlockAt( x, y );
        String s = null == b ? " " : "" + BlockRenderer.charForBlock( b );

        String connStr = "U" + bool01( getRegion().isConnected( UP ) ) + " " +
            "D" + bool01( getRegion().isConnected( DOWN ) ) + " " +
            "L" + bool01( getRegion().isConnected( LEFT ) ) + " " +
            "R" + bool01( getRegion().isConnected( RIGHT ) );

        p.addString( x, y, 0, s );

        p.addString( getRegion(), 1, "cont %04d", getRegion().getContents() );

        p.addString( getRegion(), 4, connStr );

        p.addString(
            getRegion(), 5,
            "falling" + bool01( isFalling() ) + " full" + isFull( x, y )
        );

        p.addString( getRegion(), 6, "height(targ) %02d(%02d)", height,
            targetWaterHeight );

        p.addString( getRegion(), 7, "(%d,%d)", x, y );
    }

    private String bool01( boolean b )
    {
        return b ? "1" : "0";
    }

    /**
     * Look up the water region for this renderer's coordinates.
     */
    public WaterRegion getRegion()
    {
        return world.waterTable.getItemAt( x, y );
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }
}

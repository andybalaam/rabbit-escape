package rabbitescape.render;


import java.util.Vector;

import rabbitescape.engine.BehaviourTools;
import rabbitescape.engine.Block;
import rabbitescape.engine.CellularDirection;
import static rabbitescape.engine.CellularDirection.*;
import rabbitescape.engine.WaterRegion;
import rabbitescape.engine.World;
import rabbitescape.engine.textworld.BlockRenderer;
import rabbitescape.engine.util.CellDebugPrint;
import rabbitescape.engine.util.LookupItem2D;
import rabbitescape.engine.util.Position;
import rabbitescape.render.gameloop.WaterDynamics;

public class WaterRegionRenderer implements LookupItem2D
{

    public static final int contentsPerParticle = 50;
    public static final int maxParticleCountChange = 2;
    private static final int maxHeightChange = 1;

    public static enum BounceSurface
    {
        VERTICAL,
        HORIZONTAL,
        LEFT_RISE,
        RIGHT_RISE
    }

    public WaterRegion region;

    private int targetWaterHeight = 0;
    private int height = 0;
    private int lastHeight = 0;

    public boolean drawnLT, drawnLB, drawnR;

    private final World world;
    private final WaterDynamics dynamics;

    public WaterRegionRenderer(WaterRegion region, World world, WaterDynamics dynamics )
    {
        this.region = region;
        this.world = world;
        this.dynamics = dynamics;
    }

    public void setWaterRegion( WaterRegion region )
    {
        this.region = region;
    }

    public WaterRegionRenderer adjacentRenderer( CellularDirection d )
    {
        return dynamics.lookupRenderer.getItemAt( region.x + d.xOffset, region.y + d.yOffset );
    }

    public boolean adjacentNull( CellularDirection d )
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
     */
    public void setTargetWaterHeight()
    {
        if ( isFalling() )
        {
            targetWaterHeight = 0;
            return;
        }
        Block block = world.getBlockAt( region.x, region.y );
        if ( null == block)
        {
            targetWaterHeight = region.getContents() / 32;
        }
        else if ( BehaviourTools.isSlope( block ) )
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
     * Called once per animation step
     */
    public void setWaterHeight()
    {
        if ( lastHeight + maxHeightChange < targetWaterHeight )
        {
            height = lastHeight + maxHeightChange;
            lastHeight = height;
            return;
        }
        if ( lastHeight - maxHeightChange > targetWaterHeight )
        {
            height = lastHeight - maxHeightChange;
            lastHeight = height;
            return;
        }
        height = targetWaterHeight;
        lastHeight = height;
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
     * supplied vectors of coordinates. Supplied vertex is towards the
     * cell in the supplied direction.
     */
    public void topVertex( Vector<Integer> retX, Vector<Integer> retY, CellularDirection d )
    {
        int x = region.x * 32, y = region.y * 32; // Local cell origin in nominal pixels.
        if ( 0 == height )
        {
            switch ( d )
            {
            case LEFT:
                retX.add( x );
                retY.add( y + 32 );
                return;
            case RIGHT:
                retX.add( x + 32 );
                retY.add( y + 32 );
                return;
            default:
                return;
            }
        }
        Block block = world.getBlockAt( region.x, region.y );
        int boundaryHeight = calcBoundaryHeight( d );
        //boolean eitherFalling = isFalling() || adjacentWaterIsFalling( d );
        //boundaryHeight = eitherFalling ? 0 : boundaryHeight; // If either is falling, then go with zero
        int xOffset;

        switch ( d )
        {
        case LEFT:
            if ( BehaviourTools.isLeftRiseSlope( block ) )
            {
                xOffset = 32 - boundaryHeight;
            }
            else
            {
                xOffset = 0;
            }
            break;
        case RIGHT:
            if ( BehaviourTools.isRightRiseSlope( block ) )
            {
                xOffset = boundaryHeight;
            }
            else
            {
                xOffset = 32;
            }
            break;
        default:
            return;
        }
        retX.add( x + xOffset );
        retY.add( y + 32 - boundaryHeight );
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

    private int calcBoundaryHeight( CellularDirection d )
    {
        if ( !region.isConnected( d ) )
        { // The cell in that direction is not relevant
            return height;
        }
        WaterRegionRenderer adjWrr = adjacentRenderer( d );
        if ( null == adjWrr )
        { // Adjacent is probably empty, and this cell is probably a low level
            return height;
        }
        int heightDefect = Math.abs( targetWaterHeight - height );
        int adjHeightDefect = Math.abs ( adjWrr.targetWaterHeight - adjWrr.height );
        if ( heightDefect > adjHeightDefect )
        {
            if ( heightDefect > maxHeightChange )
            {
                return height;
            }
        }
        else
        {
            if ( adjHeightDefect > maxHeightChange )
            {
                return adjWrr.height;
            }
        }
        int adjHeight = adjacentRendererWaterHeight( d );
        int boundaryHeight = adjHeight > height ?  adjHeight : height; // Use max
        return boundaryHeight;
    }

    public void bottomVertex( Vector<Integer> retX, Vector<Integer> retY, CellularDirection d )
    {
        int x = region.x * 32, y = region.y * 32; // Local cell origin in nominal pixels.
        int xOffset;
        Block block = world.getBlockAt( region.x, region.y );
        switch ( d )
        {
        case LEFT:
            if ( BehaviourTools.isLeftRiseSlope( block ) )
            {
                xOffset = 32;
            }
            else
            {
                xOffset = 0;
            }
            break;
        case RIGHT:
            if ( BehaviourTools.isRightRiseSlope( block ) )
            {
                xOffset = 0;
            }
            else
            {
                xOffset = 32;
            }
            break;
        default:
            return;
        }
        retX.add( x + xOffset );
        retY.add( y + 32 );
    }

    boolean isFalling()
    {
        if ( BehaviourTools.isSlopeNotBridge( world.getBlockAt( region.x, region.y ) ) )
        { // Non bridge slopes are not connected down.
            return false;
        }

        return !isFull( region.x, region.y + 1 );
    }

    boolean isFull( int x, int y )
    {
        Block b = world.getBlockAt( x, y );
        if ( BehaviourTools.s_isFlat( b ) )
        { // Flat block cells have zero capacity: always full.
            return true;
        }
        WaterRegionRenderer wrr = dynamics.lookupRenderer.getItemAt( x, y );
        if ( null == wrr )
        { // Not full of flat block, but no region yet, empty
            return false;
        }
        return 32 == wrr.height && 32 == wrr.targetWaterHeight;
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

        //p.addString( region, 2, "netflow%6s", calcNetFlow().toString() );

        //p.addString( region, 3, "watervel%6s", estimateVelocity( calcNetFlow() ).toString() );

        p.addString( region, 4, connStr  );

        p.addString( region, 5,
            "falling" + bool01( isFalling() ) + " full" + isFull( region.x, region.y )  );

        p.addString( region, 6, "height(targ) %02d(%02d)", height, targetWaterHeight );

        p.addString( region, 7, " LT" + bool01( drawnLT ) +
                                             " LB" + bool01( drawnLB ) +
                                              " R" + bool01( drawnR ) );
        p.addString(  region, 8, "(%d,%d)", region.x, region.y );
    }

    private String bool01( boolean b )
    {
        return b ? "1" : "0";
    }


}

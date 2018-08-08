package rabbitescape.render;

import rabbitescape.engine.Block;
import rabbitescape.engine.CellularDirection;
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

    public static final int contentsPerParticle = 50;
    public static final int maxParticleCountChange = 2;
    private static final int maxHeightChange = 1;

    public WaterRegion region;

    private int targetWaterHeight = 0;
    private int height = 0;
    private int lastHeight = 0;

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
     */
    public void setTargetWaterHeight()
    {
        if ( isFalling() )
        {
            targetWaterHeight = 0;
            return;
        }
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
     * Called once per animation step
     */
    public void setWaterHeight()
    {
        height = MathUtil.constrain( targetWaterHeight,
                                     lastHeight - maxHeightChange ,
                                     lastHeight + maxHeightChange );
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
        int xOffset;

        switch ( d )
        {
        case TOP_LEFT:
            if ( shapeEquals ( block, Block.Shape.UP_LEFT ) )
            {
                xOffset = 32 - boundaryHeight;
            }
            else
            {
                xOffset = 0;
            }
            break;
        case TOP_MIDDLE:
            if ( shapeEquals ( block, Block.Shape.UP_LEFT ) || shapeEquals ( block, Block.Shape.UP_RIGHT ) )
            {
                xOffset = ( 32 - boundaryHeight ) / 2;
            }
            else
            {
                xOffset = 16;
            }
            break;
        case TOP_RIGHT:
            if ( shapeEquals ( block, Block.Shape.UP_RIGHT ) )
            {
                xOffset = boundaryHeight;
            }
            else
            {
                xOffset = 32;
            }
            break;
        default:
            throw new RuntimeException( "Can only add vertices for LEFT or RIGHT cells." );
        }
        return new Vertex( x + xOffset, y + 32 - boundaryHeight );
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
        if ( cellPosition.leftRightness == null || !region.isConnected( cellPosition.leftRightness ) )
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

        p.addString(  region, 7, "(%d,%d)", region.x, region.y );
    }

    private String bool01( boolean b )
    {
        return b ? "1" : "0";
    }


}

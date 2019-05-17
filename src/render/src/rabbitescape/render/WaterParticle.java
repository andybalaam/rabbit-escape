package rabbitescape.render;

import rabbitescape.engine.CellularDirection;
import rabbitescape.engine.util.Position;
import rabbitescape.render.gameloop.WaterAnimation;

import java.util.Random;

public class WaterParticle
{

    private static final Random rand = new Random();
    /** Change in opacity per animation step.
     * alpha runs from 0 (invisible) to 255 (opaque).
     * the alphaStep used for this particle will be
     * maxAlpha/alphaStepNo.  */
    public static final int alphaStepNo = 16;
    /** Acceleration due to gravity in cells per animation step squared. */
    private final float gravity = 0.008f;
    /** Damping: fraction of velocity retained per animation step. */
    private final float damping = 0.95f;
    /** Converts flow into velocity */
    private final float flowFactor = 0.0002f;
    /** Half width of kite-shaped streak in nominal pixels (32 to a cell). */
    private final static float kite = 1.0f;

    /** Coordinates within world */
    public float x, y, lastX, lastY;
    /** Velocity in cells per animation step.*/
    private float vx, vy;

    /** The greatest alpha allowed for this particle. */
    public int maxAlpha;
    /** The size of the changes in alpha as this particle fades in or out */
    public int alphaStepMagnitude;
    /** This will be negative if the particle is currently fading. */
    public int alphaStep;
    /** the current value of alpha. */
    public int alpha;

    public WaterParticle(WaterRegionRenderer wrr)
    {
        // make a starting point in a cell
        boolean hasPipe = wrr.hasPipe();
        x = genPosInCell( hasPipe );
        y = genPosInCell( hasPipe );
        // create Coordinates in a part of the cell with the most flow
        if ( !hasPipe )
        {
            CellularDirection xBias = largeFlowMag( 
                    wrr, 
                    CellularDirection.LEFT,
                    CellularDirection.RIGHT 
                 );
            x = biasCoord( x, CellularDirection.LEFT, xBias);
            CellularDirection yBias = largeFlowMag( 
                wrr, 
                CellularDirection.UP,
                CellularDirection.DOWN 
            );
            y = biasCoord( y, CellularDirection.UP, yBias);
        }
        // move across the world to the correct cell
        Position p = wrr.getPosition();
        x += (float)p.x;
        y += (float)p.y;
        lastX = x; lastY = y;
        Vertex flow = wrr.netFlow();
        vx = genVelComponent( hasPipe ) + flow.x * flowFactor ;
        vy = genVelComponent( hasPipe ) + flow.y * flowFactor;
        // make each particle have a different alpha so a cloud of spray
        // has some texture
        maxAlpha = 100 + (int)( rand.nextFloat() * 150.0f );
        alphaStepMagnitude = maxAlpha / alphaStepNo;
        alphaStep = alphaStepMagnitude;
        alpha = alphaStepMagnitude;
    }

    private float genPosInCell( boolean hasPipe )
    {
        return hasPipe ?
               (rand.nextFloat() + 2f) / 5f : // middle fifth of cell
               rand.nextFloat() ;
    }

    private float genVelComponent( boolean hasPipe )
    {
        return hasPipe ?
               ( rand.nextFloat() - 0.5f ) / 4f : // sprays out more
               ( rand.nextFloat() - 0.5f ) / 16f ;
    }

    /**
     * Compares flow magnitude for the given directions: if one is
     * significantly larger than the other, it is returned. Returns null
     * if neither is much bigger.
     */
    private CellularDirection largeFlowMag(
        WaterRegionRenderer wrr,
        CellularDirection a,
        CellularDirection b
    )
    {
        int aMag = Math.abs(wrr.edgeNetFlow(a));
        int bMag = Math.abs(wrr.edgeNetFlow(b));
        if ( aMag > 2.0f * bMag )
        {
            return a;
        }
        if ( bMag > 2.0f * aMag )
        {
            return b;
        }
        return null;
    }

    private float biasCoord( 
        float coord, 
        CellularDirection lowerBiasDir,
        CellularDirection biasDir
    )
    {
        if ( biasDir == null )
        {
            return coord;
        }
        coord = coord / 2.0f;
        if ( biasDir == lowerBiasDir )
        {
            return coord;
        }
        return coord + 0.5f;
    }

    /**
     * Constructor for tests
     */
    public WaterParticle(float x, float y)
    {
        this.x = x; this.y = y;
    }

    public boolean outOfRegion( WaterRegionRenderer wrr)
    {
        Position p = wrr.getPosition();
        float rx = (float)p.x, ry = (float)p.y;
        return  x < rx || y < ry ||
                x >= ( rx + 1f ) || y >= ( ry + 1f) ;
    }

    /**
     * return position, scaled by f and offset. Also offset from
     * region's origin to global origin.
     */
    public static Vertex position( float x_, float y_,
                                   float tileSize, Vertex offset )
    {
        return new Vertex(offset.x + x_ * tileSize,
                          offset.y + y_ * tileSize);
    }

    /**
     * return the WaterRegionRenderer for this particle's position.
     * May return null if the particle has fallen (or drifted sideways)
     * to a cell with no renderer.
     */
     public WaterRegionRenderer rendererByPosition( WaterAnimation wa)
     {
        int cx = (int)Math.floor(x), cy = (int)Math.floor(y);
        try
        {
            return wa.lookupRenderer.getItemAt( cx, cy );
        }
        catch ( IndexOutOfBoundsException eIOOB)
        {
            return null;
        }
     }

     /**
      * create a polygon to represent a streak of water.
      */
     public PolygonBuilder polygon()
     {
        // multiply by 32 to convert to units of nominal pixels
        Vertex last = ( new Vertex( lastX, lastY ) ).multiply( 32.0f );
        Vertex here = ( new Vertex( x, y ) ).multiply( 32.0f );
        // vector in the direction of travel
        Vertex direction = here.subtract(last);
        // vector from here to the tip of the kite
        Vertex tip = direction.multiply( kite / direction.magnitude() );
        // vector from here to one side of the kite
        Vertex side = tip.rot90();

        PolygonBuilder p = new PolygonBuilder();
        p.add( last );
        p.add( here.add( side ) );
        p.add( here.add( tip ) );
        p.add( here.subtract( side ) );

        return p;
     }

    /**
     * Apply momentum and gravity and damping. Called once per animation step.
     */
    public void step()
    {
        // store previous position, so streaks can be drawn.
        lastX = x; lastY = y;
        // apply momentum
        x += vx; y += vy;
        // apply gravity
        vy += gravity;
        // apply damping to limit top speed
        vx *= damping; vy *= damping;
    }
}

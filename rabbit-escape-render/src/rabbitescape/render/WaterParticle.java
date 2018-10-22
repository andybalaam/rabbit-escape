package rabbitescape.render;

import java.util.Random;
import rabbitescape.engine.util.Position;
import rabbitescape.engine.WaterRegion;

public class WaterParticle
{

    private static final Random rand = new Random();
    /** Change in opacity per animation step. */
    public static final int alphaStepMagnitude = 32;
    /** Acceleration due to gravity in cells per animation step squared. */
    private final float gravity = 0.008f;
    /** Damping: fraction of velocity retained per animation step. */
    private final float damping = 0.95f;

    /** Coordinates within world */
    public float x, y, lastX, lastY;
    /** Velocity in cells per animation step.*/
    private float vx, vy;
    /** Particles fade in and out. 0-255. */
    public int alpha = alphaStepMagnitude;
    public int alphaStep = alphaStepMagnitude;
    /** schedule for deletion */
    public boolean delete = false;

    public WaterParticle(WaterRegionRenderer wrr)
    {
        Position p = wrr.region.getPosition();
        x = rand.nextFloat() + (float)p.x;
        y = rand.nextFloat() + (float)p.y;
        lastX = x; lastY = y;
        vx = ( rand.nextFloat() - 0.5f ) / 32f ;
        vy = ( rand.nextFloat() - 0.5f ) / 32f ;
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
        Position p = wrr.region.getPosition();
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

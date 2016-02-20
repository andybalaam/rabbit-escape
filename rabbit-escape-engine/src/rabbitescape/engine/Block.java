package rabbitescape.engine;

import static rabbitescape.engine.Direction.*;
import rabbitescape.engine.util.LookupItem2D;
import rabbitescape.engine.util.Position;
import rabbitescape.engine.util.WaterUtil;

public class Block implements LookupItem2D
{
    public enum Material
    {
        EARTH,
        METAL
    }

    public enum Shape
    {
        FLAT( 0 ),
        UP_RIGHT( WaterUtil.HALF_CAPACITY ),
        UP_LEFT( WaterUtil.HALF_CAPACITY ),
        BRIDGE_UP_RIGHT( WaterUtil.MAX_CAPACITY ),
        BRIDGE_UP_LEFT( WaterUtil.MAX_CAPACITY );

        public final int capacity;

        private Shape( int capacity )
        {
            this.capacity = capacity;
        }
    }

    public final int x;
    public final int y;
    public final Material material;
    public final Shape shape;
    public final int variant;

    public Block( int x, int y, Material material, Shape shape, int variant )
    {
        this.x = x;
        this.y = y;
        this.material = material;
        this.shape = shape;
        this.variant = variant;
    }

    public Direction riseDir()
    {
        switch ( shape )
        {
        case FLAT:
            return DOWN;

        case UP_RIGHT:
        case BRIDGE_UP_RIGHT:
            return RIGHT;

        case UP_LEFT:
        case BRIDGE_UP_LEFT:
            return LEFT;

        default:
            throw new RuntimeException( "Unknown block shape " + shape );
        }
    }

    @Override
    public Position getPosition()
    {
        return new Position( x, y );
    }
}

package rabbitescape.engine;

import static rabbitescape.engine.Direction.*;
import rabbitescape.engine.util.LookupItem2D;
import rabbitescape.engine.util.Position;

public class Block implements LookupItem2D
{
    public enum Type
    {
        solid_flat,
        solid_up_right,
        solid_up_left,
        bridge_up_right,
        bridge_up_left,
        metal_flat,
    }

    public final int x;
    public final int y;
    public final Type type;
    public final int variant;

    public Block( int x, int y, Type type, int variant )
    {
        this.x = x;
        this.y = y;
        this.type = type;
        this.variant = variant;
    }

    public Direction riseDir()
    {
        switch ( type )
        {
            case solid_flat:
                return DOWN;

            case solid_up_right:
            case bridge_up_right:
                return RIGHT;

            case solid_up_left:
            case bridge_up_left:
                return LEFT;

            default:
                throw new RuntimeException( "Unknown block type " + type );
        }
    }

    @Override
    public Position getPosition()
    {
        return new Position( x, y );
    }
}

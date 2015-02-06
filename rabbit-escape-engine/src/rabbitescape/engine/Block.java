package rabbitescape.engine;

import static rabbitescape.engine.Direction.*;

public class Block
{
    public enum Type
    {
        solid_flat,
        solid_up_right,
        solid_up_left,
        bridge_up_right,
        bridge_up_left,
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
}

package rabbitescape.engine.textworld;

import rabbitescape.engine.Block;

public class BlockRenderer
{
    public static void render( Chars chars, Iterable<Block> blocks )
    {
        for ( Block block : blocks )
        {
            chars.set( block.x, block.y, charForBlock( block ) );
        }
    }

    private static char charForBlock( Block block )
    {
        switch ( block.type )
        {
            case solid_flat:      return '#';
            case solid_up_right:  return '/';
            case solid_up_left:   return '\\';
            case bridge_up_right: return '(';
            case bridge_up_left:  return ')';
            default:
                throw new AssertionError(
                    "Unknown Block type: " + block.type );
        }
    }
}

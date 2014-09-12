package rabbitescape.engine.textworld;

import static rabbitescape.engine.Direction.*;

import java.util.List;

import rabbitescape.engine.Block;

public class BlockRenderer
{
    public static void render( char[][] chars, List<Block> blocks )
    {
        for ( Block block : blocks )
        {
            chars[ block.y ][ block.x ] = charForBlock( block );
        }
    }

    private static char charForBlock( Block block )
    {
        if ( block.riseDir == DOWN )
        {
            return '#';
        }
        if ( block.type == Block.Type.bridge )
        {
            if ( block.riseDir == RIGHT )
            {
                return '(';
            }
            if ( block.riseDir == LEFT )
            {
                return ')';
            }
        }
        if ( block.riseDir == RIGHT )
        {
            return '/';
        }
        if ( block.riseDir == LEFT )
        {
            return '\\';
        }
        else
        {
            throw new AssertionError(
                "Unknown Block type: " + block.getClass() );
        }
    }
}

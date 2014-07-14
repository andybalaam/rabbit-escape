package rabbitescape.engine;

import java.awt.Dimension;
import java.util.List;

public class World
{
    public final Dimension size;
    public final List<Block> blocks;
    public final List<Thing> things;

    public World( Dimension size, List<Block> blocks, List<Thing> things )
    {
        this.size = size;
        this.blocks = blocks;
        this.things = things;
    }

    public void step()
    {
        for ( Thing thing : things )
        {
            if ( thing.alive )
            {
                thing.step( this );
            }
        }
    }

    public ChangeDescription describeChanges()
    {
        ChangeDescription ret = new ChangeDescription();

        for ( Thing thing : things )
        {
            if ( thing.alive )
            {
                thing.describeChanges( this, ret );
            }
        }

        return ret;
    }

    public boolean squareBlockAt( int x, int y )
    {
        for ( Block block : blocks )
        {
            if ( block.x == x && block.y == y && block instanceof SquareBlock )
            {
                return true;
            }
        }
        return false;
    }

    public Block getBlockAt( int x, int y )
    {
        for ( Block block : blocks )
        {
            if ( block.x == x && block.y == y )
            {
                return block;
            }
        }
        return null;
    }
}

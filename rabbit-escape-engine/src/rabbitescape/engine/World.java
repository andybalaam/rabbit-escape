package rabbitescape.engine;

import static rabbitescape.engine.Direction.*;

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
        init();
    }

    private void init()
    {
        for ( Thing thing : things )
        {
            thing.init( this );
        }
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
                ret.add( thing.x, thing.y, thing.state );
            }
        }

        return ret;
    }

    public boolean flatBlockAt( int x, int y )
    {
        Block block = getBlockAt( x, y );
        return ( block != null && block.riseDir == DOWN );
    }

    public Block getBlockAt( int x, int y )
    {
        // TODO: faster
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

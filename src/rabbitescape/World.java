package rabbitescape;

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
            thing.step( this );
        }
    }
}

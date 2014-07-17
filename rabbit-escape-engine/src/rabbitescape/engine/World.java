package rabbitescape.engine;

import static rabbitescape.engine.Direction.*;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

public class World
{
    public final Dimension size;
    public final List<Block> blocks;
    public final List<Thing> things;
    public final String name;
    public final int numRabbits;
    public final int rabbitDelay;

    private final List<Thing> thingsToAdd;
    private final List<Thing> thingsToRemove;

    public int numSavedRabbits;

    public World(
        Dimension size,
        List<Block> blocks,
        List<Thing> things,
        String name,
        int numRabbits,
        int rabbitDelay
    )
    {
        this.size = size;
        this.blocks = blocks;
        this.things = things;
        this.name = name;
        this.numRabbits = numRabbits;
        this.rabbitDelay = rabbitDelay;

        thingsToAdd    = new ArrayList<Thing>();
        thingsToRemove = new ArrayList<Thing>();

        numSavedRabbits = 0;

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
        thingsToAdd.clear();
        thingsToRemove.clear();

        for ( Thing thing : things )
        {
            thing.step( this );
        }

        for ( Thing thing : thingsToAdd )
        {
            thing.init( this );
        }

        things.addAll( thingsToAdd );
        things.removeAll( thingsToRemove );
    }

    public ChangeDescription describeChanges()
    {
        ChangeDescription ret = new ChangeDescription();

        for ( Thing thing : things )
        {
            ret.add( thing.x, thing.y, thing.state );
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

    public void addThing( Thing thing )
    {
        thingsToAdd.add( thing );
    }

    public void saveRabbit( Rabbit rabbit )
    {
        ++numSavedRabbits;
        thingsToRemove.add( rabbit );
    }

    public void killRabbit( Rabbit rabbit )
    {
        thingsToRemove.add( rabbit );
    }
}

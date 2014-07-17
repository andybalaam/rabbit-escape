package rabbitescape.engine;

import static rabbitescape.engine.util.Util.*;
import static rabbitescape.engine.Direction.*;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

public class World
{
    public final Dimension size;
    public final List<Block> blocks;
    public final List<Rabbit> rabbits;
    public final List<Thing> things;
    public final String name;
    public final int numRabbits;
    public final int rabbitDelay;

    private final List<Rabbit> rabbitsToAdd;
    private final List<Rabbit> rabbitsToRemove;

    public int numSavedRabbits;

    public World(
        Dimension size,
        List<Block> blocks,
        List<Rabbit> rabbits,
        List<Thing> things,
        String name,
        int numRabbits,
        int rabbitDelay
    )
    {
        this.size = size;
        this.blocks = blocks;
        this.rabbits = rabbits;
        this.things = things;
        this.name = name;
        this.numRabbits = numRabbits;
        this.rabbitDelay = rabbitDelay;

        rabbitsToAdd    = new ArrayList<Rabbit>();
        rabbitsToRemove = new ArrayList<Rabbit>();

        numSavedRabbits = 0;

        init();
    }

    private void init()
    {
        for ( Thing thing : allThings() )
        {
            thing.init( this );
        }
    }

    public void step()
    {
        rabbitsToAdd.clear();
        rabbitsToRemove.clear();

        for ( Thing thing : allThings() )
        {
            thing.step( this );
        }

        // Add any new rabbits
        for ( Rabbit rabbit : rabbitsToAdd )
        {
            rabbit.init( this );
        }
        rabbits.addAll( rabbitsToAdd );

        // Remove any dead or saved ones
        rabbits.removeAll( rabbitsToRemove );
    }

    public ChangeDescription describeChanges()
    {
        ChangeDescription ret = new ChangeDescription();

        for ( Thing thing : allThings() )
        {
            ret.add( thing.x, thing.y, thing.state );
        }

        return ret;
    }

    private Iterable<Thing> allThings()
    {
        return chain( rabbits, things );
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

    public void addRabbit( Rabbit rabbit )
    {
        rabbitsToAdd.add( rabbit );
    }

    public void saveRabbit( Rabbit rabbit )
    {
        ++numSavedRabbits;
        rabbitsToRemove.add( rabbit );
    }

    public void killRabbit( Rabbit rabbit )
    {
        rabbitsToRemove.add( rabbit );
    }
}

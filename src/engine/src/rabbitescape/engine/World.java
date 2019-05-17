package rabbitescape.engine;

import static rabbitescape.engine.util.Util.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rabbitescape.engine.Rabbit.Type;
import rabbitescape.engine.WaterRegion;
import rabbitescape.engine.err.RabbitEscapeException;
import rabbitescape.engine.textworld.Comment;
import rabbitescape.engine.util.Dimension;
import rabbitescape.engine.util.LookupTable2D;
import rabbitescape.engine.util.Position;

public class World
{
    public static class DontStepAfterFinish extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;

        public final String worldName;

        public DontStepAfterFinish( String worldName )
        {
            this.worldName = worldName;
        }
    }

    public static class NoBlockFound extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;

        public final int x;
        public final int y;

        public NoBlockFound( int x, int y )
        {
            this.x = x;
            this.y = y;
        }
    }

    public static class UnableToAddToken extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;

        public final Token.Type ability;

        public UnableToAddToken( Token.Type ability )
        {
            this.ability = ability;
        }
    }

    public static class NoSuchAbilityInThisWorld extends UnableToAddToken
    {
        private static final long serialVersionUID = 1L;

        public NoSuchAbilityInThisWorld( Token.Type ability )
        {
            super( ability );
        }
    }

    public static class NoneOfThisAbilityLeft extends UnableToAddToken
    {
        private static final long serialVersionUID = 1L;

        public NoneOfThisAbilityLeft( Token.Type ability )
        {
            super( ability );
        }
    }

    public static class CantAddTokenOutsideWorld extends UnableToAddToken
    {
        private static final long serialVersionUID = 1L;

        public final int x;
        public final int y;
        public final Dimension worldSize;

        public CantAddTokenOutsideWorld(
            Token.Type ability, int x, int y, Dimension worldSize )
        {
            super( ability );
            this.x = x;
            this.y = y;
            this.worldSize = worldSize;
        }
    }

    public enum CompletionState
    {
        RUNNING,
        PAUSED,
        WON,
        LOST
    }

    public final Dimension size;
    public final LookupTable2D<Block> blockTable;
    /** A grid of water. Only one water object
     * should be stored in each location. */
    public final LookupTable2D<WaterRegion> waterTable;
    public final List<Rabbit> rabbits;
    public final List<Thing> things;
    public final Map<Token.Type, Integer> abilities;
    public final String name;
    public final String description;
    public final String author_name;
    public final String author_url;
    public final String[] hints;
    public final String[] solutions;
    public final Comment[] comments;
    public final int num_rabbits;
    public final int num_to_save;
    public final int[] rabbit_delay;

    private int rabbit_index_count;
    public int num_saved;
    public int num_killed;
    public int num_waiting;

    public boolean paused;

    public final WorldChanges changes;
    public final String music;
    public final VoidMarkerStyle.Style voidStyle;

    public World(
        Dimension size,
        List<Block> blocks,
        List<Rabbit> rabbits,
        List<Thing> things,
        Map<Position, Integer> waterAmounts,
        Map<Token.Type, Integer> abilities,
        String name,
        String description,
        String author_name,
        String author_url,
        String[] hints,
        String[] solutions,
        int num_rabbits,
        int num_to_save,
        int[] rabbit_delay,
        String music,
        int num_saved,
        int num_killed,
        int num_waiting,
        int rabbit_index_count,
        boolean paused,
        Comment[] comments,
        WorldStatsListener statsListener,
        VoidMarkerStyle.Style voidStyle
    )
    {
        this.size = size;
        this.rabbits = rabbits;
        this.things = things;
        this.abilities = abilities;
        this.name = name;
        this.description = description;
        this.author_name = author_name;
        this.author_url = author_url;
        this.hints = hints;
        this.solutions = solutions;
        this.num_rabbits = num_rabbits;
        this.num_to_save = num_to_save;
        this.rabbit_delay = rabbit_delay;
        this.music = music;
        this.num_saved = num_saved;
        this.num_killed = num_killed;
        this.num_waiting = num_waiting;
        this.rabbit_index_count = rabbit_index_count;
        this.paused = paused;
        this.comments = comments;
        this.voidStyle = voidStyle;

        if ( -1 == size.width )
        {
            // make allowance for tests with no world
            this.blockTable = null;
            this.waterTable = new LookupTable2D<WaterRegion>( size );
        }
        else
        {
            this.blockTable = new LookupTable2D<Block>( blocks, size );
            this.waterTable = WaterRegionFactory.generateWaterTable( blockTable,
                waterAmounts );
        }

        this.changes = new WorldChanges( this, statsListener );

        init();
    }

    public World(
        Dimension size,
        LookupTable2D<Block> blockTable,
        List<Rabbit> rabbits,
        List<Thing> things,
        LookupTable2D<WaterRegion> waterTable,
        Map<rabbitescape.engine.Token.Type, Integer> abilities,
        String name,
        String description,
        String author_name,
        String author_url,
        String[] hints,
        String[] solutions,
        int num_rabbits,
        int num_to_save,
        int[] rabbit_delay,
        String music,
        int num_saved,
        int num_killed,
        int num_waiting,
        int rabbit_index_count,
        boolean paused,
        Comment[] comments,
        IgnoreWorldStatsListener statsListener,
        VoidMarkerStyle.Style voidStyle )
    {
        this.size = size;
        this.blockTable = blockTable;
        this.rabbits = rabbits;
        this.things = things;
        this.waterTable = waterTable;
        this.abilities = abilities;
        this.name = name;
        this.description = description;
        this.author_name = author_name;
        this.author_url = author_url;
        this.hints = hints;
        this.solutions = solutions;
        this.num_rabbits = num_rabbits;
        this.num_to_save = num_to_save;
        this.rabbit_delay = rabbit_delay;
        this.music = music;
        this.num_saved = num_saved;
        this.num_killed = num_killed;
        this.num_waiting = num_waiting;
        this.rabbit_index_count = rabbit_index_count;
        this.paused = paused;
        this.comments = comments;
        this.voidStyle = voidStyle;

        this.changes = new WorldChanges( this, statsListener );

        init();
    }

    private void init()
    {
        // Number the rabbits if necessary
        for ( Rabbit r: rabbits )
        {
            rabbitIndex( r );
        }

        // Rearrange them, this may be necessary if they have been
        // restored from state.
        Collections.sort( rabbits );

        for ( Thing thing : allThings() )
        {
            thing.calcNewState( this );
        }
    }

    public void rabbitIndex( Rabbit r )
    {
        r.index = ( r.index == Rabbit.NOT_INDEXED )
                ? ++rabbit_index_count
                : r.index;
    }

    public int getRabbitIndexCount()
    {
        return rabbit_index_count;
    }

    /**
     * For levels with some rabbits in to start with.
     * Then entering rabbits are indexed correctly.
     */
    public void countRabbitsForIndex()
    {
        rabbit_index_count = rabbit_index_count == 0 ?
            rabbits.size() : rabbit_index_count;
        for ( Rabbit r:rabbits )
        {
            rabbit_index_count = rabbit_index_count > r.index ?
                rabbit_index_count : r.index;
        }
    }

    public void step()
    {

        if ( completionState() != CompletionState.RUNNING )
        {
            throw new DontStepAfterFinish( name );
        }

        for ( Thing thing : allThings() )
        {
            thing.step( this );
        }

        changes.rememberWhatWillHappen();

        changes.apply();

        for ( Thing thing : allThings() )
        {
            thing.calcNewState( this );
        }

        changes.blocksJustRemoved.clear();

        changes.apply();
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
        return chain( waterTable.getItems(), rabbits, things );
    }

    public Block getBlockAt( int x, int y)
    {
        if ( x <  0          || y <  0           ||
             x >= size.width || y >= size.height  )
        {
            return null;
        }
        return blockTable.getItemAt( x, y );
    }

    public CompletionState completionState()
    {
        if ( paused )
        {
            return CompletionState.PAUSED;
        }
        else if ( numRabbitsOut() == 0 && this.num_waiting <= 0 )
        {
            if ( num_saved >= num_to_save )
            {
                return CompletionState.WON;
            }
            else
            {
                return CompletionState.LOST;
            }
        }
        else
        {
            return CompletionState.RUNNING;
        }
    }

    public Token getTokenAt( int x, int y )
    {
        // Note it is not worth using LookupTable2D for things.
        // Handling their movement would complicate the code.
        // There are not as many instances of Thing as Block.
        // Iterating to check through is not too time
        // consuming.
        for ( Thing thing : things )
        {
            if ( thing.x == x && thing.y == y && thing instanceof Token )
            {
                if ( !changes.tokensToRemove.contains( thing ) )
                {
                    return (Token)thing;
                }
            }
        }
        return null;
    }

    public List<Thing> getThingsAt( int x, int y )
    {
        ArrayList<Thing> ret = new ArrayList<Thing>();
        for ( Thing thing : things )
        {
            if ( thing.x == x && thing.y == y )
            {
                if ( !changes.tokensToRemove.contains( thing ) &&
                     !changes.fireToRemove.contains( thing ) )
                {
                    ret.add( thing );
                }
            }
        }
        return ret;
    }

    public boolean fireAt( int x, int y )
    {
        // See note for getTokenAt() about Thing storage.
        for ( Thing thing : things )
        {
            if ( thing.x == x && thing.y == y && thing instanceof Fire )
            {
                if ( !changes.fireToRemove.contains( thing ) )
                {
                    return true;
                }
            }
        }
        return false;
    }

    public Rabbit[] getRabbitsAt( int x, int y )
    {
        List<Rabbit> ret = new ArrayList<Rabbit>();

        for ( Rabbit rabbit : rabbits )
        {
            if ( rabbit.x == x && rabbit.y == y )
            {
                ret.add( rabbit );
            }
        }

        return ret.toArray( new Rabbit[ret.size()] );
    }

    public int numRabbitsOut()
    {
        int count = 0;
        for ( Rabbit r : rabbits ) {
            if ( r.type == Type.RABBIT ) {
                ++count;
            }
        }
        return count;
    }

    public void setPaused( boolean paused )
    {
        this.paused = paused;
    }

    public void recalculateWaterRegions( Position point )
    {
        int contents = 0;
        for (WaterRegion waterRegion :
             waterTable.getItemsAt( point.x, point.y ))
        {
            contents += waterRegion.getContents();
        }
        waterTable.removeItemsAt( point.x, point.y );
        WaterRegionFactory.createWaterRegionsAtPoint(
            blockTable, 
            waterTable, 
            point.x, 
            point.y, 
            contents 
        );
    }

    public Map<Position, Integer> getWaterContents()
    {
        Map<Position, Integer> waterAmounts = new HashMap<>();
        for ( WaterRegion waterRegion : waterTable )
        {
            if ( waterAmounts.containsKey( waterRegion.getPosition() ) )
            {
                throw new IllegalStateException(
                    "There is currently no support for multiple WaterRegions "
                        + "within a single cell." );
            }
            int contents = waterRegion.getContents();
            if ( contents != 0 )
            {
                waterAmounts.put( waterRegion.getPosition(),
                    contents );
            }
        }
        return waterAmounts;
    }
}

package rabbitescape.engine;

import static rabbitescape.engine.util.Util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rabbitescape.engine.err.RabbitEscapeException;
import rabbitescape.engine.textworld.Comment;
import rabbitescape.engine.util.Dimension;
import rabbitescape.engine.util.LookupTable2D;

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
//    public final List<Block> blocks;
    public final LookupTable2D<Block> blockTable;
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
        this.paused = paused;
        this.comments = comments;
        this.voidStyle = voidStyle;

        if ( -1 == size.width )
        {
            this.blockTable = null; // make allowance for tests with no world
        }
        else
        {
            this.blockTable = new LookupTable2D<Block>( blocks, size );
        }

        this.changes = new WorldChanges( this, statsListener );

        init();
    }

    private void init()
    {
        for ( Thing thing : allThings() )
        {
            thing.calcNewState( this );
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
        return chain( rabbits, things );
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
        else if ( rabbits.size() == 0 && this.num_waiting <= 0 )
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
        // TODO: faster
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

    public boolean fireAt( int x, int y )
    {
        // TODO: faster with LookupTable2D. can do getTokenAt at the same time.
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
        return num_rabbits -
            ( this.num_waiting + num_killed + num_saved );
    }

    public void setPaused( boolean paused )
    {
        this.paused = paused;
    }
}

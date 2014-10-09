package rabbitescape.engine;

import static rabbitescape.engine.util.Util.*;
import static rabbitescape.engine.Block.Type.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import rabbitescape.engine.err.RabbitEscapeException;
import rabbitescape.engine.util.Dimension;

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

    public static class Changes
    {
        private final World world;

        private final List<Rabbit> rabbitsToAdd    = syncList();
        private final List<Rabbit> rabbitsToRemove = syncList();
        private final List<Thing>  thingsToAdd     = syncList();
        private final List<Thing>  thingsToRemove  = syncList();
        private final List<Block>  blocksToAdd     = syncList();
        private final List<Block>  blocksToRemove  = syncList();

        public Changes( World world )
        {
            this.world = world;
        }

        public void apply()
        {
            // Add any new things
            for ( Rabbit rabbit : rabbitsToAdd )
            {
                rabbit.calcNewState( world );
            }
            world.rabbits.addAll( rabbitsToAdd );
            world.things.addAll( thingsToAdd );
            world.blocks.addAll( blocksToAdd );

            // Remove dead/saved rabbits, used tokens, dug out blocks
            world.rabbits.removeAll( rabbitsToRemove );
            world.things.removeAll(  thingsToRemove );
            world.blocks.removeAll(  blocksToRemove );

            rabbitsToAdd.clear();
            rabbitsToRemove.clear();
            thingsToAdd.clear();
            thingsToRemove.clear();
            blocksToAdd.clear();
            blocksToRemove.clear();
        }

        private <T> List<T> syncList()
        {
            return Collections.synchronizedList( new ArrayList<T>() );
        }

        public void addBlock( Block block )
        {
            blocksToAdd.add( block );
        }

        public void killRabbit( Rabbit rabbit )
        {
            ++world.num_killed;
            rabbitsToRemove.add( rabbit );
        }

        public void enterRabbit( Rabbit rabbit )
        {
            --world.num_waiting;
            rabbitsToAdd.add( rabbit );
        }

        public void saveRabbit( Rabbit rabbit )
        {
            ++world.num_saved;
            rabbitsToRemove.add( rabbit );
        }


        public void addToken( int x, int y, Token.Type type )
        throws UnableToAddToken
        {
            Integer numLeft = world.abilities.get( type );

            if ( numLeft == null )
            {
                throw new NoSuchAbilityInThisWorld( type );
            }

            if ( numLeft == 0 )
            {
                throw new NoneOfThisAbilityLeft( type );
            }

            thingsToAdd.add( new Token( x, y, type ) );
            world.abilities.put( type, numLeft - 1 );
        }

        public void removeThing( Thing thing )
        {
            thingsToRemove.add( thing );
        }

        public void removeBlockAt( int x, int y )
        {
            Block block = world.getBlockAt( x, y );
            if ( block == null )
            {
                throw new NoBlockFound( x, y );
            }
            blocksToRemove.add( block );
        }

        public List<Thing> tokensAboutToAppear()
        {
            return new ArrayList<Thing>( thingsToAdd );
        }

    }

    public final Dimension size;
    public final List<Block> blocks;
    public final List<Rabbit> rabbits;
    public final List<Thing> things;
    public final Map<Token.Type, Integer> abilities;
    public final String name;
    public final int num_rabbits;
    public final int num_to_save;
    public final int rabbit_delay;

    public int num_saved;
    public int num_killed;
    public int num_waiting;

    public final Changes changes;

    public World(
        Dimension size,
        List<Block> blocks,
        List<Rabbit> rabbits,
        List<Thing> things,
        Map<Token.Type, Integer> abilities,
        String name,
        int num_rabbits,
        int num_to_save,
        int rabbit_delay,
        int num_saved,
        int num_killed,
        int num_waiting
    )
    {
        this.size = size;
        this.blocks = blocks;
        this.rabbits = rabbits;
        this.things = things;
        this.abilities = abilities;
        this.name = name;
        this.num_rabbits = num_rabbits;
        this.num_to_save = num_to_save;
        this.rabbit_delay = rabbit_delay;
        this.num_saved = num_saved;
        this.num_killed = num_killed;
        this.num_waiting = num_waiting;

        this.changes = new Changes( this );

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
        if ( finished() )
        {
            throw new DontStepAfterFinish( name );
        }

        for ( Thing thing : allThings() )
        {
            thing.step(this);
        }

        changes.apply();

        for ( Thing thing : allThings() )
        {
            thing.calcNewState( this );
        }

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

    public boolean flatBlockAt( int x, int y )
    {
        Block block = getBlockAt( x, y );
        return ( block != null && block.type == solid_flat );
    }

    public boolean slopingBlockAt( int x, int y )
    {
        Block block = getBlockAt( x, y );
        return ( block != null && block.type != solid_flat );
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

    public boolean finished()
    {
        return ( rabbits.size() == 0 && this.num_waiting <= 0 );
    }

    public Token getTokenAt( int x, int y )
    {
        // TODO: faster
        for ( Thing thing : things )
        {
            if ( thing.x == x && thing.y == y && thing instanceof Token )
            {
                return (Token)thing;
            }
        }
        return null;
    }

    public boolean success()
    {
        return num_saved > num_to_save;
    }

    public int numRabbitsOut()
    {
        return num_rabbits -
            ( this.num_waiting + num_killed + num_saved );
    }
}

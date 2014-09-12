package rabbitescape.engine;

import static rabbitescape.engine.util.Util.*;
import static rabbitescape.engine.Direction.*;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rabbitescape.engine.err.RabbitEscapeException;

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
        private final List<Rabbit> rabbitsToAdd    = new ArrayList<>();
        private final List<Rabbit> rabbitsToRemove = new ArrayList<>();
        public final List<Thing>   thingsToAdd     = new ArrayList<>();
        private final List<Thing>  thingsToRemove  = new ArrayList<>();
        private final List<Block>  blocksToRemove  = new ArrayList<>();

        public void apply( World world )
        {
            // Add any new things
            for ( Rabbit rabbit : rabbitsToAdd )
            {
                rabbit.calcNewState( world );
            }
            world.rabbits.addAll( rabbitsToAdd );
            world.things.addAll( thingsToAdd );

            // Remove dead/saved rabbits, used tokens, dug out blocks
            world.rabbits.removeAll( rabbitsToRemove );
            world.things.removeAll(  thingsToRemove );
            world.blocks.removeAll(  blocksToRemove );

            rabbitsToAdd.clear();
            rabbitsToRemove.clear();
            thingsToAdd.clear();
            thingsToRemove.clear();
            blocksToRemove.clear();
        }
    }

    public final Dimension size;
    public final List<Block> blocks;
    public final List<Rabbit> rabbits;
    public final List<Thing> things;
    public final Map<Token.Type, Integer> abilities;
    public final String name;
    public final int numRabbits;
    public final int requiredNumSavedRabbits;
    public final int rabbitDelay;

    public final Changes changes;

    public int numSavedRabbits;
    public int numKilledRabbits;
    public int rabbitsStillToEnter;

    public World(
        Dimension size,
        List<Block> blocks,
        List<Rabbit> rabbits,
        List<Thing> things,
        Map<Token.Type, Integer> abilities,
        String name,
        int numRabbits,
        int rabbitDelay
    )
    {
        this.size = size;
        this.blocks = blocks;
        this.rabbits = rabbits;
        this.things = things;
        this.abilities = abilities;
        this.name = name;
        this.numRabbits = numRabbits;
        this.requiredNumSavedRabbits = 1;
        this.rabbitDelay = rabbitDelay;
        this.changes = new Changes();

        numSavedRabbits = 0;
        numKilledRabbits = 0;
        rabbitsStillToEnter = numRabbits;

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

        changes.apply( this );

        for ( Thing thing : allThings() )
        {
            thing.calcNewState( this );
        }

        changes.apply( this );
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
        changes.rabbitsToAdd.add( rabbit );
    }

    public void saveRabbit( Rabbit rabbit )
    {
        ++numSavedRabbits;
        changes.rabbitsToRemove.add( rabbit );
    }

    public void killRabbit( Rabbit rabbit )
    {
        ++numKilledRabbits;
        changes.rabbitsToRemove.add( rabbit );
    }

    public void addToken( int x, int y, Token.Type type )
    throws UnableToAddToken
    {
        Integer numLeft = abilities.get( type );

        if ( numLeft == null )
        {
            throw new NoSuchAbilityInThisWorld( type );
        }

        if ( numLeft == 0 )
        {
            throw new NoneOfThisAbilityLeft( type );
        }

        changes.thingsToAdd.add( new Token( x, y, type ) );
        abilities.put( type, numLeft - 1 );
    }

    public void removeThing( Thing thing )
    {
        changes.thingsToRemove.add( thing );
    }

    public void removeBlockAt( int x, int y )
    {
        Block block = getBlockAt( x, y );
        if ( block == null )
        {
            throw new NoBlockFound( x, y );
        }
        changes.blocksToRemove.add( block );
    }

    public boolean finished()
    {
        return ( rabbits.size() == 0 && rabbitsStillToEnter <= 0 );
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
        return numSavedRabbits > requiredNumSavedRabbits;
    }

    public int numRabbitsOut()
    {
        return numRabbits -
            ( rabbitsStillToEnter + numKilledRabbits + numSavedRabbits );
    }
}

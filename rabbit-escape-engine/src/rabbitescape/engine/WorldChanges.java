package rabbitescape.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rabbitescape.engine.World.NoBlockFound;
import rabbitescape.engine.World.NoSuchAbilityInThisWorld;
import rabbitescape.engine.World.NoneOfThisAbilityLeft;
import rabbitescape.engine.World.UnableToAddToken;

public class WorldChanges
{
    private final World world;

    private final List<Rabbit> rabbitsToAdd    = syncList();
    private final List<Rabbit> rabbitsToRemove = syncList();
    private final List<Thing>  thingsToAdd     = syncList();
    private final List<Thing>  thingsToRemove  = syncList();
    private final List<Block>  blocksToAdd     = syncList();
    private final List<Block>  blocksToRemove  = syncList();

    public WorldChanges( World world )
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

package rabbitescape.engine.solution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import rabbitescape.engine.Entrance;
import rabbitescape.engine.Exit;
import rabbitescape.engine.IgnoreWorldStatsListener;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.Thing;
import rabbitescape.engine.Token;
import rabbitescape.engine.World;

/**
 * A completely sandboxed game that can be edited and have solutions run against
 * without affecting the real game.
 */
public class SandboxGame
{
    /**
     * The token type that is currently 'selected' by whatever is interacting
     * with the sandbox.
     */
    private Token.Type selectedType = null;
    /** The world object that is contained in the game. */
    private final World world;

    /**
     * Create a sandbox game based on a given world. This allows playing with
     * the sandbox game without affecting the world in any way.
     *
     * @param world
     *            The world to copy.
     */
    public SandboxGame( World world )
    {
        List<Rabbit> clonedRabbits = makeClonedRabbits( world.rabbits );
        List<Thing> clonedThings = makeClonedThings( world.things );
        this.world = new World( world.size,
            world.blockTable.getListCopy(),
            clonedRabbits,
            clonedThings,
            new HashMap<>( world.abilities ),
            world.name,
            world.description,
            world.author_name,
            world.author_url,
            world.hint1,
            world.hint2,
            world.hint3,
            Arrays.copyOf( world.solutions, world.solutions.length ),
            world.num_rabbits,
            world.num_to_save,
            world.rabbit_delay,
            world.music,
            world.num_saved,
            world.num_killed,
            world.num_waiting,
            world.paused,
            new IgnoreWorldStatsListener() );
    }

    /**
     * Create a clone of the list of a given list of things.
     *
     * @param things
     *            The things to clone.
     * @return The cloned list of things.
     */
    private List<Thing> makeClonedThings( List<Thing> things )
    {
        List<Thing> clonedThings = new ArrayList<>();
        for ( Thing thing : things )
        {
            if ( thing instanceof Entrance )
            {
                clonedThings.add( new Entrance( thing.x, thing.y ) );
            }
            else if ( thing instanceof Exit )
            {
                clonedThings.add( new Exit( thing.x, thing.y ) );
            }
            else if ( thing instanceof Rabbit )
            {
                Rabbit rabbit = (Rabbit)thing;
                clonedThings.add( cloneRabbit( rabbit ) );
            }
            else if ( thing instanceof Token )
            {
                Token token = (Token)thing;
                clonedThings.add( new Token( token.x, token.y, token.type ) );
            }
            else
            {
                // We've created a new type of Thing, but haven't updated the
                // code here to cope with it.
                throw new IllegalStateException( "Unrecognised type of Thing: "
                    + thing );
            }
        }
        return clonedThings;
    }

    /**
     * Make a clone of a list of rabbits.
     *
     * @param rabbits
     *            The list of rabbits to clone.
     * @return The cloned list.
     */
    private List<Rabbit> makeClonedRabbits( List<Rabbit> rabbits )
    {
        List<Rabbit> clonedRabbits = new ArrayList<>();
        for ( Rabbit rabbit : rabbits )
        {
            clonedRabbits.add( cloneRabbit( rabbit ) );
        }
        return clonedRabbits;
    }

    /**
     * Clone a single rabbit.
     *
     * @param rabbit
     *            The rabbit to be cloned.
     * @return The cloned rabbit.
     */
    private Rabbit cloneRabbit( Rabbit rabbit )
    {
        return new Rabbit( rabbit.x, rabbit.y, rabbit.dir );
    }

    /**
     * Get the currently selected token type.
     *
     * @return The token type selected.
     */
    public Token.Type getSelectedType()
    {
        return selectedType;
    }

    /**
     * Select a token type to place next.
     *
     * @param selectedType
     *            The type to select.
     */
    public void setSelectedType( Token.Type selectedType )
    {
        this.selectedType = selectedType;
    }

    /**
     * Get the current world.
     *
     * @return The sandboxed world.
     */
    public World getWorld()
    {
        return world;
    }
}

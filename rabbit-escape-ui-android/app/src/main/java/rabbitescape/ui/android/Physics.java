package rabbitescape.ui.android;

import rabbitescape.engine.LevelWinListener;
import rabbitescape.engine.Token;
import rabbitescape.engine.World;

public class Physics
{
    /**
     * Everything that modifies the world goes through here, with
     * synchronization.
     */
    private static class WorldModifier
    {
        private final World world;

        public WorldModifier( World world )
        {
            this.world = world;
        }

        public synchronized void step()
        {
            world.step();
        }

        public synchronized void addToken( int x, int y, Token.Type type )
        {
            world.changes.addToken( x, y, type );
        }
    }

    public int frame;
    public final World world;
    private final LevelWinListener winListener;
    private final WorldModifier worldModifier;

    public Physics( World world, LevelWinListener winListener )
    {
        this.frame = 0;
        this.world = world;
        this.winListener = winListener;
        this.worldModifier = new WorldModifier( world );
    }

    public void step()
    {
        ++frame;

        if ( frame == 10 )
        {
            frame = 0;
            worldModifier.step();
            checkWon();
        }
    }

    private void checkWon()
    {
        if ( world.completionState() == World.CompletionState.WON )
        {
            winListener.won();
        }
    }

    public boolean finished()
    {
        return world.completionState() != World.CompletionState.RUNNING;
    }


    public int addToken( Token.Type ability, int tileX, int tileY )
    {
        if ( world.abilities.get( ability ) > 0 )
        {
            worldModifier.addToken( tileX, tileY, ability );
        }

        return world.abilities.get( ability );
    }
}

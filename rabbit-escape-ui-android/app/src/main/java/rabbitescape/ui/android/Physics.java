package rabbitescape.ui.android;

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
    private final WorldModifier worldModifier;

    public Physics( World world )
    {
        this.frame = 0;
        this.world = world;
        this.worldModifier = new WorldModifier( world );
    }

    public void step()
    {
        ++frame;

        if ( frame == 10 )
        {
            frame = 0;
            worldModifier.step();
        }
    }

    public boolean finished()
    {
        return world.finished();
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

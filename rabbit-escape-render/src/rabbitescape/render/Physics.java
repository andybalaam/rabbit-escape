package rabbitescape.render;

import rabbitescape.engine.LevelWinListener;
import rabbitescape.engine.Token;
import rabbitescape.engine.World;

public class Physics
{
    /**
     * Everything that modifies the world goes through here, with
     * synchronization.
     *
     * Public for test
     */
    public static class WorldModifier
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

        public synchronized void addToken(
            int tileX, int tileY, Token.Type type )
        {
            world.changes.addToken( tileX, tileY, type );
        }
    }

    private final World world;
    private final LevelWinListener winListener;
    private final WorldModifier worldModifier;

    public Physics( World world, LevelWinListener winListener )
    {
        this.world = world;
        this.winListener = winListener;
        this.worldModifier = new Physics.WorldModifier( world );
    }

    public void step()
    {
        worldModifier.step();
        checkWon();
    }

    private void checkWon()
    {
        switch ( world.completionState() )
        {
            case WON:
            {
                winListener.won();
                break;
            }
            case LOST:
            {
                winListener.lost();
                break;
            }
            default:
            {
                break;
            }
        }
    }

    public int addToken( int tileX, int tileY, Token.Type ability )
    {
        if ( world.abilities.get( ability ) > 0 )
        {
            worldModifier.addToken( tileX, tileY, ability );
        }

        return world.abilities.get( ability );
    }
}

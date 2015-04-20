package rabbitescape.render;

import java.util.ArrayList;
import java.util.List;

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

    public static interface StatsChangedListener
    {
        void changed( int waiting, int out, int saved );
    }

    private final World world;
    private final WorldModifier worldModifier;
    private final LevelWinListener winListener;
    private final List<StatsChangedListener> statsListeners;

    public Physics( World world, LevelWinListener winListener )
    {
        this.world = world;
        this.worldModifier = new WorldModifier( world );
        this.winListener = winListener;
        this.statsListeners = new ArrayList<>();
    }

    public boolean gameRunning()
    {
        return ( world.completionState() == World.CompletionState.RUNNING );
    }

    public void step()
    {
        worldModifier.step();
        checkWon();
        notifyStatsListeners();
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

    private void notifyStatsListeners()
    {
        for ( StatsChangedListener listener : statsListeners )
        {
            listener.changed(
                world.num_waiting,
                world.numRabbitsOut(),
                world.num_saved
            );
        }
    }

    public int addToken( int tileX, int tileY, Token.Type ability )
    {
        if (
               gameRunning()
            && tileX >= 0
            && tileX < world.size.width
            && tileY >= 0
            && tileY < world.size.height
            && world.abilities.get( ability ) > 0
        )
        {
            worldModifier.addToken( tileX, tileY, ability );
        }

        return world.abilities.get( ability );
    }

    public void addStatsChangedListener( StatsChangedListener listener )
    {
        statsListeners.add( listener );
    }
}

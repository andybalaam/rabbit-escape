package rabbitescape.ui.android;

import rabbitescape.engine.World;
import rabbitescape.engine.textworld.TextWorldManip;

/**
 * Hairy synchronization of the game loop, and the UI/system thread to
 * ensure the world saves correctly.
 *
 * Only the game loop can stop what it is doing to save the world, but
 * it is the UI/system thread that asks for the saved world.
 */
public class WorldSaver
{
    public static class Signal
    {
        public synchronized void listen( long wait_time )
        {
            try
            {
                wait( wait_time );
            }
            catch ( InterruptedException e )
            {
                // We have been notified that what we are waiting for
                // might have arrived
            }
        }

        public synchronized void speak()
        {
            notify();
        }
    }

    private final World world;
    private final AndroidGameLaunch gameLaunch;

    private final Signal requestSave = new Signal();
    private final Signal saved = new Signal();
    private boolean saveWorld = false;
    public String[] savedWorld = null;

    public WorldSaver( World world, AndroidGameLaunch gameLaunch )
    {
        this.world = world;
        this.gameLaunch = gameLaunch;
    }

    /**
     * Game loop thread: checks whether we've been asked
     * to save the world, and if so, blocks here while it does it.
     */
    public void check()
    {
        if ( saveWorld )
        {
            saveWorld = false;
            savedWorld = actuallySaveWorld();
            saved.speak();
        }
    }

    private String[] actuallySaveWorld()
    {
        world.changes.revert();
        return TextWorldManip.renderCompleteWorld( world, true );
    }

    /**
     * Game loop thread: pauses the time supplied, but will
     * be interrupted if the UI/system thread wakes us up
     * by calling calls waitUntilSaved().
     */
    public void waitUnlessSaveSignal( long wait_time )
    {
        requestSave.listen( wait_time );
    }

    /**
     * UI/system thread: requests a world save, wakes up the
     * game loop if it is sleeping, then blocks until the game
     * loop has saved the world, which it does by calling check().
     *
     * @return the saved model
     */
    public String[] waitUntilSaved()
    {
        if ( !gameLaunch.isRunning() )
        {
            return actuallySaveWorld();
        }

        saveWorld = true;
        requestSave.speak();
        while ( savedWorld == null )
        {
            saved.listen( 0 );
        }

        String[] ret = savedWorld;
        savedWorld = null;
        return ret;
    }
}

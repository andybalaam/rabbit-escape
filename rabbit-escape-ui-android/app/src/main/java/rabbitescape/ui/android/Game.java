package rabbitescape.ui.android;

import android.os.Bundle;
import android.view.SurfaceHolder;

import rabbitescape.engine.LevelWinListener;
import rabbitescape.engine.World;
import rabbitescape.render.BitmapCache;

public class Game
{
    // Saved state (saved by GameSurfaceView)
    public final AndroidGameLaunch gameLaunch;

    // Transient state
    private final Thread thread;

    public Game(
        SurfaceHolder surfaceHolder,
        BitmapCache<AndroidBitmap> bitmapCache,
        World world,
        LevelWinListener winListener,
        float displayDensity,
        Bundle savedInstanceState
    )
    {
        gameLaunch = new AndroidGameLaunch(
            surfaceHolder, bitmapCache, world, winListener, displayDensity, savedInstanceState );

        thread = new Thread( gameLaunch, "GameLaunch" );
    }

    public void start()
    {
        thread.start();
    }

    public void stop()
    {
        gameLaunch.pleaseStop();
        try
        {
            thread.join();
        }
        catch ( InterruptedException e )
        {
            // Should never happen
            e.printStackTrace();
        }
    }
}

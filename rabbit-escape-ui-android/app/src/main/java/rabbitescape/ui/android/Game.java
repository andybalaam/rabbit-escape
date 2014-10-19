package rabbitescape.ui.android;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.SurfaceHolder;

import rabbitescape.engine.LevelWinListener;
import rabbitescape.engine.World;
import rabbitescape.render.BitmapCache;

public class Game
{
    // Saved state (saved by GameSurfaceView)
    public final AndroidGameLoop gameLoop;

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
        gameLoop = new AndroidGameLoop(
            surfaceHolder, bitmapCache, world, winListener, displayDensity, savedInstanceState );

        thread = new Thread( gameLoop, "GameLoop" );
    }

    public void start()
    {
        thread.start();
    }

    public void stop()
    {
        gameLoop.pleaseStop();
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

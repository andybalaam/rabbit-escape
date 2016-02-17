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
    public final AndroidGameLaunch gameLaunch;

    // Transient state
    private final Thread thread;

    public Game(
        BitmapCache<AndroidBitmap> bitmapCache,
        Resources resources,
        World world,
        LevelWinListener winListener,
        Bundle savedInstanceState
    )
    {
        gameLaunch = new AndroidGameLaunch(
            bitmapCache,
            resources,
            world,
            winListener,
            savedInstanceState
        );

        thread = new Thread( gameLaunch, "GameLaunch" );
    }

    public void start( SurfaceHolder surfaceHolder )
    {
        gameLaunch.graphics.surfaceHolder = surfaceHolder;
        thread.start();
    }

    public void stop()
    {
        gameLaunch.stopAndDispose();
        try
        {
            thread.join();
        }
        catch ( InterruptedException e )
        {
            // Should never happen
            e.printStackTrace();
        }
        gameLaunch.graphics.surfaceHolder = null;
    }
}

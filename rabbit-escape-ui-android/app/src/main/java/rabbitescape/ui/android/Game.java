package rabbitescape.ui.android;

import android.content.res.Resources;
import android.view.SurfaceHolder;

import rabbitescape.engine.World;

public class Game
{
    private final Thread thread;
    public final AndroidGameLoop gameLoop;

    public Game( SurfaceHolder surfaceHolder, Resources resources, World world )
    {
        gameLoop = new AndroidGameLoop( surfaceHolder, resources, world );
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

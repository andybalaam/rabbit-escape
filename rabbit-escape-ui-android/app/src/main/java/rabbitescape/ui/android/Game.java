package rabbitescape.ui.android;

import android.content.res.Resources;
import android.view.SurfaceHolder;

public class Game
{
    private final Thread thread;
    public final GameLoop gameLoop;

    public Game( SurfaceHolder surfaceHolder, Resources resources )
    {
        gameLoop = new GameLoop( surfaceHolder, resources );
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

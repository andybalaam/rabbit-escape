package rabbitescape.ui.android;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.Date;

import rabbitescape.engine.World;
import rabbitescape.render.BitmapCache;

public class AndroidGameLoop implements Runnable
{
    private static final long max_allowed_skips = 10;
    private static final long simulation_time_step_ms = 70;
    private static final long frame_time_ms = 70;

    private final SurfaceHolder surfaceHolder;
    private final Physics physics;
    private final Graphics graphics;

    private int scrollX;
    private int scrollY;
    private boolean running;
    public boolean paused;
    private int screenWidthPixels;
    private int screenHeightPixels;

    public AndroidGameLoop(
        SurfaceHolder surfaceHolder, BitmapCache<AndroidBitmap> bitmapCache, World world )
    {
        this.surfaceHolder = surfaceHolder;
        this.physics = new Physics( world );
        this.graphics = new Graphics( bitmapCache, world );

        this.scrollX = 0;
        this.scrollY = 0;
        this.screenWidthPixels = 100;
        this.screenHeightPixels = 100;
        this.running = true;
        this.paused = false;
    }

    @Override
    public void run()
    {
        long simulation_time = new Date().getTime();
        long frame_start_time = simulation_time;

        while( running )
        {
            processInput();
            simulation_time = doPhysics( simulation_time, frame_start_time );
            drawGraphics();
            frame_start_time = waitForNextFrame( frame_start_time );

            if ( paused )
            {
                pause();

                // Reset the times to stop us thinking we've really lagged
                simulation_time = new Date().getTime();
                frame_start_time = simulation_time;
            }

            if ( physics.finished() )
            {
                running = false;
                Log.i( "artific", "Finished" );
            }
        }
    }

    private void pause()
    {
        int prevScrollX = scrollX;
        int prevScrollY = scrollY;

        while ( paused )
        {
            try
            {
                Thread.sleep( 100 );
            }
            catch ( InterruptedException e )
            {
                // Ignore - no need to do anything if interrupted
            }

            if ( prevScrollX != scrollX || prevScrollY != scrollY )
            {
                drawGraphics();
                prevScrollX = scrollX;
                prevScrollY = scrollY;
            }
        }
    }

    private long waitForNextFrame( long frame_start_time )
    {
        long next_frame_start_time = new Date().getTime();

        long how_long_we_took = next_frame_start_time - frame_start_time;
        long wait_time = frame_time_ms - how_long_we_took;

        if ( wait_time > 0 )
        {
            try
            {
                Thread.sleep( wait_time );
            }
            catch ( InterruptedException e )
            {
                // Should never happen
                e.printStackTrace();
            }
        }

        return next_frame_start_time;
    }

    private void drawGraphics()
    {
        Canvas canvas = surfaceHolder.lockCanvas();

        if ( canvas == null )
        {
            return;
        }

        try
        {
            synchronized ( surfaceHolder )
            {
                actuallyDrawGraphics( canvas );
            }
        }
        finally
        {
            surfaceHolder.unlockCanvasAndPost( canvas );
        }
    }

    private void actuallyDrawGraphics( Canvas canvas )
    {
        screenWidthPixels = canvas.getWidth();
        screenHeightPixels = canvas.getHeight();

        graphics.draw( canvas, -scrollX, -scrollY, physics.frame );
    }

    private long doPhysics( long simulation_time, long frame_start_frame )
    {
        for ( int skipped = 0; skipped < max_allowed_skips; ++skipped )
        {
            if ( simulation_time >= frame_start_frame )
            {
                break;
            }

            physics.step();

            simulation_time += simulation_time_step_ms;
        }

        return simulation_time;
    }

    private void processInput()
    {
    }

    public void pleaseStop()
    {
        running = false;
    }

    public void scrollBy( float x, float y )
    {
        scrollX += x;
        scrollY += y;

        if ( scrollX < 0 || graphics.levelWidthPixels < screenWidthPixels )
        {
            scrollX = 0;
        }
        else if ( scrollX > graphics.levelWidthPixels - screenWidthPixels )
        {
            scrollX = graphics.levelWidthPixels - screenWidthPixels;
        }

        if ( scrollY < 0 || graphics.levelHeightPixels < screenHeightPixels )
        {
            scrollY = 0;
        }
        else if ( scrollY > graphics.levelHeightPixels - screenHeightPixels )
        {
            scrollY = graphics.levelHeightPixels - screenHeightPixels;
        }
    }
}

package rabbitescape.ui.android;

import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.Date;

import rabbitescape.engine.Token;
import rabbitescape.engine.World;
import rabbitescape.render.BitmapCache;

public class AndroidGameLoop implements Runnable
{
    // Constants
    private static final java.lang.String STATE_PAUSED   = "rabbitescape.paused";
    private static final java.lang.String STATE_SCROLL_X = "rabbitescape.scrollx";
    private static final java.lang.String STATE_SCROLL_Y = "rabbitescape.scrolly";
    private static final long max_allowed_skips = 10;
    private static final long simulation_time_step_ms = 70;
    private static final long frame_time_ms = 70;

    // Saved state
    public boolean paused;
    private int scrollX;
    private int scrollY;

    // Transient state
    private final SurfaceHolder surfaceHolder;
    private final Physics physics;
    private final Graphics graphics;
    private boolean running;
    private int screenWidthPixels;
    private int screenHeightPixels;

    public AndroidGameLoop(
        SurfaceHolder surfaceHolder,
        BitmapCache<AndroidBitmap> bitmapCache,
        World world,
        float displayDensity,
        Bundle savedInstanceState
    )
    {
        this.surfaceHolder = surfaceHolder;
        this.physics = new Physics( world );
        this.graphics = new Graphics( bitmapCache, world, displayDensity );

        if ( savedInstanceState != null )
        {
            this.paused = savedInstanceState.getBoolean( STATE_PAUSED, false );
            this.scrollX = savedInstanceState.getInt( STATE_SCROLL_X, 0 );
            this.scrollY = savedInstanceState.getInt( STATE_SCROLL_Y, 0);
            scrollBy( 0, 0 ); // Ensure we are not off the edge
        }
        else
        {
            this.paused = false;
            this.scrollX = 0;
            this.scrollY = 0;
        }

        this.screenWidthPixels = 100;
        this.screenHeightPixels = 100;
        this.running = true;
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

        while ( paused && running )
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

    public int addToken( Token.Type ability, float pixelX, float pixelY )
    {
        if ( paused )
        {
            return physics.world.abilities.get( ability );
        }

        return physics.addToken(
            ability,
            (int)( ( pixelX + scrollX ) / graphics.renderingTileSize ),
            (int)( ( pixelY + scrollY ) / graphics.renderingTileSize )
    );
    }

    public void onSaveInstanceState( Bundle outState )
    {
        outState.putBoolean( STATE_PAUSED, paused );
        outState.putInt( STATE_SCROLL_X, scrollX );
        outState.putInt( STATE_SCROLL_Y, scrollY );
    }
}

package rabbitescape.ui.android;

import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.Date;

import rabbitescape.engine.LevelWinListener;
import rabbitescape.engine.Token;
import rabbitescape.engine.World;
import rabbitescape.render.BitmapCache;

import static android.text.TextUtils.join;

public class AndroidGameLoop implements Runnable
{
    // Constants
    public  static final String STATE_WORLD    = "rabbitescape.world";
    private static final String STATE_SCROLL_X = "rabbitescape.scrollx";
    private static final String STATE_SCROLL_Y = "rabbitescape.scrolly";
    private static final long max_allowed_skips = 10;
    private static final long simulation_time_step_ms = 70;
    private static final long frame_time_ms = 70;

    // Saved state
    private int scrollX;
    private int scrollY;

    // Transient state
    private final SurfaceHolder surfaceHolder;
    public final Physics physics;
    private final Graphics graphics;
    private boolean running;
    private int screenWidthPixels;
    private int screenHeightPixels;
    private boolean checkScroll;

    public final WorldSaver worldSaver;

    public AndroidGameLoop(
        SurfaceHolder surfaceHolder,
        BitmapCache<AndroidBitmap> bitmapCache,
        World world,
        LevelWinListener winListener,
        float displayDensity,
        Bundle savedInstanceState
    )
    {
        this.surfaceHolder = surfaceHolder;
        this.physics = new Physics( world, winListener );
        this.graphics = new Graphics( bitmapCache, world, displayDensity );

        this.checkScroll = true;

        if ( savedInstanceState != null )
        {
            this.scrollX = savedInstanceState.getInt( STATE_SCROLL_X, 0 );
            this.scrollY = savedInstanceState.getInt( STATE_SCROLL_Y, 0);

        }
        else
        {
            this.scrollX = 0;
            this.scrollY = 0;
        }

        this.screenWidthPixels = 100;
        this.screenHeightPixels = 100;
        this.running = true;
        this.worldSaver = new WorldSaver( world, this );
    }

    @Override
    public void run()
    {
        long simulation_time = new Date().getTime();
        long frame_start_time = simulation_time;

        while( running )
        {
            processInput();
            worldSaver.check();
            simulation_time = doPhysics( simulation_time, frame_start_time );
            drawGraphics();
            frame_start_time = waitForNextFrame( frame_start_time );

            if ( !gameRunning() )
            {
                pause();

                // Reset the times to stop us thinking we've really lagged
                simulation_time = new Date().getTime();
                frame_start_time = simulation_time;
            }

            if ( physics.finished() )
            {
                running = false;
            }
        }
    }

    private void pause()
    {
        int prevScrollX = scrollX;
        int prevScrollY = scrollY;

        while ( !gameRunning() && running )
        {
            worldSaver.waitUnlessSaveSignal( 100 );
            worldSaver.check();

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
            worldSaver.waitUnlessSaveSignal( wait_time );
        }
        worldSaver.check();

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
        if ( checkScroll )
        {
            scrollBy( 0, 0 );
            checkScroll = false;
        }

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

        if ( graphics.levelWidthPixels < screenWidthPixels )
        {
            scrollX = -( screenWidthPixels - graphics.levelWidthPixels ) / 2;
        }
        else if ( scrollX < 0  )
        {
            scrollX = 0;
        }
        else if ( scrollX > graphics.levelWidthPixels - screenWidthPixels )
        {
            scrollX = graphics.levelWidthPixels - screenWidthPixels;
        }

        if ( graphics.levelHeightPixels < screenHeightPixels )
        {
            scrollY = -( screenHeightPixels - graphics.levelHeightPixels ) / 2;
        }
        else if ( scrollY < 0 )
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
        if ( !gameRunning() )
        {
            return physics.world.abilities.get( ability );
        }

        int x = (int)( ( pixelX + scrollX ) / graphics.renderingTileSize );
        int y = (int)( ( pixelY + scrollY ) / graphics.renderingTileSize );

        if ( x < 0 || x >= physics.world.size.width || y < 0 || y >= physics.world.size.height )
        {
            return physics.world.abilities.get( ability );
        }

        return physics.addToken( ability,x, y );
    }

    public void setPaused( boolean paused )
    {
        physics.world.paused = paused;
    }

    public boolean paused()
    {
        return physics.world.paused;
    }

    public boolean gameRunning()
    {
        return ( physics.world.completionState() == World.CompletionState.RUNNING );
    }

    public void onSaveInstanceState( Bundle outState )
    {
        outState.putString( STATE_WORLD, join( "\n", worldSaver.waitUntilSaved() ) );
        outState.putInt( STATE_SCROLL_X, scrollX );
        outState.putInt( STATE_SCROLL_Y, scrollY );
    }

    public boolean isRunning()
    {
        return running;
    }
}

package rabbitescape.ui.android;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;

import java.util.Date;
import java.util.Random;

import rabbitescape.engine.World;
import rabbitescape.render.Renderer;
import rabbitescape.render.Sprite;

public class AndroidGameLoop implements Runnable
{
    private final int num_balls = 20;
    private final long max_allowed_skips = 10;
    private final long simulation_time_step_ms = 5;
    private final long frame_time_ms = 20;
    private final SurfaceHolder surfaceHolder;
    private int scrollX;
    private int scrollY;

    private boolean running = true;
    private final Ball[] balls;
    private final AndroidPaint paint;
    private Bitmap ballBitmap;
    private static int levelWidthPixels = 1000;
    private static int levelHeightPixels = 500;
    private int screenWidthPixels;
    private int screenHeightPixels;

    public AndroidGameLoop( SurfaceHolder surfaceHolder, Resources resources, World world )
    {
        this.surfaceHolder = surfaceHolder;
        this.scrollX = 0;
        this.scrollY = 0;
        this.screenWidthPixels = 100;
        this.screenHeightPixels = 100;

        Random rand = new Random();

        this.balls = new Ball[num_balls];
        for (int i = 0; i < num_balls; ++i)
        {
            balls[i] = new Ball( rand );
        }

        paint = new AndroidPaint( new Paint() );

        ballBitmap = BitmapFactory.decodeResource( resources, R.drawable.ball );
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

        AndroidCanvas androidCanvas = new AndroidCanvas( canvas );
        Renderer renderer = new Renderer( scrollX, scrollY, 32 );
        int frameNum = 1;
        //animator =
        Sprite[] sprites = new Sprite[]
        {
            new Sprite(
                new AndroidBitmap( ballBitmap ),
                new AndroidBitmapScaler(),
                1,
                1,
                32,
                0,
                0
            )
        };

        canvas.drawColor( Color.WHITE );

        //renderer.render( androidCanvas, animator.getSprites( frameNum ), paint );
        renderer.render( androidCanvas, sprites, paint );
    }

    private long doPhysics( long simulation_time, long frame_start_frame )
    {
        for ( int skipped = 0; skipped < max_allowed_skips; ++skipped )
        {
            if ( simulation_time >= frame_start_frame )
            {
                break;
            }

            moveBalls();
            simulation_time += simulation_time_step_ms;
        }

        return simulation_time;
    }

    private void moveBalls()
    {
        for ( Ball ball : balls )
        {
            ball.step();
        }
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

        if ( scrollX < 0 )
        {
            scrollX = 0;
        }
        else if ( scrollX > levelWidthPixels - screenWidthPixels )
        {
            scrollX = levelWidthPixels - screenWidthPixels;
        }

        if ( scrollY < 0 )
        {
            scrollY = 0;
        }
        else if ( scrollY > levelHeightPixels - screenHeightPixels )
        {
            scrollY = levelHeightPixels - screenHeightPixels;
        }
    }
}

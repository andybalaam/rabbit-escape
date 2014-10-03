package rabbitescape.ui.android;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import rabbitescape.engine.World;
import rabbitescape.render.BitmapCache;
import rabbitescape.render.GameLoop;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback
{
    private final BitmapCache<AndroidBitmap> bitmapCache;
    private final World world;
    private Game game;
    private float curX;
    private float curY;
    public float velX;
    public float velY;
    private Flinger flinger;

    public GameSurfaceView( Context context, BitmapCache<AndroidBitmap> bitmapCache, World world )
    {
        super( context );
        this.bitmapCache = bitmapCache;
        this.world = world;
        game = null;
        curX = 0;
        curY = 0;
        velX = 0;
        velY = 0;
        flinger = null;

        getHolder().addCallback( this );
    }

    @Override
    public void surfaceCreated( SurfaceHolder surfaceHolder )
    {
        game = new Game( surfaceHolder, bitmapCache, world );
        game.start();
    }

    @Override
    public void surfaceChanged( SurfaceHolder surfaceHolder, int i, int i2, int i3 )
    {
    }

    @Override
    public boolean onTouchEvent( MotionEvent event )
    {
        if ( event.getAction() == MotionEvent.ACTION_DOWN )
        {
            cancelFlinger();

            curX = event.getX();
            curY = event.getY();

            return true;
        }
        else if ( event.getAction() == MotionEvent.ACTION_MOVE )
        {
            cancelFlinger();

            velX = curX - event.getX();
            velY = curY - event.getY();
            curX = event.getX();
            curY = event.getY();

            doScroll();

            return true;
        }
        else if( event.getAction() == MotionEvent.ACTION_UP )
        {
            cancelFlinger();

            flinger = new Flinger( this );
            flinger.start();

            return true;
        }
        else
        {
            return false;
        }
    }

    private void cancelFlinger()
    {
        if ( flinger != null )
        {
            flinger.pleaseStop();
            flinger = null;
        }
    }

    public void doScroll()
    {
        game.gameLoop.scrollBy( velX, velY );
    }

    @Override
    public void surfaceDestroyed( SurfaceHolder surfaceHolder )
    {
        if ( game != null )
        {
            game.stop();
        }
        game = null;
    }

    public boolean togglePaused()
    {
        if ( game != null )
        {
            AndroidGameLoop gameLoop = game.gameLoop;
            gameLoop.paused = !gameLoop.paused;
            return gameLoop.paused;
        }
        else
        {
            return false;
        }
    }
}

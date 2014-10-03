package rabbitescape.ui.android;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import rabbitescape.engine.Token;
import rabbitescape.engine.World;
import rabbitescape.render.BitmapCache;
import rabbitescape.render.GameLoop;

public class GameSurfaceView extends SurfaceView
    implements
        SurfaceHolder.Callback,
        View.OnClickListener
{
    private final BitmapCache<AndroidBitmap> bitmapCache;
    private final World world;
    public Game game;
    public Scrolling scrolling;
    private Token.Type chosenAbility;

    public GameSurfaceView( Context context, BitmapCache<AndroidBitmap> bitmapCache, World world )
    {
        super( context );
        this.bitmapCache = bitmapCache;
        this.world = world;
        game = null;
        scrolling = new Scrolling( this );
        chosenAbility = null;

        getHolder().addCallback( this );
    }

    @Override
    public void surfaceCreated( SurfaceHolder surfaceHolder )
    {
        game = new Game( surfaceHolder, bitmapCache, world );
        game.start();

        setOnClickListener( this );
    }

    @Override
    public void surfaceChanged( SurfaceHolder surfaceHolder, int i, int i2, int i3 )
    {
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

    @Override
    public void onClick( View view )
    {
        if ( chosenAbility == null )
        {
            return;
        }

        int numLeft = game.gameLoop.addToken( chosenAbility, scrolling.curX, scrolling.curY );

        if ( numLeft == 0 )
        {
            //TODO disable button
        }
    }

    @Override
    public boolean onTouchEvent( MotionEvent event )
    {
        return scrolling.onTouchEvent( event );
    }

    public void chooseAbility( Token.Type ability )
    {
        chosenAbility = ability;
    }
}

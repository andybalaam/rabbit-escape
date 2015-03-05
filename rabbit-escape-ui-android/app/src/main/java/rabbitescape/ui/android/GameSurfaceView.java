package rabbitescape.ui.android;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewConfiguration;

import java.util.Map;

import rabbitescape.engine.LevelWinListener;
import rabbitescape.engine.Token;
import rabbitescape.engine.World;
import rabbitescape.render.BitmapCache;
import rabbitescape.render.GameLoop;

public class GameSurfaceView extends SurfaceView
    implements
        SurfaceHolder.Callback,
        View.OnClickListener
{
    // Saved state (saved by AndroidGameActivity)
    public final World world;
    private Token.Type chosenAbility;

    // Saved state
    public Game game;

    // Transient state
    private final NumLeftListener numLeftListener;
    private final BitmapCache<AndroidBitmap> bitmapCache;
    private final LevelWinListener winListener;
    private final float displayDensity;
    private final Bundle savedInstanceState;
    private final Scrolling scrolling;

    public GameSurfaceView(
        Context context,
        NumLeftListener numLeftListener,
        BitmapCache<AndroidBitmap> bitmapCache,
        World world,
        LevelWinListener winListener,
        float displayDensity,
        Bundle savedInstanceState
    )
    {
        super( context );
        this.numLeftListener = numLeftListener;
        this.bitmapCache = bitmapCache;
        this.world = world;
        this.winListener = winListener;
        this.displayDensity = displayDensity;
        this.savedInstanceState = savedInstanceState;

        game = null;
        scrolling = new Scrolling( this, ViewConfiguration.get( context ).getScaledTouchSlop() );
        chosenAbility = null;

        getHolder().addCallback( this );
    }

    @Override
    public void surfaceCreated( SurfaceHolder surfaceHolder )
    {
        game = new Game(
            surfaceHolder, bitmapCache, world, winListener, displayDensity, savedInstanceState );

        game.start();

        setOnClickListener( this );

        for ( Map.Entry<Token.Type, Integer> e : world.abilities.entrySet() )
        {
            numLeftListener.numLeft( e.getKey(), e.getValue() );
        }
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
            gameLoop.setPaused( !gameLoop.paused() );
            return gameLoop.paused();
        }
        else
        {
            return false;
        }
    }

    @Override
    public void onClick( View view )
    {
        if ( game == null || chosenAbility == null )
        {
            return;
        }

        int numLeft = game.gameLoop.addToken( chosenAbility, scrolling.curX, scrolling.curY );

        numLeftListener.numLeft( chosenAbility, numLeft );
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

    public void onSaveInstanceState( Bundle outState )
    {
        if ( game != null )
        {
            game.gameLoop.onSaveInstanceState( outState );
        }
    }
}

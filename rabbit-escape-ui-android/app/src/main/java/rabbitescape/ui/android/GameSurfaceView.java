package rabbitescape.ui.android;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewConfiguration;

import java.util.Map;

import rabbitescape.engine.LevelWinListener;
import rabbitescape.engine.Token;
import rabbitescape.engine.World;
import rabbitescape.render.BitmapCache;
import rabbitescape.ui.android.sound.AndroidSound;

public class GameSurfaceView extends SurfaceView
    implements
        SurfaceHolder.Callback,
        View.OnClickListener
{
    // Saved state (saved by AndroidGameActivity)
    public final World world;
    private OnScaleListener scaleGestureListener;
    private ScaleGestureDetector scaleGestureDetector;
    private Token.Type chosenAbility;

    // Saved state
    public Game game;

    // Transient state
    private final NumLeftListener numLeftListener;
    private final AndroidSound sound;
    private final BitmapCache<AndroidBitmap> bitmapCache;
    private final LevelWinListener winListener;
    private final float displayDensity;
    private final Bundle savedInstanceState;
    private final Scrolling scrolling;


    public GameSurfaceView(
        Context context,
        NumLeftListener numLeftListener,
        AndroidSound sound,
        BitmapCache<AndroidBitmap> bitmapCache,
        World world,
        LevelWinListener winListener,
        float displayDensity,
        Bundle savedInstanceState
    )
    {
        super( context );
        this.numLeftListener = numLeftListener;
        this.sound = sound;
        this.bitmapCache = bitmapCache;
        this.world = world;
        this.winListener = winListener;
        this.displayDensity = displayDensity;
        this.savedInstanceState = savedInstanceState;

        game = null;
        scaleGestureListener = null;
        scaleGestureDetector = null;

        scrolling = new Scrolling( this, ViewConfiguration.get( context ).getScaledTouchSlop() );
        chosenAbility = null;

        getHolder().addCallback( this );
    }

    @Override
    public void surfaceCreated( SurfaceHolder surfaceHolder )
    {
        game = new Game(
            surfaceHolder,
            bitmapCache,
            getResources(),
            world,
            winListener,
            displayDensity,
            savedInstanceState
        );

        game.start();

        setOnClickListener( this );
        this.scaleGestureListener = new OnScaleListener( game.gameLaunch.graphics );
        this.scaleGestureDetector = new ScaleGestureDetector(
            getContext(), this.scaleGestureListener );

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
            AndroidGameLaunch gameLaunch = game.gameLaunch;
            gameLaunch.setPaused( !gameLaunch.paused() );
            return gameLaunch.paused();
        }
        else
        {
            return false;
        }
    }
    
    public boolean toggleSpeed()
    {
        if ( game != null )
        {
            AndroidGameLaunch gameLaunch = game.gameLaunch;
            boolean fast = gameLaunch.toggleSpeed();
            return fast;
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

        int prev = world.abilities.get( chosenAbility );
        int numLeft = game.gameLaunch.addToken( chosenAbility, scrolling.curX, scrolling.curY );

        if ( numLeft != prev )
        {
            sound.playSound( "place_token" );
        }

        numLeftListener.numLeft( chosenAbility, numLeft );
    }

    @Override
    public boolean onTouchEvent( MotionEvent event )
    {
        boolean ret = scaleGestureDetector.onTouchEvent( event );

        ret = scrolling.onTouchEvent( event ) || ret;

        return ret || super.onTouchEvent( event );
    }

    public void chooseAbility( Token.Type ability )
    {
        chosenAbility = ability;
    }

    public void onSaveInstanceState( Bundle outState )
    {
        if ( game != null )
        {
            game.gameLaunch.onSaveInstanceState( outState );
        }
    }
}

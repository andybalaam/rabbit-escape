package rabbitescape.ui.android;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewConfiguration;

import java.util.Map;

import rabbitescape.engine.Token;
import rabbitescape.engine.World;
import rabbitescape.engine.config.TapTimer;
import rabbitescape.ui.android.sound.AndroidSound;

public class GameSurfaceView extends SurfaceView
    implements
        SurfaceHolder.Callback,
        View.OnClickListener
{
    // Saved state (saved by AndroidGameActivity)
    private OnScaleListener scaleGestureListener;
    private ScaleGestureDetector scaleGestureDetector;

    // Transient state
    private final NumLeftListener numLeftListener;
    private final AndroidSound sound;
    private final Scrolling scrolling;

    public final Game game;
    private final World world;

    public GameSurfaceView(
        Context context,
        NumLeftListener numLeftListener,
        AndroidSound sound,
        Game game,
        World world
    )
    {
        super( context );
        this.numLeftListener = numLeftListener;
        this.sound = sound;
        this.game = game;
        this.world = world;

        this.scaleGestureListener = null;
        this.scaleGestureDetector = null;

        this.scrolling = new Scrolling( this, ViewConfiguration.get( context ).getScaledTouchSlop() );

        getHolder().addCallback( this );

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
    public void surfaceCreated( SurfaceHolder surfaceHolder )
    {
        game.start( surfaceHolder );
    }

    @Override
    public void surfaceChanged( SurfaceHolder surfaceHolder, int format, int width, int height )
    {
    }

    @Override
    public void surfaceDestroyed( SurfaceHolder surfaceHolder )
    {
        game.stop();
    }

    public boolean togglePaused()
    {
        AndroidGameLaunch gameLaunch = game.gameLaunch;
        gameLaunch.setPaused( !gameLaunch.paused() );
        return gameLaunch.paused();
    }

    public boolean toggleSpeed()
    {
        AndroidGameLaunch gameLaunch = game.gameLaunch;
        return gameLaunch.toggleSpeed();
    }

    @Override
    public void onClick( View view )
    {
        // Check for cheat mode enabling
        TapTimer.newTap();

        if ( game.gameLaunch.chosenAbility == null )
        {
            return;
        }

        int prev = world.abilities.get( game.gameLaunch.chosenAbility );

        int numLeft = game.gameLaunch.addToken(
            game.gameLaunch.chosenAbility, scrolling.curX, scrolling.curY );

        if ( numLeft != prev )
        {
            sound.playSound( "place_token" );
        }

        numLeftListener.numLeft( game.gameLaunch.chosenAbility, numLeft );
    }

    @Override
    public boolean onTouchEvent( MotionEvent event )
    {
        boolean ret = scaleGestureDetector.onTouchEvent( event );

        ret = scrolling.onTouchEvent( event ) || ret;

        return ret || super.onTouchEvent( event );
    }
}

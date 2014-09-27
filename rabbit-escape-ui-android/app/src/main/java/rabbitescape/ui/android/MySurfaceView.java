package rabbitescape.ui.android;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback
{
    private final Resources resources;
    private Game game;
    private float curX;
    private float curY;

    public MySurfaceView( Context context, Resources resources )
    {
        super( context );
        this.resources = resources;
        game = null;

        getHolder().addCallback( this );
    }

    @Override
    public void surfaceCreated( SurfaceHolder surfaceHolder )
    {
        game = new Game( surfaceHolder,resources );
        game.start();
    }

    @Override
    public void surfaceChanged( SurfaceHolder surfaceHolder, int i, int i2, int i3 )
    {
    }

    @Override
    public boolean onTouchEvent( MotionEvent event )
    {
        Log.i( "artific", event.toString() );

        if ( event.getAction() == MotionEvent.ACTION_DOWN )
        {
            curX = event.getX();
            curY = event.getY();
            return false;
        }
        else if ( event.getAction() == MotionEvent.ACTION_MOVE )
        {
            Log.i( "artific", event.toString() );
            game.gameLoop.scrollBy( curX - event.getX(), curY - event.getY() );

            curX = event.getX();
            curY = event.getY();
            return true;
        }
        else
        {
            return false;
        }
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
}

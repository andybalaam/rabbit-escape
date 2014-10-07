package rabbitescape.ui.android;

import android.content.res.Resources;
import android.view.MotionEvent;

public class Scrolling
{
    public float curX = 0;
    public float curY = 0;
    public float velX = 0;
    public float velY = 0;

    private float origX = 0;
    private float origY = 0;

    private final GameSurfaceView view;
    private final float squaredTouchSlop;

    private Flinger flinger = null;

    public Scrolling( GameSurfaceView view, float touchSlop )
    {
        this.view = view;
        this.squaredTouchSlop = touchSlop * touchSlop;
    }

    public boolean onTouchEvent( MotionEvent event )
    {
        if ( event.getAction() == MotionEvent.ACTION_DOWN )
        {
            cancelFlinger();

            curX = event.getX();
            curY = event.getY();
            origX = curX;
            origY = curY;
        }
        else if ( event.getAction() == MotionEvent.ACTION_MOVE )
        {
            cancelFlinger();

            velX = curX - event.getX();
            velY = curY - event.getY();
            curX = event.getX();
            curY = event.getY();

            doScroll();
        }
        else if( event.getAction() == MotionEvent.ACTION_UP )
        {
            cancelFlinger();

            curX = event.getX();
            curY = event.getY();

            if ( squaredDistance( origX, origY, curX, curY ) < squaredTouchSlop )
            {
                view.performClick();
            }
            else
            {
                flinger = new Flinger( this );
                flinger.start();
            }
        }

        return true;
    }

    private float squaredDistance( float origX, float origY, float curX, float curY )
    {
        float x = curX - origX;
        float y = curY - origY;
        return ( x * x ) + ( y * y );
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
        if ( view.game != null )
        {
            view.game.gameLoop.scrollBy( velX, velY );
        }
    }

}

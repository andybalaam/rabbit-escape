package rabbitescape.ui.android;

public class Flinger implements Runnable
{
    private static final float slow_factor   = 0.95f;
    private static final float min_vel       = 3f;
    private static final int   pause_time_ms = 10;

    private final GameSurfaceView surfaceView;
    private boolean running;

    public Flinger( GameSurfaceView surfaceView )
    {
        this.surfaceView = surfaceView;
        this.running = true;
    }

    public void pleaseStop()
    {
        running = false;
    }

    @Override
    public void run()
    {
        while( running )
        {
            surfaceView.velX *= slow_factor;
            surfaceView.velY *= slow_factor;

            if ( Math.abs( surfaceView.velX ) < min_vel && Math.abs( surfaceView.velY ) < min_vel )
            {
                running = false;
                break;
            }

            surfaceView.doScroll();

            try
            {
                Thread.sleep( pause_time_ms );
            }
            catch ( InterruptedException e )
            {
                // Should never happen
            }
        }
    }

    public void start()
    {
        new Thread( this, "Flinger" ).start();
    }
}

package rabbitescape.ui.android;

public class Flinger implements Runnable
{
    private static final float slow_factor   = 0.95f;
    private static final float min_vel       = 3f;
    private static final int   pause_time_ms = 10;

    private final Scrolling scrolling;
    private boolean running;

    public Flinger( Scrolling scrolling )
    {
        this.scrolling = scrolling;
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
            scrolling.velX *= slow_factor;
            scrolling.velY *= slow_factor;

            if ( Math.abs( scrolling.velX ) < min_vel && Math.abs( scrolling.velY ) < min_vel )
            {
                running = false;
                break;
            }

            scrolling.doScroll();

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

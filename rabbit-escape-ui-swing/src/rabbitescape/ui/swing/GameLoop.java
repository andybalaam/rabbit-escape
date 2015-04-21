package rabbitescape.ui.swing;

import rabbitescape.render.Physics;

public class GameLoop
{
    private static final int framesPerStep = 10;

    private boolean running;

    private final Physics physics;
    private final Graphics graphics;

    public GameLoop( Physics physics, Graphics graphics )
    {
        this.physics = physics;
        this.graphics = graphics;
        this.running = true;
    }

    public void run()
    {
        while( running )
        {
            if ( physics.gameRunning() )
            {
                physics.step();
            }

            int f = 0;
            while ( running && f < framesPerStep )
            {
                sleep( 50 );

                graphics.step( f );

                if ( physics.gameRunning() )
                {
                    // If not paused, go on to the next frame
                    ++f;
                }
                else
                {
                    // Slow the frame rate when paused
                    sleep( 500 );
                }
            }
        }
    }

    public void stop()
    {
        running = false;
    }

    private void sleep( long millis )
    {
        try
        {
            Thread.sleep( millis );
        }
        catch ( InterruptedException ignored )
        {
        }
    }
}

package rabbitescape.ui.swing;

import rabbitescape.render.gameloop.Graphics;
import rabbitescape.render.gameloop.Input;
import rabbitescape.render.gameloop.Physics;

public class LegacyGameLoop
{
    private static final int framesPerStep = 10;

    private boolean running;

    private final Input input;
    private final Physics physics;
    private final Graphics graphics;

    public LegacyGameLoop( Input input, Physics physics, Graphics graphics )
    {
        this.input = input;
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
                physics.step( 70, 70 );
            }

            int f = 0;
            while ( running && f < framesPerStep )
            {
                input.waitMs( 50 );

                graphics.draw( f );

                if ( physics.gameRunning() )
                {
                    // If not paused, go on to the next frame
                    ++f;
                }
                else
                {
                    // Slow the frame rate when paused
                    input.waitMs( 500 );
                }
            }
        }
    }

    public void stop()
    {
        running = false;
    }
}

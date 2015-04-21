package rabbitescape.ui.android;

import java.util.Date;

public class GameLoop
{
    private static final long frame_time_ms = 70;

    private final Input input;
    private final Physics physics;
    private final Graphics graphics;
    public boolean running;

    public GameLoop( Input input, Physics physics, Graphics graphics )
    {
        this.input = input;
        this.physics = physics;
        this.graphics = graphics;
        this.running = true;
    }

    public void run()
    {
        long simulation_time = new Date().getTime();
        long frame_start_time = simulation_time;

        while( running )
        {
            input.waitMs( 0 );
            simulation_time = physics.step( simulation_time, frame_start_time );
            graphics.draw( physics.frame );
            frame_start_time = waitForNextFrame( frame_start_time );

            if ( !physics.gameRunning() )
            {
                pause();

                // Reset the times to stop us thinking we've really lagged
                simulation_time = new Date().getTime();
                frame_start_time = simulation_time;
            }

            if ( !physics.gameRunning() )
            {
                running = false;
            }
        }
    }

    public void pleaseStop()
    {
        running = false;
    }

    private void pause()
    {
        graphics.rememberScrollPos();

        while ( !physics.gameRunning() && running )
        {
            input.waitMs( 100 );
            graphics.drawIfScrolled( physics.frame );
        }
    }

    private long waitForNextFrame( long frame_start_time )
    {
        long next_frame_start_time = new Date().getTime();

        long how_long_we_took = next_frame_start_time - frame_start_time;
        long wait_time = frame_time_ms - how_long_we_took;

        input.waitMs( wait_time );

        return next_frame_start_time;
    }
}

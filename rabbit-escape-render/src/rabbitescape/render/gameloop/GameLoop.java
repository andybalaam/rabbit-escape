package rabbitescape.render.gameloop;

public class GameLoop
{
    private static final long frame_time_ms = 70;

    private final Input input;
    private final Physics physics;
    private final Graphics graphics;
    private boolean running;
    private long simulation_time;
    private long frame_start_time;

    public GameLoop( Input input, Physics physics, Graphics graphics )
    {
        this.input = input;
        this.physics = physics;
        this.graphics = graphics;
        this.running = true;
        simulation_time = -1;
        frame_start_time = -1;
    }

    public void run()
    {
        simulation_time = input.timeNow();
        frame_start_time = simulation_time;

        while( running )
        {
            running = step();
        }
    }

    public boolean step()
    {
        input.waitMs( 0 );
        simulation_time = physics.step( simulation_time, frame_start_time );
        graphics.draw( physics.frameNumber() );
        frame_start_time = waitForNextFrame( frame_start_time );

        if ( !physics.gameRunning() )
        {
            pause();

            // Reset the times to stop us thinking we've really lagged
            simulation_time = input.timeNow();
            frame_start_time = simulation_time;
        }

        return physics.gameRunning() && running;
    }

    public void pleaseStop()
    {
        running = false;
    }

    public boolean isRunning()
    {
        return running;
    }

    private void pause()
    {
        graphics.rememberScrollPos();

        while ( !physics.gameRunning() && running )
        {
            input.waitMs( 100 );
            graphics.drawIfScrolled( physics.frameNumber() );
        }
    }

    private long waitForNextFrame( long frame_start_time )
    {
        long next_frame_start_time = input.timeNow();

        long how_long_we_took = next_frame_start_time - frame_start_time;
        long wait_time = frame_time_ms - how_long_we_took;

        input.waitMs( wait_time );

        return next_frame_start_time;
    }
}

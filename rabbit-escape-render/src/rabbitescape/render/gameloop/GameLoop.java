package rabbitescape.render.gameloop;

import java.io.PrintStream;

import rabbitescape.engine.Rabbit;
import rabbitescape.engine.config.ConfigKeys;
import rabbitescape.engine.config.ConfigTools;
import rabbitescape.engine.config.IConfig;

public class GameLoop
{
    private static final long frame_time_ms = 70;

    private final Input input;
    private final Physics physics;
    private final Graphics graphics;
    private boolean running;
    private long simulation_time;
    private long frame_start_time;

    private final IConfig config;
    private final PrintStream debugout;

    public GameLoop(
        Input input,
        Physics physics,
        Graphics graphics,
        IConfig config,
        PrintStream debugout
    )
    {
        this.input = input;
        this.physics = physics;
        this.graphics = graphics;
        this.config = config;
        this.debugout = debugout;
        this.running = true;
        simulation_time = -1;
        frame_start_time = -1;
    }

    public void run()
    {
        resetClock();

        while( running )
        {
            running = step();
            printDebugOutput();
        }

        input.dispose();
        physics.dispose();
        graphics.dispose();
    }

    public void resetClock()
    {
        simulation_time = input.timeNow();
        frame_start_time = simulation_time;
    }

    public boolean step()
    {
        input.waitMs( 0 ); // Check for interruptions or input
        simulation_time = physics.step( simulation_time, frame_start_time );
        graphics.draw( physics.frameNumber() );
        frame_start_time = waitForNextFrame( frame_start_time );

        if ( !physics.gameRunning() )
        {
            pause();

            resetClock();
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
        long how_long_we_took = input.timeNow() - frame_start_time;
        long wait_time = frame_time_ms - how_long_we_took;

        input.waitMs( wait_time );

        return input.timeNow();
    }

    private void printDebugOutput()
    {
        if ( physics.frameNumber() != 0 )
        { // only want this once per world step, not once per anim frame
            return;
        }
        if ( ConfigTools.getBool( config, ConfigKeys.CFG_DEBUG_PRINT_STATES ) )
        {
            for ( Rabbit rabbit : physics.world().rabbits )
            {
                debugout.println( " " + rabbit.toString() + ":" + rabbit.state.name() + " onSlope:" + rabbit.onSlope );
            }
        }
    }
}

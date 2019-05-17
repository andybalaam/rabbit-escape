package rabbitescape.render.gameloop;

import java.io.PrintStream;

import rabbitescape.engine.Rabbit;
import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.ConfigKeys;
import rabbitescape.engine.config.ConfigTools;
import rabbitescape.engine.util.LongRingBuffer;

public class GameLoop
{
    public final static long WAIT_TIME_UNAVAILABLE = -99999;

    private static final long frame_time_ms = 70;

    private final Input input;
    private final Physics physics;
    private final Graphics graphics;
    private final WaterAnimation water;
    private boolean running;
    private long simulation_time;
    private long frame_start_time;
    private final LongRingBuffer waitTimes;

    private final Config config;
    private final PrintStream debugout;

    public GameLoop(
        Input input,
        Physics physics,
        WaterAnimation water,
        Graphics graphics,
        Config config,
        PrintStream debugout
    )
    {
        this.input = input;
        this.physics = physics;
        this.water = water;
        this.graphics = graphics;
        this.config = config;
        this.debugout = debugout;
        this.running = true;
        simulation_time = -1;
        frame_start_time = -1;
        waitTimes = new LongRingBuffer( 32 );
        water.setGameLoop( this );
    }

    public void run()
    {
        resetClock();
        physics.init();

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
        water.update( physics.frameNumber() );
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

        waitTimes.write( wait_time );
        input.waitMs( wait_time );

        return input.timeNow();
    }

    /**
     * Return the smoothed wait time for use as a performance indicator.
     * Returns WAIT_TIME_UNAVAILABLE if there has been insufficent time
     * to accumulate data to calculate a boxcar average.
     */
    public long getWaitTime()
    {
        if ( waitTimes.full() )
        {
            return waitTimes.mean();
        }
        return WAIT_TIME_UNAVAILABLE;
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
                debugout.println( " " + rabbit.toString() + ":" +
                                  rabbit.state.name() + " onSlope:" +
                                  rabbit.onSlope );
            }
        }
    }
}

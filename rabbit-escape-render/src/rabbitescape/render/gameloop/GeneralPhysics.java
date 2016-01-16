package rabbitescape.render.gameloop;

import java.util.ArrayList;
import java.util.List;

import rabbitescape.engine.solution.PlaceTokenAction;
import rabbitescape.engine.solution.SelectAction;
import rabbitescape.engine.solution.SolutionIgnorer;
import rabbitescape.engine.solution.SolutionInterpreter;
import rabbitescape.engine.solution.SolutionRecorderTemplate;
import rabbitescape.engine.solution.SolutionTimeStep;
import rabbitescape.engine.solution.TimeStepAction;
import rabbitescape.engine.solution.UiPlayback;
import rabbitescape.engine.LevelWinListener;
import rabbitescape.engine.Token;
import rabbitescape.engine.World;
import rabbitescape.engine.World.CompletionState;

public class GeneralPhysics implements Physics
{
    /**
     * Everything that modifies the world goes through here, with
     * synchronization.
     *
     * Public for test
     */
    public static class WorldModifier
    {
        private final World world;
        public final SolutionRecorderTemplate solutionRecorder;

        public WorldModifier( World world, SolutionRecorderTemplate solutionRecorder )
        {
            this.world = world;
            this.solutionRecorder = solutionRecorder;
        }

        public synchronized void step()
        {
            world.step();
            solutionRecorder.appendStepEnd( );
        }

        public synchronized void addToken(
            int tileX, int tileY, Token.Type type )
        {
            world.changes.addToken( tileX, tileY, type );
        }
    }

    private static final long max_allowed_skips = 10;
    public static final long simulation_time_step_ms = 70;

    public int frame;
    public final World world;
    private final WorldModifier worldModifier;
    private final LevelWinListener winListener;
    private final List<StatsChangedListener> statsListeners;
    private final SolutionInterpreter solutionInterpreter;
    private final UiPlayback uiPlayback;
    
    /** Speed factor. 1 is normal, 2 is twice as fast, and so on. */
    private int speed = 1;
    
    public GeneralPhysics( World world, LevelWinListener winListener )
    {
        this( world, 
              winListener, 
              new SolutionIgnorer(), 
              SolutionInterpreter.getNothingPlaying(),
              null);
    }
    

    public GeneralPhysics( World world, 
                           LevelWinListener winListener, 
                           SolutionRecorderTemplate solutionRecorder,
                           SolutionInterpreter solutionInterpreter,
                           UiPlayback uiPlayback)
    {
        this.frame = 0;
        this.world = world;
        this.worldModifier = new WorldModifier( world, solutionRecorder );
        this.winListener = winListener;
        this.statsListeners = new ArrayList<>();
        this.solutionInterpreter = solutionInterpreter;
        this.uiPlayback = uiPlayback; 
    }
    
    public void setSpeed( int speed )
    {
        if ( speed > 10 )
        {
            //TODO exception: hyperspeed may miss world steps.
        }
        this.speed = speed;
    }

    @Override
    public long step( long simulation_time, long frame_start_time )
    {
        for ( int skipped = 0; skipped < max_allowed_skips; ++skipped )
        {
            if ( simulation_time >= frame_start_time )
            {
                break;
            }

            frame += speed;

            if ( frame >= 10 )
            {
                frame -= 10;

                doInterpreterActions();
                worldModifier.step();
                checkWon();
                notifyStatsListeners();
            }

            simulation_time += simulation_time_step_ms;
        }

        return simulation_time;
    }

    @Override
    public int frameNumber()
    {
        return frame;
    }

    @Override
    public boolean gameRunning()
    {
        return ( world.completionState() == World.CompletionState.RUNNING );
    }

    /**
     * Take actions for demo mode, eg drop tokens.
     */
    private void doInterpreterActions()
    {
        SolutionTimeStep stp = solutionInterpreter.next( CompletionState.RUNNING );
        for ( TimeStepAction action: stp.actions )
        {
            if ( action instanceof SelectAction )
            {
                uiPlayback.selectToken( (SelectAction)action );
            }
            else if ( action instanceof PlaceTokenAction )
            {
                uiPlayback.placeToken( (PlaceTokenAction)action );
            }
        }
    }
    
    private void notifyStatsListeners()
    {
        for ( StatsChangedListener listener : statsListeners )
        {
            listener.changed(
                world.num_waiting,
                world.numRabbitsOut(),
                world.num_saved
            );
        }
    }

    public int addToken( int tileX, int tileY, Token.Type ability )
    {
        if (
               gameRunning()
            && tileX >= 0
            && tileX < world.size.width
            && tileY >= 0
            && tileY < world.size.height
            && world.abilities.get( ability ) > 0
        )
        {
            worldModifier.addToken( tileX, tileY, ability );
        }

        return world.abilities.get( ability );
    }

    public void addStatsChangedListener( StatsChangedListener listener )
    {
        statsListeners.add( listener );
    }

    private void checkWon()
    {
        switch ( world.completionState() )
        {
            case WON:
            {
                winListener.won();
                break;
            }
            case LOST:
            {
                winListener.lost();
                break;
            }
            default:
            {
                break;
            }
        }
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public World world()
    {
        return world;
    }
}

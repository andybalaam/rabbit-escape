package rabbitescape.render;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import rabbitescape.engine.ChangeDescription;
import rabbitescape.engine.LevelWinListener;
import rabbitescape.engine.Token;
import rabbitescape.engine.World;
import rabbitescape.engine.solution.SolutionIgnorer;
import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.render.gameloop.GeneralPhysics;
import rabbitescape.render.gameloop.Physics.StatsChangedListener;
import rabbitescape.render.gameloop.GeneralPhysics.WorldModifier;

public class TestPhysics
{
    @Test
    public void Many_threads_can_manipulate_World_simultaneously()
        throws Exception
    {
        final World world = TextWorldManip.createWorld(
            "#      #",
            "# /) r #",
            "########",
            ":climb=1000000"
        );

        final int num_threads = 100;
        final int num_iters   = 20;

        final WorldModifier modifier = new WorldModifier( world, new SolutionIgnorer() );

        class Stepper implements Runnable
        {
            public boolean failed = false;

            @Override
            public void run()
            {
                try
                {
                    for ( int i = 0; i < num_iters; ++i )
                    {
                        modifier.step();
                    }
                }
                catch ( Throwable e )
                {
                    failed = true;
                    e.printStackTrace();
                }
            }
        };

        class TokenAdder implements Runnable
        {
            public boolean failed = false;

            @Override
            public void run()
            {
                try
                {
                    for ( int i = 0; i < num_iters; ++i )
                    {
                        modifier.addToken( 5, 0, Token.Type.climb );
                    }
                }
                catch ( Throwable e )
                {
                    failed = true;
                    e.printStackTrace();
                }
            }
        };

        // ---

        final Stepper stepper = new Stepper();
        final TokenAdder tokenAdder = new TokenAdder();

        Thread[] threads = new Thread[num_threads];
        for ( int i = 0; i < num_threads; i += 2 )
        {
            threads[i    ] = new Thread( stepper );
            threads[i + 1] = new Thread( tokenAdder );
        }

        for ( int i = 0; i < num_threads; ++i )
        {
            threads[i].start();
        }

        for ( int i = 0; i < num_threads; ++i )
        {
            threads[i].join();
        }

        assertFalse( stepper.failed );
        assertFalse( tokenAdder.failed );
    }

    @Test
    public void Step_steps_world()
    {
        final World world = TextWorldManip.createWorld(
            "#      #",
            "# /) r #",
            "########",
            ":climb=1000000"
        );

        LevelWinListener winListener = null;
        GeneralPhysics physics = new GeneralPhysics(
            world, winListener, false );

        // This is what we are testing - step once
        for ( int i = 0; i < 10; ++i )
        {
            physics.step( 0, GeneralPhysics.simulation_time_step_ms );
        }

        // The rabbit has moved
        assertEquals( 6, world.rabbits.get( 0 ).x );
    }

    class TracingWinListener implements LevelWinListener
    {
        public boolean wonCalled  = false;
        public boolean lostCalled = false;

        @Override
        public void won()
        {
            assertFalse( wonCalled );
            wonCalled = true;
        }

        @Override
        public void lost()
        {
            assertFalse( lostCalled );
            lostCalled = true;
        }
    }

    @Test
    public void Step_notifies_when_we_won()
    {
        final World world = TextWorldManip.createWorld(
            "#      #",
            "# /) rO#",
            "########",
            ":num_rabbits=0"
        );

        TracingWinListener winListener = new TracingWinListener();
        GeneralPhysics physics = new GeneralPhysics(
            world, winListener, false );

        // This is what we are testing - 2 time steps - winlistener should hear
        for ( int i = 0; i < 20; ++i )
        {
            physics.step( 0, GeneralPhysics.simulation_time_step_ms );
        }

        // The winListener was notified of the win
        assertTrue(  winListener.wonCalled );
        assertFalse( winListener.lostCalled );
    }

    @Test
    public void Step_notifies_when_we_lost()
    {
        final World world = TextWorldManip.createWorld(
            "#   ",
            "#  r", // Death in 1 step
            "####",
            ":num_rabbits=0"
        );

        TracingWinListener winListener = new TracingWinListener();
        GeneralPhysics physics = new GeneralPhysics(
            world, winListener, false );

        // This is what we are testing - step twice - winlistener should hear
        for ( int i = 0; i < 20; ++i )
        {
            physics.step( 0, GeneralPhysics.simulation_time_step_ms );
        }

        // The winListener was notified of the loss
        assertFalse(  winListener.wonCalled );
        assertTrue(  winListener.lostCalled );
    }

    @Test
    public void AddToken_adds_a_token_if_youve_got_some()
    {
        final World world = TextWorldManip.createWorld(
            "#      #",
            "# /) r #",
            "########",
            ":bash=10"
        );

        LevelWinListener winListener = null;
        GeneralPhysics physics = new GeneralPhysics(
            world, winListener, false );

        // Sanity: no things at the moment
        assertEquals( 0, world.things.size() );

        // This is what we are testing - add the token
        physics.addToken( 1, 1, Token.Type.bash );

        // Allow the change to happen
        world.step();

        // It was added - there is now a token
        assertEquals( 1, world.things.size() );

        assertEquals(
            ChangeDescription.State.TOKEN_BASH_STILL,
            world.things.get( 0 ).state
        );
    }

    @Test
    public void AddToken_does_not_add_a_token_if_youve_not_got_any()
    {
        final World world = TextWorldManip.createWorld(
            "#      #",
            "# /) r #",
            "########",
            ":bash=1"
        );

        LevelWinListener winListener = null;
        GeneralPhysics physics = new GeneralPhysics(
            world, winListener, false );

        // Add 1 - should work
        physics.addToken( 1, 1, Token.Type.bash );
        world.step();
        assertEquals( 1, world.things.size() );

        // This is what we are testing - add another, but you don't have it
        physics.addToken( 1, 1, Token.Type.bash );

        // It was not added - there still only 1 thing
        assertEquals( 1, world.things.size() );
    }

    @Test
    public void AddToken_returns_how_many_are_left()
    {
        final World world = TextWorldManip.createWorld(
            "#      #",
            "# /) r #",
            "########",
            ":bash=2",
            ":climb=12"
        );

        LevelWinListener winListener = null;
        GeneralPhysics physics = new GeneralPhysics(
            world, winListener, false );

        // Add 1 - 1 left
        assertEquals( 1, physics.addToken( 1, 1, Token.Type.bash ) );

        // Add another, 0 left
        assertEquals( 0, physics.addToken( 1, 1, Token.Type.bash ) );

        // Try to add more, still 0 left
        assertEquals( 0, physics.addToken( 1, 1, Token.Type.bash ) );

        // Different type is independent
        assertEquals( 11, physics.addToken( 1, 1, Token.Type.climb ) );
    }

    @Test
    public void AddToken_does_not_add_a_token_if_its_outside_world()
    {
        final World world = TextWorldManip.createWorld(
            "#      #",
            "# /) r #",
            "########",
            ":bash=1"
        );

        LevelWinListener winListener = null;
        GeneralPhysics physics = new GeneralPhysics(
            world, winListener, false );

        // Off the left does not add
        physics.addToken( -1, 1, Token.Type.bash );
        world.step();
        assertEquals( 0, world.things.size() );

        // Off the right does not add
        physics.addToken( 8, 1, Token.Type.bash );
        world.step();
        assertEquals( 0, world.things.size() );

        // Off the top does not add
        physics.addToken( 1, -1, Token.Type.bash );
        world.step();
        assertEquals( 0, world.things.size() );

        // Off the bottom does not add
        physics.addToken( 1, 3, Token.Type.bash );
        world.step();
        assertEquals( 0, world.things.size() );
    }

    @Test
    public void AddToken_does_not_add_a_token_if_not_running()
    {
        final World world = TextWorldManip.createWorld(
            "#      #",
            "# /) r #",
            "########",
            ":bash=1"
        );

        LevelWinListener winListener = null;
        GeneralPhysics physics = new GeneralPhysics(
            world, winListener, false );

        // Paused does not add
        world.setPaused( true );
        physics.addToken( 1, 1, Token.Type.bash );

        // Unpause to step and check
        world.setPaused( false );
        world.step();
        assertEquals( 0, world.things.size() );

        // Unpaused does add
        physics.addToken( 1, 1, Token.Type.bash );
        world.step();
        assertEquals( 1, world.things.size() );
    }

    @Test
    public void GameRunning_reports_game_status()
    {
        final World world = TextWorldManip.createWorld(
            "#      #",
            "# /) r #",
            "########",
            ":bash=1"
        );

        LevelWinListener winListener = null;
        GeneralPhysics physics = new GeneralPhysics(
            world, winListener, false );

        assertTrue( physics.gameRunning() );

        world.setPaused( true );
        assertFalse( physics.gameRunning() );
    }

    @Test
    public void Stats_listeners_are_notified_when_stats_change()
    {
        class TrackingStatsListener implements StatsChangedListener
        {
            boolean changedCalled = false;

            @Override
            public void changed( int waiting, int out, int saved )
            {
                assertFalse( changedCalled );
                changedCalled = true;
            }
        }

        final World world = TextWorldManip.createWorld(
            "#      #",
            "# /) r #",
            "########",
            ":block=1"
        );

        LevelWinListener winListener = null;
        GeneralPhysics physics = new GeneralPhysics(
            world, winListener, false );

        // Ask to track stats
        TrackingStatsListener myListener = new TrackingStatsListener();
        physics.addStatsChangedListener( myListener );

        // Sanity
        assertFalse( myListener.changedCalled );

        // Change something that changes stats
        physics.addToken( 1, 1, Token.Type.block );

        // This is what we are testing - step the world
        for ( int i = 0; i < 10; ++i )
        {
            physics.step( 0, GeneralPhysics.simulation_time_step_ms );
        }

        // Listeners should have been called
        assertTrue( myListener.changedCalled );
    }

    // TODO: Stats_listeners_are_not_notified_when_stats_do_not_change

    @Test
    public void Fast_is_set_by_constructor()
    {
        GeneralPhysics physicsSlow = new GeneralPhysics( null, null, false );
        assertThat( physicsSlow.fast, is( false ) );

        GeneralPhysics physicsFast = new GeneralPhysics( null, null, true );
        assertThat( physicsFast.fast, is( true ) );
    }

    @Test
    public void Step_one_frame_if_fast_is_false()
    {
        final World world = TextWorldManip.createWorld( "#" );
        LevelWinListener winListener = null;

        // Make a physics that is not fast
        GeneralPhysics physics = new GeneralPhysics(
            world, winListener, false );

        // Sanity: we start at frame 0
        assertThat( physics.frame, is( 0 ) );

        // This is what we are testing: step forward once
        physics.step( 0, 1 );

        // We only moved one frame
        assertThat( physics.frame, is( 1 ) );
    }

    @Test
    public void Step_three_frames_if_fast_is_true()
    {
        final World world = TextWorldManip.createWorld( "#" );
        LevelWinListener winListener = null;

        // Make a physics that IS fast
        GeneralPhysics physics = new GeneralPhysics(
            world, winListener, true );

        // Sanity: we start at frame 0
        assertThat( physics.frame, is( 0 ) );

        // This is what we are testing: step forward once
        physics.step( 0, 1 );

        // We moved 3 frames
        assertThat( physics.frame, is( 3 ) );
    }
}

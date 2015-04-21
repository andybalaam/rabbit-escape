package rabbitescape.render;

import static org.junit.Assert.*;

import org.junit.Test;

import rabbitescape.engine.ChangeDescription;
import rabbitescape.engine.LevelWinListener;
import rabbitescape.engine.Token;
import rabbitescape.engine.World;
import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.render.Physics.StatsChangedListener;
import rabbitescape.render.Physics.WorldModifier;

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
        world.setIntro( false );

        final int num_threads = 100;
        final int num_iters   = 20;

        final WorldModifier modifier = new WorldModifier( world );

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
        world.setIntro( false );

        LevelWinListener winListener = null;
        Physics physics = new Physics( world, winListener );

        // This is what we are testing - step
        physics.step();

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
        world.setIntro( false );

        TracingWinListener winListener = new TracingWinListener();
        Physics physics = new Physics( world, winListener );

        // This is what we are testing - step twice - winlistener should hear
        physics.step();
        physics.step();

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
        world.setIntro( false );

        TracingWinListener winListener = new TracingWinListener();
        Physics physics = new Physics( world, winListener );

        // This is what we are testing - step twice - winlistener should hear
        physics.step();
        physics.step();

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
        world.setIntro( false );

        LevelWinListener winListener = null;
        Physics physics = new Physics( world, winListener );

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
        world.setIntro( false );

        LevelWinListener winListener = null;
        Physics physics = new Physics( world, winListener );

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
        world.setIntro( false );

        LevelWinListener winListener = null;
        Physics physics = new Physics( world, winListener );

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
        world.setIntro( false );

        LevelWinListener winListener = null;
        Physics physics = new Physics( world, winListener );

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
        world.setIntro( false );

        LevelWinListener winListener = null;
        Physics physics = new Physics( world, winListener );

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
        world.setIntro( false );

        LevelWinListener winListener = null;
        Physics physics = new Physics( world, winListener );

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
        world.setIntro( false );

        LevelWinListener winListener = null;
        Physics physics = new Physics( world, winListener );

        // Ask to track stats
        TrackingStatsListener myListener = new TrackingStatsListener();
        physics.addStatsChangedListener( myListener );

        // Sanity
        assertFalse( myListener.changedCalled );

        // Change something that changes stats
        physics.addToken( 1, 1, Token.Type.block );

        // This is what we are testing - step the world
        physics.step();

        // Listeners should have been called
        assertTrue( myListener.changedCalled );
    }

    // TODO: Stats_listeners_are_not_notified_when_stats_do_not_change
}

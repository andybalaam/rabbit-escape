package rabbitescape.render;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.*;
import rabbitescape.engine.*;
import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.render.gameloop.*;

public class TestGameLoop
{
    @Test
    public void If_rendering_is_quick_we_render_all_frames()
    {
        CallTracker calls = new CallTracker();
        SimpleClockInput input = new SimpleClockInput( calls );
        DrawTracker graphics = new DrawTracker( input, calls, 0 );

        GeneralPhysics physics = new GeneralPhysics(
            new NeverWonWorld( TextWorldManip.createEmptyWorld( 5, 5 ) ),
            null
        );

        GameLoop gameLoop = new GameLoop( input, physics, graphics );
        gameLoop.resetClock();

        // Progress through the frames
        gameLoop.step();

        // At each step, we should draw
        graphics.calls.assertCalls( "waitMs(0)", "draw(0)", "waitMs(70)" );

        // 10 more frames
        for ( int i = 0; i < 10; ++i )
        {
            gameLoop.step();
        }
        graphics.calls.assertCalls(
            "waitMs(0)", "draw(0)", "waitMs(70)",
            "waitMs(0)", "draw(1)", "waitMs(70)",
            "waitMs(0)", "draw(2)", "waitMs(70)",
            "waitMs(0)", "draw(3)", "waitMs(70)",
            "waitMs(0)", "draw(4)", "waitMs(70)",
            "waitMs(0)", "draw(5)", "waitMs(70)",
            "waitMs(0)", "draw(6)", "waitMs(70)",
            "waitMs(0)", "draw(7)", "waitMs(70)",
            "waitMs(0)", "draw(8)", "waitMs(70)",
            "waitMs(0)", "draw(9)", "waitMs(70)",
            "waitMs(0)", "draw(0)", "waitMs(70)" // Start again after 10
        );
    }

    // ---

    private static class NeverWonWorld extends World
    {
        public NeverWonWorld( World w )
        {
            super(
                w.size,
                w.blocks,
                w.rabbits,
                w.things,
                w.abilities,
                w.name,
                w.description,
                w.num_rabbits,
                w.num_to_save,
                w.rabbit_delay,
                w.num_saved,
                w.num_killed,
                1,
                w.intro,
                w.paused,
                w.readyToExplodeAll,
                w.changes.statsListener
            );
            setIntro( false );
            assertThat( completionState(), equalTo( CompletionState.RUNNING ) );
        }
    }

    private static class SimpleClockInput implements Input
    {
        private final CallTracker calls;
        private long time = 0;

        public SimpleClockInput( CallTracker calls )
        {
            this.calls = calls;
        }

        @Override
        public void waitMs( long wait_time )
        {
            calls.track( "waitMs", wait_time );
            time += wait_time;
        }

        @Override
        public long timeNow()
        {
            return time;
        }
    }

    private static class DrawTracker extends DoNothingGraphics
    {
        private final SimpleClockInput input;
        private final CallTracker calls;
        private final int renderTimeMs;

        public DrawTracker(
            SimpleClockInput input, CallTracker calls, int renderTimeMs )
        {
            super();
            this.input = input;
            this.calls = calls;
            this.renderTimeMs = renderTimeMs;
        }

        @Override
        public void draw( int frame )
        {
            calls.track( "draw", frame );
            input.time += renderTimeMs;
        }
    }

    private static class DoNothingGraphics implements Graphics
    {

        @Override
        public void draw( int frame )
        {
        }

        @Override
        public void rememberScrollPos()
        {

        }

        @Override
        public void drawIfScrolled( int frame )
        {
        }
    }
}
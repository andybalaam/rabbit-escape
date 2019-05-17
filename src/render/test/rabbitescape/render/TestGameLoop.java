package rabbitescape.render;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.io.PrintStream;

import org.junit.*;
import rabbitescape.engine.*;
import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.MemoryConfigStorage;
import rabbitescape.engine.textworld.Comment;
import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.render.gameloop.*;

public class TestGameLoop
{
    @Test
    public void If_rendering_is_quick_we_render_all_frames()
    {
        TestObjects obj = prepareGameLoop( 0 ); // 0ms to draw a frame

        // Progress through the frames
        obj.gameLoop.step();

        // At each step, we should draw
        obj.calls.assertCalls( "waitMs(0)", "draw(0)", "waitMs(70)" );

        // 10 more frames
        for ( int i = 0; i < 10; ++i )
        {
            obj.gameLoop.step();
        }
        obj.calls.assertCalls(
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

    @Test
    public void If_rendering_is_fairly_quick_we_render_all_frames()
    {
        TestObjects obj = prepareGameLoop( 55 ); // 55ms per frame

        // Do 5 frames
        for ( int i = 0; i < 5; ++i )
        {
            obj.gameLoop.step();
        }

        obj.calls.assertCalls(
            "waitMs(0)", "draw(0)", "waitMs(15)",
            "waitMs(0)", "draw(1)", "waitMs(15)",
            "waitMs(0)", "draw(2)", "waitMs(15)",
            "waitMs(0)", "draw(3)", "waitMs(15)",
            "waitMs(0)", "draw(4)", "waitMs(15)"
        );
    }

    @Test
    public void Long_pause_in_middle_does_not_cause_us_to_skip()
    {
        TestObjects obj = prepareGameLoop( 45 );

        // More frames
        for ( int i = 0; i < 5; ++i )
        {
            obj.gameLoop.step();
        }

        // A long pause happens...
        obj.input.time += 3000;

        // More frames
        for ( int i = 0; i < 6; ++i )
        {
            obj.gameLoop.step();
        }

        obj.calls.assertCalls(
            "waitMs(0)", "draw(0)", "waitMs(25)",
            "waitMs(0)", "draw(1)", "waitMs(25)",
            "waitMs(0)", "draw(2)", "waitMs(25)",
            "waitMs(0)", "draw(3)", "waitMs(25)",
            "waitMs(0)", "draw(4)", "waitMs(25)",
            "waitMs(0)", "draw(5)", "waitMs(-2975)", // We'd like to go back
            "waitMs(0)", "draw(6)", "waitMs(25)",    // in time, but once we've
            "waitMs(0)", "draw(7)", "waitMs(25)",    // expressed that, we
            "waitMs(0)", "draw(8)", "waitMs(25)",    // carry on regardless.
            "waitMs(0)", "draw(9)", "waitMs(25)",
            "waitMs(0)", "draw(0)", "waitMs(25)"
        );
    }

    // ---

    class TestObjects
    {
        public CallTracker calls;
        public GameLoop gameLoop;
        public SimpleClockInput input;
        public DrawTracker graphics;
    }

    private TestObjects prepareGameLoop( int msPerFrame )
    {
        TestObjects ret = new TestObjects();

        ret.calls = new CallTracker();
        ret.input = new SimpleClockInput( ret.calls );

        // 0 ms to draw each per frame
        ret.graphics = new DrawTracker( ret.input, ret.calls, msPerFrame );

        World neverWonWorld = new NeverWonWorld( TextWorldManip.createEmptyWorld( 5, 5 ) );
        GeneralPhysics physics = new GeneralPhysics(
            neverWonWorld,
            null,
            false
        );

        Config config = new Config( null, new MemoryConfigStorage() );
        PrintStream debugout = null;

        ret.gameLoop = new GameLoop(
            ret.input, physics, new WaterAnimation( neverWonWorld ), ret.graphics, config, debugout );

        ret.gameLoop.resetClock();

        return ret;
    }

    private static class NeverWonWorld extends World
    {
        public NeverWonWorld( World w )
        {
            super(
                w.size,
                w.blockTable.getListCopy(),
                w.rabbits,
                w.things,
                w.getWaterContents(),
                w.abilities,
                w.name,
                w.description,
                w.author_name,
                w.author_url,
                w.hints,
                w.solutions,
                w.num_rabbits,
                w.num_to_save,
                w.rabbit_delay,
                "",
                w.num_saved,
                w.num_killed,
                1,
                w.getRabbitIndexCount(),
                w.paused,
                new Comment[] {},
                w.changes.statsListener,
                VoidMarkerStyle.Style.HIGHLIGHTER
            );
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

        @Override
        public void dispose()
        {
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

        @Override
        public void dispose()
        {
        }
    }
}
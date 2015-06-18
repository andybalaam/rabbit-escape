package rabbitescape.render.androidutil;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.*;

/**
 * See http://www.artificialworlds.net/blog/2015/06/05/order-of-android-activity-lifecycle-events/
 * - we must receive certain events in certain orders, and start, pause and stop
 * the music appropriately.
 */
public class TestLifecycle2SoundEvents
{
    private static final String activity1 = "one";
    private static final String activity2 = "two";

    @Test
    public void Press_the_home_button_api10_causes_pause()
    {
        Tester t = new Tester( activity1 );

        t.in.onSaveInstanceState( activity1 );
        t.in.onPause( activity1 );
        t.in.onStop( activity1 );

        // When we press home, we must at least pause (really we stop)
        t.assertPaused();
    }

    @Test
    public void Press_the_home_button_api20_causes_pause()
    {
        Tester t = new Tester( activity1 );

        t.in.onPause( activity1 );
        t.in.onSaveInstanceState( activity1 );
        t.in.onStop( activity1 );

        // When we press home, we must at least pause (really we stop)
        t.assertPaused();
    }

    @Test
    public void Press_the_back_button_to_exit_causes_dispose()
    {
        Tester t = new Tester( activity1 );

        t.in.onPause( activity1 );
        t.in.onStop( activity1 );
        t.in.onDestroy( activity1 );

        t.assertDisposed();
    }

    @Test
    public void Start_the_app_causes_start()
    {
        Tester t = new Tester();

        t.in.onCreate( activity1 );
        t.in.onStart( activity1 );
        t.in.onResume( activity1 );

        t.assertStarted( activity1 );
    }

    @Test
    public void Turn_off_with_the_power_button_api10_causes_pause()
    {
        Tester t = new Tester( activity1 );

        t.in.onSaveInstanceState( activity1 );
        t.in.onPause( activity1 );

        // We never get an onStop, so we can't dispose unless we use
        // some kind of thread hack.  Make do with pausing.
        t.assertPaused();
    }

    @Test
    public void Turn_off_with_the_power_button_api20_causes_pause()
    {
        Tester t = new Tester( activity1 );

        t.in.onPause( activity1 );
        t.in.onSaveInstanceState( activity1 );
        t.in.onStop( activity1 );

        // Only ask for pause here, to match API level 10 (really, we
        // will dispose too).
        t.assertPaused();
    }

    @Test
    public void Turn_on_with_the_power_button_api10_causes_start()
    {
        Tester t = new Tester();

        t.in.onResume( activity1 );

        t.assertStarted( activity1 );
    }

    @Test
    public void Turn_on_with_the_power_button_api20_causes_start()
    {
        Tester t = new Tester();

        t.in.onRestart( activity1 );
        t.in.onStart( activity1 );
        t.in.onResume( activity1 );

        t.assertStarted( activity1 );
    }

    @Test
    public void Restart_the_app_after_pressing_home_causes_start()
    {
        Tester t = new Tester();

        t.in.onRestart( activity1 );
        t.in.onStart( activity1 );
        t.in.onResume( activity1 );

        t.assertStarted( activity1 );
    }

    @Test
    public void Restart_the_app_after_pressing_back_causes_start()
    {
        Tester t = new Tester();

        t.in.onCreate( activity1 );
        t.in.onStart( activity1 );
        t.in.onResume( activity1 );

        t.assertStarted( activity1 );
    }

    @Test
    public void Launch_a_second_activity_api10_ends_up_started()
    {
        Tester t = new Tester( activity1 );

        t.in.onSaveInstanceState( activity1 );
        t.in.onPause(             activity1 );
        t.in.onCreate(            activity2 );
        t.in.onStart(             activity2 );
        t.in.onResume(            activity2 );
        t.in.onStop(              activity1 );

        t.assertEndsUpStarted( activity2 );
    }

    @Test
    public void Launch_a_second_activity_api20_ends_up_started()
    {
        Tester t = new Tester( activity1 );

        t.in.onPause(             activity1 );
        t.in.onCreate(            activity2 );
        t.in.onStart(             activity2 );
        t.in.onResume(            activity2 );
        t.in.onSaveInstanceState( activity1 );
        t.in.onStop(              activity1 );

        t.assertEndsUpStarted( activity2 );
    }

    @Test
    public void Go_back_from_one_activity_to_another_ends_up_started()
    {
        Tester t = new Tester( activity2 );

        t.in.onPause(             activity2 );
        t.in.onRestart(           activity1 );
        t.in.onStart(             activity1 );
        t.in.onResume(            activity1 );
        t.in.onStop(              activity2 );
        t.in.onDestroy(           activity2 );

        t.assertEndsUpStarted( activity1 );
    }

    // ---

    private static class Tester
    {
        public TracingSoundResources out = new TracingSoundResources();
        public Lifecycle2SoundEvents<String> in =
            new Lifecycle2SoundEvents<String>( out );

        /**
         * Ready to start with no current activity.
         */
        public Tester()
        {
        }

        /**
         * Ready to start with the supplied activity already running.
         */
        public Tester( String currentActivity )
        {
            in.onStart(  currentActivity );
            in.onCreate( currentActivity );
            in.onResume( currentActivity );
            out.calls = "";
        }

        public void assertPaused()
        {
            assertThat( out.calls, containsString( "pause" ) );
            // May contain disposed
            assertThat( out.calls, not( containsString( "start" ) ) );
        }

        public void assertDisposed()
        {
            // May contain paused
            assertThat( out.calls, containsString( "dispose" ) );
            assertThat( out.calls, not( containsString( "start" ) ) );
        }

        public void assertStarted( String activity )
        {
            assertThat( out.calls, containsString( "start " + activity ) );
            assertThat( out.calls, not( containsString( "pause" ) ) );
            assertThat( out.calls, not( containsString( "dispose" ) ) );
        }

        public void assertEndsUpStarted( String activity )
        {
            assertThat(
                out.calls,
                anyOf(
                    equalTo( " start " + activity ),
                    equalTo( " pause start " + activity )
                )
            );
        }
    }
}

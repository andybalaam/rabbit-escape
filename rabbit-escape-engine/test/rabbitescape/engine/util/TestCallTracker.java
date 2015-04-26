package rabbitescape.engine.util;

import org.junit.Test;
import rabbitescape.engine.CallTracker;

public class TestCallTracker
{
    @Test
    public void Uncalled_tracker_is_empty()
    {
        CallTracker tracker = new CallTracker();

        tracker.assertCalls(
        );
    }

    @Test
    public void Single_call_is_tracked()
    {
        CallTracker tracker = new CallTracker();

        tracker.track( "foo", 4, "sdf" );

        tracker.assertCalls(
            "foo(4,sdf)"
        );
    }

    @Test
    public void Multiple_calls_are_tracked()
    {
        CallTracker tracker = new CallTracker();

        tracker.track( "foo", 4, "sdf" );
        tracker.track( "bar", true, 17.5 );

        tracker.assertCalls(
            "foo(4,sdf)",
            "bar(true,17.5)"
        );
    }
}

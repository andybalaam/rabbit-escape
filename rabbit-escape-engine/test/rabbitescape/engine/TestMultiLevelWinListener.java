package rabbitescape.engine;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.*;

public class TestMultiLevelWinListener
{
    @Test
    public void Each_sub_listener_is_notified()
    {
        TrackingLevelWinListener sub1 = new TrackingLevelWinListener();
        TrackingLevelWinListener sub2 = new TrackingLevelWinListener();

        // Make a multi-listener with 2 sub-listeners
        MultiLevelWinListener listener =
            new MultiLevelWinListener( sub1, sub2 );

        // Sanity - no notifications yet
        assertThat( sub1.wonCalled, is( false ) );
        assertThat( sub2.wonCalled, is( false ) );

        // This is what we are testing - say we won
        listener.won();

        // Both were notified
        assertThat( sub1.wonCalled, is( true ) );
        assertThat( sub2.wonCalled, is( true ) );
    }

    // ---

    private static class TrackingLevelWinListener implements LevelWinListener
    {
        public boolean wonCalled = false;

        @Override
        public void won()
        {
            wonCalled = true;
        }
    }
}

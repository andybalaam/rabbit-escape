package rabbitescape.engine;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.*;

import rabbitescape.engine.menu.LevelsCompleted;

public class TestCompletedLevelWinListener
{
    @Test
    public void Same_level_as_highest_has_no_effect()
    {
        // Track calls to LevelsCompleted with this fake (claim highest is 3)
        FakeLevelsCompleted fakeLevelsCompleted = new FakeLevelsCompleted( 3 );

        // Make a win listener for level 3 of the foo section
        CompletedLevelWinListener winListener =
            new CompletedLevelWinListener( "foo", 3, fakeLevelsCompleted );

        // Sanity - no calls to the LevelsCompleted yet
        assertThat( fakeLevelsCompleted.log.size(), equalTo( 0 ) );

        // This is what we are testing - say we won
        winListener.won();

        // We did not update the LC because the level we were playing
        // was not higher than the highest so far.
        assertThat( fakeLevelsCompleted.log.size(), equalTo( 0 ) );
    }

    @Test
    public void Higher_level_notifies_levels_completed()
    {
        // Track calls to LevelsCompleted with this fake (claim highest is 3)
        FakeLevelsCompleted fakeLevelsCompleted = new FakeLevelsCompleted( 3 );

        // Make a win listener for level 4 of the foo section
        CompletedLevelWinListener winListener =
            new CompletedLevelWinListener( "foo", 4, fakeLevelsCompleted );

        // Sanity - no calls to the LevelsCompleted yet
        assertThat( fakeLevelsCompleted.log.size(), equalTo( 0 ) );

        // This is what we are testing - say we won
        winListener.won();

        // We updated the LC because the level we were playing
        // was higher than the highest so far.
        assertThat(
            fakeLevelsCompleted.log.get( 0 ),
            equalTo( "setCompletedLevel foo 4" )
        );
        assertThat( fakeLevelsCompleted.log.size(), equalTo( 1 ) );
    }

    // ---

    private static class FakeLevelsCompleted implements LevelsCompleted
    {
        private final int highest;
        public final List<String> log = new ArrayList<String>();

        public FakeLevelsCompleted( int highest )
        {
            this.highest = highest;
        }

        @Override
        public int highestLevelCompleted( String levelsDir )
        {
            return highest;
        }

        @Override
        public void setCompletedLevel( String levelsDir, int levelNum )
        {
            log.add( "setCompletedLevel " + levelsDir + " " + levelNum );
        }
    }
}

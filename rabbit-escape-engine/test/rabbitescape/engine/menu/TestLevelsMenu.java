package rabbitescape.engine.menu;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.*;

import rabbitescape.engine.util.NamedFieldFormatter;

public class TestLevelsMenu
{
    @Test
    public void List_all_levels_in_a_dir_when_completed_none()
    {
        LevelsCompleted levelsCompleted = new HardCodedLevelsCompleted( 0 );

        // Only the first level is enabled
        assertThat(
            menuItemsToStrings(
                LevelsMenu.menuItems( "test2", levelsCompleted ) ),
            equalTo(
                new String[] {
                    "Level 1 test2/lev1.rel",
                    "Level 2 test2/lev2.rel (disabled)",
                    "Level 3 test2/lev3.rel (disabled)"
                }
            )
        );
    }

    @Test
    public void List_all_levels_in_a_dir_when_completed_one()
    {
        LevelsCompleted levelsCompleted = new HardCodedLevelsCompleted( 1 );

        // The level after the one we completed is enabled
        assertThat(
            menuItemsToStrings(
                LevelsMenu.menuItems( "test2", levelsCompleted ) ),
            equalTo(
                new String[] {
                    "Level 1 test2/lev1.rel",
                    "Level 2 test2/lev2.rel",
                    "Level 3 test2/lev3.rel (disabled)"
                }
            )
        );
    }

    @Test
    public void List_all_levels_in_a_dir_when_completed_all_but_last()
    {
        LevelsCompleted levelsCompleted = new HardCodedLevelsCompleted( 2 );

        // All levels are enabled because we've completed the penultimate one
        assertThat(
            menuItemsToStrings(
                LevelsMenu.menuItems( "test2", levelsCompleted ) ),
            equalTo(
                new String[] {
                    "Level 1 test2/lev1.rel",
                    "Level 2 test2/lev2.rel",
                    "Level 3 test2/lev3.rel"
                }
            )
        );
    }

    @Test
    public void List_all_levels_in_a_dir_when_completed_all()
    {
        LevelsCompleted levelsCompleted = new HardCodedLevelsCompleted( 3 );

        // All levels are enabled because we've completed them all
        assertThat(
            menuItemsToStrings(
                LevelsMenu.menuItems( "test2", levelsCompleted ) ),
            equalTo(
                new String[] {
                    "Level 1 test2/lev1.rel",
                    "Level 2 test2/lev2.rel",
                    "Level 3 test2/lev3.rel"
                }
            )
        );
    }

    // ---

    private static class HardCodedLevelsCompleted implements LevelsCompleted
    {
        private final int highest;

        public HardCodedLevelsCompleted( int highest )
        {
            this.highest = highest;
        }

        @Override
        public void setCompletedLevel( String levelsDir, int levelNum )
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public int highestLevelCompleted( String levelsDir )
        {
            return highest;
        }
    }

    private String[] menuItemsToStrings( MenuItem[] menuItems )
    {
        String[] ret = new String[menuItems.length];

        int i = 0;
        for ( MenuItem genericItem : menuItems )
        {
            LevelMenuItem item = (LevelMenuItem)genericItem;

            // Note don't use t() here since this is a test and we want
            // the same answer in all locales
            String ans = NamedFieldFormatter.format(
                item.name, item.nameParams );
            ans += " " + item.fileName;
            if ( !item.enabled )
            {
                ans += " (disabled)";
            }
            ret[i] = ans;
            ++i;
        }

        return ret;
    }
}

package rabbitescape.engine.menu;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.*;

import static rabbitescape.engine.i18n.Translation.*;

public class TestLevelsMenu
{
    @Test@Ignore  // Not finished
    public void List_all_levels_in_a_dir()
    {
        LevelsCompleted levelsCompleted = new LevelsCompleted()
        {
            @Override
            public void setCompletedLevel( String levelsDir, int levelNum )
            {
                throw new UnsupportedOperationException();
            }

            @Override
            public int highestLevelCompleted( String levelsDir )
            {
                return 0;
            }
        };

        assertThat(
            menuItemsToStrings(
                LevelsMenu.menuItems( "test2", levelsCompleted ) ),
            equalTo(
                new String[] {
                    "Level 1 test2/lev1.rel",
                    "Level 2 test2/lev2.rel (disabled)"
                }
            )
        );
    }

    private String[] menuItemsToStrings( MenuItem[] menuItems )
    {
        String[] ret = new String[menuItems.length];

        int i = 0;
        for ( MenuItem genericItem : menuItems )
        {
            LevelMenuItem item = (LevelMenuItem)genericItem;

            ret[i] = t( item.name, item.nameParams ) + " " + item.fileName;
            ++i;
        }

        return ret;
    }
}

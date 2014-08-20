package rabbitescape.engine.menu;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import static rabbitescape.engine.util.Translation.*;

public class TestLevelsMenu
{
    @Test
    public void List_all_levels_in_a_dir()
    {
        assertThat(
            menuItemsToStrings( LevelsMenu.menuItems( "test2" ) ),
            equalTo(
                new String[] {
                    "Level 1 /rabbitescape/levels/test2/lev1",
                    "Level 2 /rabbitescape/levels/test2/lev2"
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

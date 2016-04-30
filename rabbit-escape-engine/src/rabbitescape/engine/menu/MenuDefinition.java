package rabbitescape.engine.menu;

import static rabbitescape.engine.menu.MenuConstruction.*;
import static rabbitescape.engine.util.Util.*;

import rabbitescape.engine.menu.LevelsList.LevelSetInfo;
import rabbitescape.engine.menu.MenuItem.Type;
import rabbitescape.engine.util.Util.IdxObj;

public class MenuDefinition
{
    public static final LevelsList allLevels = new LevelsList(
        new LevelSetInfo( "Easy",     "01_easy",     null ),
        new LevelSetInfo( "Medium",   "02_medium",   null ),
        new LevelSetInfo( "Hard",     "03_hard",     null ),
        new LevelSetInfo( "Outdoors", "04_outdoors", null ),
        new LevelSetInfo( "Arcade",   "05_arcade",   null )
    );

    public static Menu mainMenu(
        LevelsCompleted levelsCompleted,
        LevelsList loadedLevels,
        boolean includeLoadLevel
    )
    {
        return menu(
            "Welcome to Rabbit Escape!",
            item(
                "Start Game",
                menu(
                    "Choose a set of levels:",
                    items( levelsCompleted, loadedLevels )
                ),
                true
            ),
            item( "About", Type.ABOUT, true ),
            maybeItem(
                includeLoadLevel,
                "Custom Levels",
                menu(
                    "Get from file or network",
                    item( "Load Level", Type.LOAD, true ),
                    item( "GitHub Issue", Type.GITHUB_ISSUE, true )
                ),
                true
            ),
            item( "Quit", Type.QUIT,  true )
        );
    }

    private static MenuItem[] items(
        LevelsCompleted levelsCompleted,
        LevelsList loadedLevels
    )
    {
        MenuItem[] ret = new MenuItem[ loadedLevels.size() ];
        for ( IdxObj<LevelSetInfo> setI : enumerate( loadedLevels ) )
        {
            LevelSetInfo set = setI.object;
            ret[setI.index] = item(
                set.name,
                new LevelsMenu( set.dirName, loadedLevels, levelsCompleted ),
                true
            );
        }
        return ret;
    }
}

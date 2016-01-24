package rabbitescape.engine.menu;

import static rabbitescape.engine.menu.MenuConstruction.*;
import static rabbitescape.engine.menu.MenuTargets.*;
import rabbitescape.engine.menu.MenuItem.Type;

public class MenuDefinition
{
    public static Menu mainMenu( LevelsCompleted levelsCompleted )
    {
        return mainMenu( levelsCompleted, true );
    }

    public static Menu mainMenu(
        LevelsCompleted levelsCompleted, boolean includeLoadLevel )
    {
        return menu(
            "Welcome to Rabbit Escape!",
            item(
                "Start Game",
                menu(
                    "Choose a set of levels:",
                    item( "Easy",     levels( "01_easy",     levelsCompleted ), true ),
                    item( "Medium",   levels( "02_medium",   levelsCompleted ), true ),
                    item( "Hard",     levels( "03_hard",     levelsCompleted ), true ),
                    item( "Outdoors", levels( "04_outdoors", levelsCompleted ), true ),
                    item( "Arcade",   levels( "05_arcade",   levelsCompleted ), true )
                ),
                true
            ),
            item( "About",      Type.ABOUT, true ),
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
            item( "Quit",       Type.QUIT,  true )
        );
    }
}

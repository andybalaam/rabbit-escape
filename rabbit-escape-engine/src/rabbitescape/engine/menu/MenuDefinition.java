package rabbitescape.engine.menu;

import static rabbitescape.engine.menu.MenuConstruction.*;
import static rabbitescape.engine.menu.MenuTargets.*;
import rabbitescape.engine.menu.MenuItem.Type;

public class MenuDefinition
{
    public static Menu mainMenu( LevelsCompleted levelsCompleted )
    {
        return menu(
            "Welcome to Rabbit Escape!",
            item(
                "Start Game",
                menu(
                    "Choose a set of levels:",
                    item( "Easy",     levels( "easy",     levelsCompleted ), true ),
                    item( "Medium",   levels( "medium",   levelsCompleted ), true ),
                    item( "Hard",     levels( "hard",     levelsCompleted ), true ),
                    item( "Outdoors", levels( "outdoors", levelsCompleted ), true )
                ),
                true
            ),
            item( "About", Type.ABOUT, true ),
            item( "Demo",  Type.DEMO,  false ),
            item( "Quit",  Type.QUIT,  true )
        );
    }
}

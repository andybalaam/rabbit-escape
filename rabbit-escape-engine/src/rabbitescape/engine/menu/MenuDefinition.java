package rabbitescape.engine.menu;

import static rabbitescape.engine.menu.MenuConstruction.*;
import static rabbitescape.engine.menu.MenuTargets.*;
import rabbitescape.engine.menu.MenuItem.Type;

public class MenuDefinition
{
    public static Menu mainMenu = menu(
        "Welcome to Rabbit Escape!",
        item(
            "Start Game",
            menu(
                "Choose a set of levels:",
                item( "Easy",   levels( "easy" ) ),
                item( "Medium", levels( "medium" ) ),
                item( "Hard",   levels( "hard" ) )
            )
        ),
        item( "About", Type.ABOUT ),
        item( "Demo",  Type.DEMO ),
        item( "Quit",  Type.QUIT )
    );
}

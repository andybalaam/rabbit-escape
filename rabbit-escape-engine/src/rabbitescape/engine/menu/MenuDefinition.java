package rabbitescape.engine.menu;

import static rabbitescape.engine.menu.MenuConstruction.*;
import static rabbitescape.engine.menu.MenuTargets.*;

public class MenuDefinition
{
    public enum ItemType
    {
        MENU,
        ABOUT,
        DEMO,
        QUIT,
    }

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
        item( "About", ItemType.ABOUT ),
        item( "Demo",  ItemType.DEMO ),
        item( "Quit",  ItemType.QUIT )
    );
}

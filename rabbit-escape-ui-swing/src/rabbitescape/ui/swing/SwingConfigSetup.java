package rabbitescape.ui.swing;

import rabbitescape.engine.config.Config;
import rabbitescape.engine.util.RealFileSystem;

public class SwingConfigSetup
{
    private static final String CONFIG_PATH =
        "~/.rabbitescape/config/ui.properties"
            .replace( "~", System.getProperty( "user.home" ) );

    public static final String CFG_GAME_WINDOW_LEFT   = "game.window.left";
    public static final String CFG_GAME_WINDOW_TOP    = "game.window.top";
    public static final String CFG_GAME_WINDOW_WIDTH  = "game.window.width";
    public static final String CFG_GAME_WINDOW_HEIGHT = "game.window.height";
    public static final String CFG_MENU_WINDOW_LEFT   = "menu.window.left";
    public static final String CFG_MENU_WINDOW_TOP    = "menu.window.top";
    public static final String CFG_MENU_WINDOW_WIDTH  = "menu.window.width";
    public static final String CFG_MENU_WINDOW_HEIGHT = "menu.window.height";
    public static final String CFG_LEVELS_COMPLETED   = "levels.completed";
    public static final String CFG_MUTED = "muted";

    public static Config createConfig()
    {
        Config.Definition definition = new Config.Definition();

        definition.set(
            CFG_MENU_WINDOW_LEFT,
            String.valueOf( Integer.MIN_VALUE ),
            "The x position of the menu window on the screen"
        );
        definition.set(
            CFG_MENU_WINDOW_TOP,
            String.valueOf( Integer.MIN_VALUE ),
            "The y position of the menu window on the screen"
        );
        definition.set(
            CFG_MENU_WINDOW_WIDTH,
            String.valueOf( Integer.MIN_VALUE ),
            "The width of the menu window on the screen"
        );
        definition.set(
            CFG_MENU_WINDOW_HEIGHT,
            String.valueOf( Integer.MIN_VALUE ),
            "The height of the menu window on the screen"
        );

        definition.set(
            CFG_GAME_WINDOW_LEFT,
            String.valueOf( Integer.MIN_VALUE ),
            "The x position of the game window on the screen"
        );
        definition.set(
            CFG_GAME_WINDOW_TOP,
            String.valueOf( Integer.MIN_VALUE ),
            "The y position of the game window on the screen"
        );
        definition.set(
            CFG_GAME_WINDOW_WIDTH,
            String.valueOf( Integer.MIN_VALUE ),
            "The width of the game window on the screen"
        );
        definition.set(
            CFG_GAME_WINDOW_HEIGHT,
            String.valueOf( Integer.MIN_VALUE ),
            "The height of the game window on the screen"
        );

        definition.set(
            CFG_LEVELS_COMPLETED,
            "",
            "Which level you have got to in each level set."
        );

        definition.set(
            CFG_MUTED,
            String.valueOf( false ),
            "Disable all sound"
        );

        return new Config( definition, new RealFileSystem(), CONFIG_PATH );
    }
}

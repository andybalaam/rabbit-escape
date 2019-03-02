package rabbitescape.ui.swing2

import rabbitescape.engine.config.*
import rabbitescape.engine.util.RealFileSystem

import javax.swing.*

import rabbitescape.engine.config.ConfigKeys.CFG_LOAD_LEVEL_PATH

object Swing2ConfigSetup {
    private val CONFIG_PATH = "~/.rabbitescape/config/ui.properties"
        .replace("~", System.getProperty("user.home"))

    const val CFG_GAME_WINDOW_LEFT = "game.window.left"
    const val CFG_GAME_WINDOW_TOP = "game.window.top"
    const val CFG_GAME_WINDOW_WIDTH = "game.window.width"
    const val CFG_GAME_WINDOW_HEIGHT = "game.window.height"
    private const val CFG_CLICK_THRESHOLD_MS = "click.threshold.ms"
    private const val DEPRECATED_CFG_MENU_WINDOW_LEFT = "menu.window.left"
    private const val DEPRECATED_CFG_MENU_WINDOW_TOP = "menu.window.top"
    private const val DEPRECATED_CFG_MENU_WINDOW_WIDTH = "menu.window.width"
    private const val DEPRECATED_CFG_MENU_WINDOW_HEIGHT = "menu.window.height"
    private const val CFG_MUTED = "muted"

    fun createConfig(
        storage: IConfigStorage =
            ConfigFile(
                RealFileSystem(),
                CONFIG_PATH
            )
    ): Config {
        val definition = ConfigSchema()
        StandardConfigSchema.setSchema(definition)

        definition.set(
            DEPRECATED_CFG_MENU_WINDOW_LEFT,
            Integer.MIN_VALUE.toString(),
            "deprecated"
        )
        definition.set(
            DEPRECATED_CFG_MENU_WINDOW_TOP,
            Integer.MIN_VALUE.toString(),
            "deprecated"
        )
        definition.set(
            DEPRECATED_CFG_MENU_WINDOW_WIDTH,
            Integer.MIN_VALUE.toString(),
            "deprecated"
        )
        definition.set(
            DEPRECATED_CFG_MENU_WINDOW_HEIGHT,
            Integer.MIN_VALUE.toString(),
            "deprecated"
        )

        definition.set(
            CFG_GAME_WINDOW_LEFT,
            Integer.MIN_VALUE.toString(),
            "The x position of the game window on the screen"
        )
        definition.set(
            CFG_GAME_WINDOW_TOP,
            Integer.MIN_VALUE.toString(),
            "The y position of the game window on the screen"
        )
        definition.set(
            CFG_GAME_WINDOW_WIDTH,
            Integer.MIN_VALUE.toString(),
            "The width of the game window on the screen"
        )
        definition.set(
            CFG_GAME_WINDOW_HEIGHT,
            Integer.MIN_VALUE.toString(),
            "The height of the game window on the screen"
        )

        definition.set(
            CFG_CLICK_THRESHOLD_MS,
            300.toString(),
            "Time in ms. Longer than this are drags.")

        definition.set(
            CFG_MUTED,
            false.toString(),
            "Disable all sound"
        )

        definition.set(
            CFG_LOAD_LEVEL_PATH,
            JFileChooser().currentDirectory.absolutePath,
            "Default path in the dialog to load a level file for testing."
        )

        return Config(
            definition,
            storage,
            *RealConfigUpgrades.realConfigUpgrades()
        )
    }
}

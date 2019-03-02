package rabbitescape.ui.swing2

import rabbitescape.engine.config.Config
import rabbitescape.engine.config.ConfigTools
import rabbitescape.ui.swing2.Swing2ConfigSetup.CFG_GAME_WINDOW_HEIGHT
import rabbitescape.ui.swing2.Swing2ConfigSetup.CFG_GAME_WINDOW_LEFT
import rabbitescape.ui.swing2.Swing2ConfigSetup.CFG_GAME_WINDOW_TOP
import rabbitescape.ui.swing2.Swing2ConfigSetup.CFG_GAME_WINDOW_WIDTH
import java.awt.Dimension
import java.awt.Point
import javax.swing.JFrame
import javax.swing.JPanel

class MainWindow : JFrame("Rabbit Escape") {

    private val mode: MainWindowMode = MainWindowMode.TITLE
    private val config: Config = Swing2ConfigSetup.createConfig()

    init {
        iconImage = ImageReader.read("images32/ic_launcher.png")
        defaultCloseOperation = EXIT_ON_CLOSE
        locationFromConfig(config)
        sizeFromConfig(config)
        add(modePanel(mode))
        pack()
    }

    private fun modePanel(mode: MainWindowMode): JPanel {
        return when (mode) {
            MainWindowMode.TITLE -> TitleScreen
        }
    }

    private fun sizeFromConfig(config: Config) {
        val width  = ConfigTools.getInt( config, CFG_GAME_WINDOW_WIDTH )
        val height = ConfigTools.getInt( config, CFG_GAME_WINDOW_HEIGHT )
        if ( width != Integer.MIN_VALUE && height != Integer.MIN_VALUE )
        {
            preferredSize = Dimension(width, height)
        }
    }

    private fun locationFromConfig(config: Config) {
        val x = ConfigTools.getInt( config, CFG_GAME_WINDOW_LEFT )
        val y = ConfigTools.getInt( config, CFG_GAME_WINDOW_TOP )
        if (x != Integer.MIN_VALUE && y != Integer.MIN_VALUE) {
            location = Point(x, y)
        }
    }
}

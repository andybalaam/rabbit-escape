package rabbitescape.ui.swing2

import java.awt.*
import java.awt.image.BufferStrategy

abstract class BufferedDraw2(private val strategy: BufferStrategy) {

    internal abstract fun draw(graphics: Graphics2D)

    internal fun run() {
        do {
            do {
                try {
                    val g = strategy.drawGraphics as Graphics2D
                    try {
                        draw(g)
                    } finally {
                        g.dispose()
                    }
                } catch (e: IllegalStateException) {
                    // Display has gone away - nothing to do
                    return
                } catch (e: NullPointerException) {
                    // Can get this from within getDrawGraphics()
                    // While resizing when not paused.
                    // Ignoring it seems harmless.
                    return
                }
            } while (strategy.contentsRestored())

            try {
                strategy.show()
            } catch (e: IllegalStateException) {
                // Display has gone away - nothing to do
                return
            }
        } while (strategy.contentsLost())
    }
}

package rabbitescape.ui.swing2

import java.awt.Canvas
import java.awt.Graphics2D
import java.awt.LayoutManager
import java.awt.image.BufferStrategy
import java.lang.Double.max
import javax.swing.GroupLayout
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.SwingUtilities

object TitleScreen : JPanel() {

    var bgOffsetX = -0.2
    var bgOffsetY = -0.3
    var bgOverzoom = 2.0

    private val backgroundImage = ImageReader.read(
        "images-other/background.png")

    private var canvas: Canvas = Canvas()

    init {
        layout = createTopLayout()
        val drawTitle = DrawTitle(canvas.bufferStrategy, canvas)

        Thread() {

            SwingUtilities.invokeLater(drawTitle::run)

        }.start()
    }

    private fun createTopLayout(): LayoutManager {
        val ret = GroupLayout(this)

        ret.autoCreateContainerGaps = true

        val buttonsPanel = JPanel()

        ret.setHorizontalGroup(
            ret.createParallelGroup()
                .addComponent(buttonsPanel)
                .addComponent(canvas)
        )

        ret.setVerticalGroup(
            ret.createParallelGroup()
                .addComponent(buttonsPanel)
                .addComponent(canvas)
        )

        buttonsPanel.layout = createButtonsLayout(buttonsPanel)

        return ret
    }

    private fun createButtonsLayout(buttonsPanel: JPanel): GroupLayout {
        val ret = GroupLayout(buttonsPanel)

        val button = JButton("foo")

        ret.setHorizontalGroup(
            ret.createSequentialGroup()
                .addComponent(button)
        )
        ret.setVerticalGroup(
            ret.createSequentialGroup()
                .addComponent(button)
        )

        return ret
    }

    class DrawTitle(
        strategy : BufferStrategy,
        private val canvas: Canvas
    ) : BufferedDraw2(strategy)
    {
        override fun draw(graphics : Graphics2D)
        {
            val dstx1 = 0
            val dsty1 = 0
            val dstx2 = canvas.width
            val dsty2 = canvas.height

            val bgZoom = (
                bgOverzoom *
                    max(
                        dstx2 / backgroundImage.width.toDouble(),
                        dsty2 / backgroundImage.height.toDouble()
                    )
                )

            val srcx1 = (-bgOffsetX * backgroundImage.width).toInt()
            val srcy1 = (-bgOffsetY * backgroundImage.height).toInt()
            val srcx2 = srcx1 + (dstx2 / bgZoom).toInt()
            val srcy2 = srcy1 + (dsty2 / bgZoom).toInt()

            graphics.drawImage(
                backgroundImage,
                dstx1, dsty1, dstx2, dsty2,
                srcx1, srcy1, srcx2, srcy2,
                null
            )
        }
    }
    private fun moveBackground() {
        bgOffsetX -= 0.0001
        if (bgOffsetX < -1.0) {
            bgOffsetX = 1.0
        }
        bgOffsetY -= 0.00001
        if (bgOffsetY < -1.0) {
            bgOffsetY = 1.0
        }
        bgOverzoom += 0.0001
        if (bgOverzoom > 3.0) {
            bgOverzoom = 2.0
        }
    }
}

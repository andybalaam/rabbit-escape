package rabbitescape.ui.swing2

import java.awt.*
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener
import java.awt.image.BufferedImage
import java.util.Random
import javax.swing.*

private val font = Font("Courier New", Font.PLAIN, 12)

fun main()
{
    val app = JFrame()
    app.ignoreRepaint = true
    app.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    app.setBounds(50, 50, 400, 350)

    val canvas = Canvas()
    canvas.ignoreRepaint = true

    val button1 = JButton("foo")
    val button2 = JButton("bar")

    app.add(button1)
    app.add(button2)
    app.add(canvas)
    button1.setBounds(100, 100, 150, 50)
    button2.setBounds(100, 200, 150, 50)

    val resizeListener = ResizeListener(
        GraphicsEnvironment
            .getLocalGraphicsEnvironment()
            .defaultScreenDevice
            .defaultConfiguration,
        canvas
    )
    app.addComponentListener(resizeListener)

    app.isVisible = true
    canvas.createBufferStrategy(2)
    val buffer = canvas.bufferStrategy


    val backgroundImage = ImageReader.read("images-other/background.png")
    val background = Color.BLACK
    val rand = Random()

    var fps = 0
    var frames = 0
    var totalTime: Long = 0
    var curTime: Long = System.currentTimeMillis()
    var lastTime: Long

    while (true)
    {
        lastTime = curTime
        curTime = System.currentTimeMillis()
        totalTime += curTime - lastTime
        if (totalTime > 1000)
        {
            totalTime -= 1000
            fps = frames
            frames = 0
        }
        ++frames

        val info = resizeListener.info()
        val bi = info.bufferedImage
        val g2d = bi.createGraphics()
        try
        {
            g2d.color = background
            g2d.fillRect(0, 0, info.width, info.height)

            // draw some rectangles...
            for (i in 0 until 2)
            {
                val r = rand.nextInt(256)
                val g = rand.nextInt(256)
                val b = rand.nextInt(256)
                g2d.color = Color(r, g, b)
                val x = rand.nextInt(info.width / 2)
                val y = rand.nextInt(info.height / 2)
                val w = rand.nextInt(info.width / 2)
                val h = rand.nextInt(info.height / 2)
                g2d.fillRect(x, y, w, h)
            }
            g2d.drawImage(
                backgroundImage,
                0, 0,// info.width, info.height,
                //0, 0, 400, 300,
                null
            )

            button1.text = fps.toString()
            g2d.font = font
            g2d.color = Color.GREEN
            g2d.drawString(String.format("FPS: %s", fps), 20, 20)

            val graphics = buffer.drawGraphics
            try
            {
                graphics.drawImage(bi, 0, 0, null)
                if (!buffer.contentsLost())
                {
                    buffer.show()
                }
            }
            finally
            {
                graphics.dispose()
            }
        }
        finally
        {
            g2d.dispose()
        }

        Thread.yield()
    }
}

data class SizeDependentInfo(
    val bufferedImage: BufferedImage,
    val width: Int,
    val height: Int
)

class ResizeListener(
    private val graphicsConfig : GraphicsConfiguration,
    private val canvas: Canvas
) : ComponentListener
{
    private var sizeDependentInfo: SizeDependentInfo? = null

    override fun componentMoved(event: ComponentEvent) = Unit
    override fun componentHidden(event: ComponentEvent) = Unit
    override fun componentShown(event: ComponentEvent) = Unit

    override fun componentResized(event: ComponentEvent)
    {
        sizeDependentInfo = null
    }

    fun info(): SizeDependentInfo {
        val ret = sizeDependentInfo
        return if (ret != null)
        {
            ret
        }
        else
        {
            val newSizeDependentInfo = calcSizeDependentInfo()
            sizeDependentInfo = newSizeDependentInfo
            newSizeDependentInfo
        }
    }

    private fun calcSizeDependentInfo(): SizeDependentInfo
    {
        val s = canvas.size

        return SizeDependentInfo(
            graphicsConfig.createCompatibleImage(s.width, s.height),
            s.width,
            s.height
        )
    }
}

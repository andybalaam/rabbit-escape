package rabbitescape.ui.swing2

import java.awt.image.BufferedImage
import javax.imageio.ImageIO

object ImageReader {
    fun read(subPath: String): BufferedImage {
        return ImageIO.read(
            javaClass.getResource("/rabbitescape/ui/swing2/$subPath")
        )
    }
}

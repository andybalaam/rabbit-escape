package rabbitescape.ui.swing;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import rabbitescape.render.BitmapScaler;

public class SwingBitmapScaler implements BitmapScaler<SwingBitmap>
{
    @Override
    public SwingBitmap scale( SwingBitmap originalBitmap, double scale )
    {
        SwingBitmap orig = originalBitmap;
        BufferedImage origImage = orig.image;

        int width  = (int)( origImage.getWidth()  * scale );
        int height = (int)( origImage.getHeight() * scale );

        BufferedImage ret = new java.awt.image.BufferedImage(
            width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB );

        Graphics2D gfx = ret.createGraphics();

        gfx.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BILINEAR
        );

        gfx.drawImage(
            origImage,
            0, 0, width, height,
            0, 0, origImage.getWidth(), origImage.getHeight(),
            null
        );

        return new SwingBitmap( originalBitmap.name(), ret );
    }
}

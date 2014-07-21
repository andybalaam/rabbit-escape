package rabbitescape.ui.swing;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import rabbitescape.render.BitmapScaler;
import rabbitescape.render.androidlike.Bitmap;

public class SwingBitmapScaler implements BitmapScaler
{
    @Override
    public Bitmap scale( Bitmap originalBitmap, int tileSize )
    {
        SwingBitmap orig = (SwingBitmap)originalBitmap;
        BufferedImage origImage = orig.image;

        int width = tileSize;
        int height = tileSize;

        BufferedImage ret = new java.awt.image.BufferedImage(
            tileSize, tileSize, java.awt.image.BufferedImage.TYPE_INT_RGB );

        Graphics2D gfx = ret.createGraphics();

        gfx.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
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

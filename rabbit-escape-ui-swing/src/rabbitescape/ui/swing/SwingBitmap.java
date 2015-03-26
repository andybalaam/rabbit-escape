package rabbitescape.ui.swing;

import rabbitescape.render.androidlike.Bitmap;

public class SwingBitmap implements Bitmap
{
    private final String name;
    public final java.awt.image.BufferedImage image;

    public SwingBitmap(
        String name, java.awt.image.BufferedImage image )
    {
        this.name = name;
        this.image = image;
    }

    public SwingBitmap( String name, int w, int h )
    {
        this.name = name;
        this.image = new java.awt.image.BufferedImage(
            w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB );
    }

    @Override
    public String name()
    {
        return name;
    }

    @Override
    public void recycle()
    {
        // We don't do anything here in the Swing world
    }
}

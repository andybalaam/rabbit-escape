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
            w, h, java.awt.image.BufferedImage.TYPE_INT_RGB );
    }

    @Override
    public String name()
    {
        return name;
    }

    @Override
    public void drawBitmap( Bitmap bitmap, int x, int y )
    {
        SwingBitmap bmp = (SwingBitmap)bitmap;

        image.getRaster().setRect( x, y, bmp.image.getData() );
    }
}

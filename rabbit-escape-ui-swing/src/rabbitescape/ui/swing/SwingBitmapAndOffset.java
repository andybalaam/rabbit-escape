package rabbitescape.ui.swing;

import rabbitescape.render.ScaledBitmap;

public class SwingBitmapAndOffset
{
    public final int offsetX;
    public final ScaledBitmap<SwingBitmap> bitmap;
    public final int offsetY;

    public SwingBitmapAndOffset(
        ScaledBitmap<SwingBitmap> bitmap, int offsetX, int offsetY )
    {
        this.bitmap = bitmap;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
}

package rabbitescape.ui.swing;

import rabbitescape.render.androidlike.Bitmap;

public class SwingBitmapAndOffset
{
    public final Bitmap bitmap;
    public final int offsetX;
    public final int offsetY;

    public SwingBitmapAndOffset( SwingBitmap bitmap, int offsetX, int offsetY )
    {
        this.bitmap = bitmap;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
}

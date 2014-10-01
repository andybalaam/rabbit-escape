package rabbitescape.ui.android;

import rabbitescape.render.androidlike.Bitmap;

public class AndroidBitmapAndOffset
{
    public final AndroidBitmap bitmap;
    public final int offsetX;
    public final int offsetY;

    public AndroidBitmapAndOffset( AndroidBitmap bitmap, int offsetX, int offsetY )
    {
        this.bitmap = bitmap;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
}

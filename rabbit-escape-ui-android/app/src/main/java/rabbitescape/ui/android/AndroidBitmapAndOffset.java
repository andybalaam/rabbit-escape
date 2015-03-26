package rabbitescape.ui.android;

import rabbitescape.render.ScaledBitmap;
import rabbitescape.render.androidlike.Bitmap;

public class AndroidBitmapAndOffset
{
    public final ScaledBitmap<AndroidBitmap> bitmap;
    public final int offsetX;
    public final int offsetY;

    public AndroidBitmapAndOffset( ScaledBitmap<AndroidBitmap> bitmap, int offsetX, int offsetY )
    {
        this.bitmap = bitmap;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
}

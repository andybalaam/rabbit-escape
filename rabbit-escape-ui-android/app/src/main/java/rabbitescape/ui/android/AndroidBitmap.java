package rabbitescape.ui.android;

import rabbitescape.render.androidlike.Bitmap;

public class AndroidBitmap implements Bitmap
{
    public final android.graphics.Bitmap bitmap;

    public AndroidBitmap( android.graphics.Bitmap bitmap )
    {
        this.bitmap = bitmap;
    }

    @Override
    public String name()
    {
        return null;
    }

    @Override
    public void recycle()
    {
        bitmap.recycle();
    }
}

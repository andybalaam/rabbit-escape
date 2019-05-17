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
    public int width()
    {
        return bitmap.getWidth();
    }

    @Override
    public int height()
    {
        return bitmap.getHeight();
    }

    @Override
    public long getByteCount()
    {
        return bitmap.getRowBytes() * bitmap.getHeight();
        // API level 12+: return bitmap.getByteCount();
    }

    @Override
    public void recycle()
    {
        // Prevent recycling this bitmap while we're drawing it
        synchronized ( bitmap )
        {
            bitmap.recycle();
        }
    }
}

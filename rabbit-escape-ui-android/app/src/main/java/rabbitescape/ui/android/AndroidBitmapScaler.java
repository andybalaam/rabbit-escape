package rabbitescape.ui.android;

import rabbitescape.render.BitmapScaler;
import rabbitescape.render.androidlike.Bitmap;

public class AndroidBitmapScaler implements BitmapScaler
{
    @Override
    public Bitmap scale( Bitmap originalBitmap, double scale )
    {
        // On Android, the bitmaps got scaled for us automatically, so we
        // do nothing here.
        return originalBitmap;
    }
}

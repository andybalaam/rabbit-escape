package rabbitescape.ui.android;

import rabbitescape.render.BitmapScaler;

public class AndroidBitmapScaler implements BitmapScaler<AndroidBitmap>
{
    @Override
    public AndroidBitmap scale( AndroidBitmap originalBitmap, double scale )
    {
        // On Android, the bitmaps got scaled for us automatically, so we
        // do nothing here.
        return originalBitmap;
    }
}

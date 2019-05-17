package rabbitescape.ui.android;

import android.graphics.Bitmap;

import rabbitescape.render.BitmapScaler;

import static rabbitescape.engine.util.Util.reAssert;

public class AndroidBitmapScaler implements BitmapScaler<AndroidBitmap>
{
    @Override
    public AndroidBitmap scale( AndroidBitmap originalBitmap, double scale )
    {
        reAssert( scale != 1.0 );

        Bitmap origImage = originalBitmap.bitmap;

        int width  = (int)( origImage.getWidth() * scale );
        int height = (int)( origImage.getHeight() * scale );

        return new AndroidBitmap(
            Bitmap.createScaledBitmap( origImage, width, height, true ) );
    }
}

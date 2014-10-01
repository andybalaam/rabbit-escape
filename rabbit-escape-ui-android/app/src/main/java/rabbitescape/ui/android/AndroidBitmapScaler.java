package rabbitescape.ui.android;

import rabbitescape.render.BitmapScaler;
import rabbitescape.render.androidlike.Bitmap;

public class AndroidBitmapScaler implements BitmapScaler
{
    @Override
    public Bitmap scale( Bitmap originalBitmap, double scale )
    {
        if ( scale != 1.0 )
        {
            throw new RuntimeException( "TODO: scaling bitmaps on Android" );
        }
        return originalBitmap;
    }
}

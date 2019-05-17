package rabbitescape.render;

import rabbitescape.render.androidlike.Bitmap;

public interface BitmapScaler<T extends Bitmap>
{
    T scale( T originalBitmap, double scale );
}

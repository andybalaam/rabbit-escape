package rabbitescape.render;

import rabbitescape.render.androidlike.Bitmap;

public interface BitmapScaler
{
    Bitmap scale( Bitmap originalBitmap, int tileSize );
}

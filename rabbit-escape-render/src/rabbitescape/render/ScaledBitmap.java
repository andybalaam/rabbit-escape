package rabbitescape.render;

import rabbitescape.render.androidlike.Bitmap;

public class ScaledBitmap<T extends Bitmap>
{
    private final T originalBitmap;
    public final int originalTileSize;

    private final BitmapScaler<T> scaler;

    public T bitmap;
    public int tileSize;

    public ScaledBitmap( BitmapScaler<T> scaler, T bitmap, int tileSize )
    {
        this.scaler = scaler;
        this.originalBitmap = bitmap;
        this.bitmap = bitmap;
        this.originalTileSize = tileSize;
        this.tileSize = tileSize;
    }

    public void scaleTo( int tileSize )
    {
        if ( this.tileSize != tileSize )
        {
            double scale = (double)tileSize / (double)originalTileSize;
            this.bitmap = scaler.scale( this.originalBitmap, scale );
            this.tileSize = tileSize;
        }
    }

    public void recycle()
    {
        if ( bitmap != originalBitmap )
        {
            bitmap.recycle();
        }
        originalBitmap.recycle();
    }
}

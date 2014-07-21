package rabbitescape.render;

import rabbitescape.render.androidlike.Bitmap;

/**
 * A Bitmap and a location at which to draw it.
 */
public class Sprite
{
    private final Bitmap originalBitmap;
    private final BitmapScaler scaler;
    public final int tileX;
    public final int tileY;

    public Bitmap bitmap;
    private int tileSize;

    public Sprite(
        Bitmap bitmap,
        BitmapScaler scaler,
        int tileX,
        int tileY,
        int initialTileSize
    )
    {
        this.originalBitmap = bitmap;
        this.scaler = scaler;
        this.tileX = tileX;
        this.tileY = tileY;
        this.tileSize = -1;

        scaleTo( this.tileSize );
    }

    public void scaleTo( int tileSize )
    {
        if ( this.tileSize != tileSize )
        {
            this.bitmap = scaler.scale( this.originalBitmap, tileSize );
            this.tileSize = tileSize;
        }
    }
}

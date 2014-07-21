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
    private final int originalTileSize;
    private final int originalOffsetX;
    private final int originalOffsetY;

    public Bitmap bitmap;
    private int tileSize;
    public int offsetX;
    public int offsetY;

    public Sprite(
        Bitmap bitmap,
        BitmapScaler scaler,
        int tileX,
        int tileY,
        int tileSize,
        int offsetX,
        int offsetY
    )
    {
        this.originalBitmap = bitmap;
        this.scaler = scaler;
        this.tileX = tileX;
        this.tileY = tileY;
        this.originalTileSize = tileSize;
        this.originalOffsetX = offsetX;
        this.originalOffsetY = offsetY;

        this.tileSize = -1;
        scaleTo( tileSize );
    }

    public void scaleTo( int tileSize )
    {
        if ( this.tileSize != tileSize )
        {
            this.bitmap = scaler.scale( this.originalBitmap, tileSize );
            double scale = (double)tileSize / (double)originalTileSize;
            offsetX = (int)( originalOffsetX * scale );
            offsetY = (int)( originalOffsetY * scale );
            this.tileSize = tileSize;
        }
    }
}

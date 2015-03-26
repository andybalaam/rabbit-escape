package rabbitescape.render;

import rabbitescape.render.androidlike.Bitmap;

/**
 * A Bitmap and a location at which to draw it.
 */
public class Sprite<T extends Bitmap>
{
    public final ScaledBitmap<T> bitmap;
    public final int tileX;
    public final int tileY;

    private final int originalOffsetX;
    private final int originalOffsetY;

    public int offsetX;
    public int offsetY;

    public Sprite(
        ScaledBitmap<T> bitmap, int tileX, int tileY, int offsetX, int offsetY )
    {
        this.tileX = tileX;
        this.tileY = tileY;
        this.originalOffsetX = offsetX;
        this.originalOffsetY = offsetY;

        this.bitmap = bitmap;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public void scaleTo( int tileSize )
    {
        if ( bitmap.tileSize != tileSize )
        {
            double scale = (double)tileSize / (double)bitmap.originalTileSize;
            offsetX = (int)( originalOffsetX * scale );
            offsetY = (int)( originalOffsetY * scale );

            bitmap.scaleTo( tileSize );
        }
    }
}

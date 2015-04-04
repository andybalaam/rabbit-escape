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

    private final int originalOffsetX; // TODO: move offsets into ScaledBitmap?
    private final int originalOffsetY;

    public int offsetX;
    public int offsetY;

    /**
     * @param bitmap
     * @param tileX
     * @param tileY
     * @param offsetX x offset (relative to 32x32 image)
     * @param offsetY y offset (relative to 32x32 image)
     */
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
            // Ensure we have called bitmap here so we can use unscaledTileSize.
            // This is why offsets should move.
            bitmap.bitmap( tileSize );
        }

        double scale = tileSize / 32.0;
        offsetX = (int)( originalOffsetX * scale );
        offsetY = (int)( originalOffsetY * scale );
    }
}

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

    private final int offset32X; // X offset relative to a 32x32 image
    private final int offset32Y; // X offset relative to a 32x32 image

    /**
     * @param bitmap
     * @param tileX
     * @param tileY
     * @param offset32X x offset (relative to 32x32 image)
     * @param offset32Y y offset (relative to 32x32 image)
     */
    public Sprite(
        ScaledBitmap<T> bitmap,
        int tileX,
        int tileY,
        int offset32X,
        int offset32Y
    )
    {
        this.tileX = tileX;
        this.tileY = tileY;
        this.offset32X = offset32X;
        this.offset32Y = offset32Y;

        this.bitmap = bitmap;
    }

    public int offsetX( int tileSize )
    {
        return (int)( offset32X * ( tileSize / 32.0 ) );
    }

    public int offsetY( int tileSize )
    {
        return (int)( offset32Y * ( tileSize / 32.0 ) );
    }
}

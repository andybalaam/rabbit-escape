package rabbitescape.render;

import rabbitescape.engine.util.Position;

/**
 * A Bitmap name and a location at which to draw it.
 */
public class Sprite
{
    public final String bitmapName;
    public final String soundEffect;
    public final int tileX;
    public final int tileY;

    private final int offset32X; // X offset relative to a 32x32 image
    private final int offset32Y; // X offset relative to a 32x32 image

    public Sprite(
        String bitmapName,
        String soundEffect,
        Position tilePos,
        Position offset
    )
    {
        this(
            bitmapName,
            soundEffect,
            tilePos.x,
            tilePos.y,
            offset.x,
            offset.y
        );
    }

    /**
     * @param bitmapName
     * @param tileX
     * @param tileY
     * @param offset32X x offset (relative to 32x32 image)
     * @param offset32Y y offset (relative to 32x32 image)
     */
    public Sprite(
        String bitmapName,
        String soundEffect,
        int tileX,
        int tileY,
        int offset32X,
        int offset32Y
    )
    {
        this.bitmapName = bitmapName;
        this.soundEffect = soundEffect;
        this.tileX = tileX;
        this.tileY = tileY;

        this.offset32X = offset32X;
        this.offset32Y = offset32Y;
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

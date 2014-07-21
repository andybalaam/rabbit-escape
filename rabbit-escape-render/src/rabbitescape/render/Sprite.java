package rabbitescape.render;

import rabbitescape.render.androidlike.Bitmap;

/**
 * A Bitmap and a location at which to draw it.
 */
public class Sprite
{
    public final Bitmap bitmap;
    public final int x;
    public final int y;

    public Sprite( Bitmap bitmap, int x, int y )
    {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
    }
}

package rabbitescape.render.androidlike;

/**
 * Something like android.graphics.Bitmap.
 */
public interface Bitmap
{
    public String name();
    public int width();
    public int height();

    /**
     * Reclaim the memory of this bitmap, if possible on this platform.
     */
    public void recycle();
}

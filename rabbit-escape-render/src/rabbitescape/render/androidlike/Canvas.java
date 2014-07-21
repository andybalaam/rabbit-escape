package rabbitescape.render.androidlike;

/**
 * Something a bit like android.graphics.Canvas - provides calls like
 * drawBitmap that we use to draw our UI.
 */
public interface Canvas
{
    public void drawBitmap( Bitmap bitmap, float left, float top, Paint paint );
}

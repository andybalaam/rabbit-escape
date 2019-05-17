package rabbitescape.render.androidlike;

/**
 * Something a bit like android.graphics.Canvas - provides calls like
 * drawBitmap that we use to draw our UI.
 */
public interface Canvas<T extends Bitmap, P extends Paint>
{
    public void drawBitmap( T bitmap, float left, float top, P paint );
    public int width();
    public int height();
    public void drawColor( P paint );
    public void drawLine(
        float startX, float startY, float stopX, float stopY, P paint );
    public void drawPath( Path path, P paint );
    public void drawRect(Rect rect, P paint);
    public void drawText(String text, float x, float y, P paint);
}

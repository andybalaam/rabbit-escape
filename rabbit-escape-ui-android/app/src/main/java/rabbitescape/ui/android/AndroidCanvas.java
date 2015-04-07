package rabbitescape.ui.android;

import rabbitescape.render.androidlike.Bitmap;
import rabbitescape.render.androidlike.Canvas;
import rabbitescape.render.androidlike.Paint;

public class AndroidCanvas implements Canvas
{
    private final android.graphics.Canvas canvas;

    public AndroidCanvas( android.graphics.Canvas canvas )
    {
        this.canvas = canvas;
    }

    @Override
    public void drawBitmap( Bitmap bitmap, float left, float top, Paint paint )
    {
        android.graphics.Bitmap realBitmap = ( (AndroidBitmap)bitmap ).bitmap;
        android.graphics.Paint realPaint   = (  (AndroidPaint)paint  ).paint;

        canvas.drawBitmap( realBitmap, left, top, realPaint );
    }

    @Override
    public int width()
    {
        return canvas.getWidth();
    }

    @Override
    public int height()
    {
        return canvas.getHeight();
    }
}

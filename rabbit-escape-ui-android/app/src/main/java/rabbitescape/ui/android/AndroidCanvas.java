package rabbitescape.ui.android;

import rabbitescape.render.androidlike.Canvas;

public class AndroidCanvas implements Canvas<AndroidBitmap, AndroidPaint>
{
    private final android.graphics.Canvas canvas;

    public AndroidCanvas( android.graphics.Canvas canvas )
    {
        this.canvas = canvas;
    }

    @Override
    public void drawBitmap( AndroidBitmap bitmap, float left, float top, AndroidPaint paint )
    {
        canvas.drawBitmap( bitmap.bitmap, left, top, paint.paint );
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

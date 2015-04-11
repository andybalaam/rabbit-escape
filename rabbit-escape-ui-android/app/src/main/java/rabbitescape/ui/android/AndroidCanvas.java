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

    @Override
    public void drawColor( AndroidPaint paint )
    {
        canvas.drawColor( paint.paint.getColor() );
    }

    @Override
    public void drawLine(
        float startX, float startY, float stopX, float stopY, AndroidPaint paint
    )
    {
        this.canvas.drawLine( startX, startY, stopX, stopY, paint.paint );
    }
}

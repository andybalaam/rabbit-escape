package rabbitescape.ui.android;

import android.graphics.Paint;
import android.graphics.Path;

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
        this.canvas.drawLine(startX, startY, stopX, stopY, paint.paint);
    }

    @Override
    public void drawFilledPoly( int[] xs, int[] ys, AndroidPaint paint )
    {
        if ( xs.length == 0 )
        {
            return;
        }
        paint.paint.setStyle( Paint.Style.FILL );
        Path p = new Path();
        p.moveTo( (float)xs[0], (float)ys[0]);
        for ( int i = 1; i < xs.length ; i++ )
        {
            p.lineTo( (float)xs[i], (float)ys[i] );
        }
        p.close();
        canvas.drawPath( p, paint.paint );
    }
}

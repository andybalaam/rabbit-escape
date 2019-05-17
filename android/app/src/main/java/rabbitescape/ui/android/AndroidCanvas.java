package rabbitescape.ui.android;

import android.graphics.Paint;
import android.graphics.Path;

import java.util.Iterator;
import java.util.List;

import rabbitescape.render.Vertex;
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
        // v0.10.1 crashed on Nexus 10 in some levels with
        // "java.lang.RuntimeException: Canvas: trying to use a recycled bitmap"
        // This lock and check should protect us from using a bitmap that has been recycled.
        // I added the same lock into AndroidBitmap.recycle, so we should not get a race
        // condition here.
        // See https://github.com/andybalaam/rabbit-escape/issues/476
        synchronized ( bitmap.bitmap )
        {
            if ( !bitmap.bitmap.isRecycled() ) {
                canvas.drawBitmap(bitmap.bitmap, left, top, paint.paint);
            }
        }
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
    public void drawPath( rabbitescape.render.androidlike.Path rePath,
                          AndroidPaint paint )
    {
        List<Vertex> vertices = rePath.getVertices();

        if ( vertices.size() == 0 )
        {
            return;
        }
        android.graphics.Path aPath = new android.graphics.Path();
        Iterator<Vertex> iterator = vertices.iterator();
        Vertex start = vertices.get( 0 );
        aPath.moveTo( start.x, start.y );
        for ( int i = 1 ; i < vertices.size() ; i++ )
        {
            Vertex v = vertices.get( i );
            aPath.lineTo( v.x, v.y );
        }
        canvas.drawPath( aPath, paint.paint );
    }

    public void drawRect( rabbitescape.render.androidlike.Rect rect, AndroidPaint paint )
    {
        this.canvas.drawRect( new android.graphics.Rect( rect.left, rect.top, rect.right, rect.bottom ) , paint.paint );
    }

    public void drawText( String text, float x, float y, AndroidPaint paint )
    {
        this.canvas.drawText( text, x, y, paint.paint);
    }
}

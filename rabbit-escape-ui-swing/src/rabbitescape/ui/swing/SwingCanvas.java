package rabbitescape.ui.swing;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;

import rabbitescape.render.androidlike.Canvas;

public class SwingCanvas implements Canvas<SwingBitmap, SwingPaint>
{
    private final Graphics2D gfx;
    private final int width;
    private final int height;

    public SwingCanvas( Graphics2D gfx, int width, int height )
    {
        this.gfx = gfx;
        this.width = width;
        this.height = height;
    }

    @Override
    public void drawBitmap(
        SwingBitmap bitmap, float left, float top, SwingPaint paint )
    {
        this.gfx.setComposite( AlphaComposite.SrcAtop );
        this.gfx.drawImage( bitmap.image, (int)left, (int)top, null );
    }

    @Override
    public int width()
    {
        return width;
    }

    @Override
    public int height()
    {
        return height;
    }

    @Override
    public void drawColor( SwingPaint paint )
    {
        this.gfx.setPaint( paint.color );
        this.gfx.fillRect( 0, 0, width, height );
    }

    @Override
    public void drawLine(
        float startX,
        float startY,
        float stopX,
        float stopY,
        SwingPaint paint )
    {
        this.gfx.setPaint( paint.color );
        this.gfx.drawLine( (int)startX, (int)startY, (int)stopX, (int)stopY );
    }

    @Override
    public void drawFilledPoly( int[] xs, int[] ys, SwingPaint paint )
    {
        this.gfx.setPaint( paint.color );
        this.gfx.fillPolygon( xs, ys, xs.length );
    }
}

package rabbitescape.ui.swing;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;

import rabbitescape.render.androidlike.Bitmap;
import rabbitescape.render.androidlike.Canvas;
import rabbitescape.render.androidlike.Paint;

public class SwingCanvas implements Canvas
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
    public void drawBitmap( Bitmap bitmap, float left, float top, Paint paint )
    {
        SwingBitmap b = (SwingBitmap)bitmap;

        this.gfx.setComposite( AlphaComposite.SrcAtop );
        this.gfx.drawImage( b.image, (int)left, (int)top, null );
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
}

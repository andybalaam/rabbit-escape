package rabbitescape.ui.swing;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;

import rabbitescape.render.androidlike.Bitmap;
import rabbitescape.render.androidlike.Canvas;
import rabbitescape.render.androidlike.Paint;

public class SwingCanvas implements Canvas
{
    public final Graphics2D gfx;

    public SwingCanvas( Graphics2D gfx )
    {
        this.gfx = gfx;
    }

    @Override
    public void drawBitmap( Bitmap bitmap, float left, float top, Paint paint )
    {
        SwingBitmap b = (SwingBitmap)bitmap;

        this.gfx.setComposite( AlphaComposite.SrcAtop );
        this.gfx.drawImage( b.image, (int)left, (int)top, null );
    }
}

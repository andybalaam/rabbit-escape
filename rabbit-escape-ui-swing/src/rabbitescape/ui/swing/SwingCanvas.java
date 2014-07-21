package rabbitescape.ui.swing;

import rabbitescape.render.androidlike.Bitmap;
import rabbitescape.render.androidlike.Canvas;
import rabbitescape.render.androidlike.Paint;

public class SwingCanvas implements Canvas
{
    public final SwingBitmap bitmap;

    public SwingCanvas( SwingBitmap bitmap )
    {
        this.bitmap = bitmap;
    }

    @Override
    public void drawBitmap( Bitmap bitmap, float left, float top, Paint paint )
    {
        this.bitmap.drawBitmap( bitmap, (int)left, (int)top );
    }
}

package rabbitescape.render;

import rabbitescape.render.androidlike.Canvas;
import rabbitescape.render.androidlike.Paint;

public class Renderer
{
    private final Canvas canvas;
    private final int offsetX;
    private final int offsetY;

    public Renderer( Canvas canvas, int offsetX, int offsetY )
    {
        this.canvas = canvas;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public void render( Sprite[] sprites, Paint paint )
    {
        for ( Sprite sprite : sprites )
        {
            this.canvas.drawBitmap(
                sprite.bitmap,
                offsetX + sprite.x * 32,
                offsetY + sprite.y * 32,
                paint
            );
        }
    }
}

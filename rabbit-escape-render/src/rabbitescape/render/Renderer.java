package rabbitescape.render;

import rabbitescape.render.androidlike.Canvas;
import rabbitescape.render.androidlike.Paint;

public class Renderer
{
    private final Canvas canvas;

    public Renderer( Canvas canvas )
    {
        this.canvas = canvas;
    }

    public void render( Sprite[] sprites, Paint paint )
    {
        for ( Sprite sprite : sprites )
        {
            this.canvas.drawBitmap(
                sprite.bitmap, sprite.x * 32, sprite.y * 32, paint );
        }
    }
}

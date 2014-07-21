package rabbitescape.render;

import rabbitescape.render.androidlike.Canvas;
import rabbitescape.render.androidlike.Paint;

public class Renderer
{
    private final Canvas canvas;
    private final int offsetX;
    private final int offsetY;
    private final int tileSize;

    public Renderer( Canvas canvas, int offsetX, int offsetY, int tileSize )
    {
        this.canvas = canvas;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.tileSize = tileSize;
    }

    public void render( Sprite[] sprites, Paint paint )
    {
        for ( Sprite sprite : sprites )
        {
            sprite.scaleTo( tileSize );

            this.canvas.drawBitmap(
                sprite.bitmap,
                offsetX + sprite.tileX * tileSize,
                offsetY + sprite.tileY * tileSize,
                paint
            );
        }
    }
}

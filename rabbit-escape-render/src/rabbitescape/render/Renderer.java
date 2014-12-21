package rabbitescape.render;

import rabbitescape.render.androidlike.Canvas;
import rabbitescape.render.androidlike.Paint;

public class Renderer
{
    private int offsetX;
    private int offsetY;
    private final int tileSize;

    public Renderer( int offsetX, int offsetY, int tileSize )
    {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.tileSize = tileSize;
    }

    public void render( Canvas canvas, Sprite[] sprites, Paint paint )
    {
        for ( Sprite sprite : sprites )
        {
            sprite.scaleTo( tileSize );

            canvas.drawBitmap(
                sprite.bitmap,
                sprite.offsetX + offsetX + ( sprite.tileX * tileSize ),
                sprite.offsetY + offsetY + ( sprite.tileY * tileSize ),
                paint
            );
        }
    }

    public void setOffset( int offsetX, int offsetY )
    {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
}

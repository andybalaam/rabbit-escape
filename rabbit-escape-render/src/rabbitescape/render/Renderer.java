package rabbitescape.render;

import java.util.List;

import rabbitescape.render.androidlike.Bitmap;
import rabbitescape.render.androidlike.Canvas;
import rabbitescape.render.androidlike.Paint;

public class Renderer<T extends Bitmap>
{
    public int offsetX;
    public int offsetY;
    public final int tileSize;

    public Renderer( int offsetX, int offsetY, int tileSize )
    {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.tileSize = tileSize;
    }

    public void render( Canvas canvas, List<Sprite<T>> sprites, Paint paint )
    {
        for ( Sprite<T> sprite : sprites )
        {
            sprite.scaleTo( tileSize );

            canvas.drawBitmap(
                sprite.bitmap.bitmap,
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

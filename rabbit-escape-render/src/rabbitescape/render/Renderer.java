package rabbitescape.render;

import java.util.List;

import rabbitescape.render.androidlike.Bitmap;
import rabbitescape.render.androidlike.Canvas;
import rabbitescape.render.androidlike.Paint;

public class Renderer<T extends Bitmap>
{
    public int offsetX;
    public int offsetY;
    public int tileSize;

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
            T bitmap = sprite.bitmap.bitmap( tileSize );

            int left = sprite.offsetX( tileSize )
                + offsetX + ( sprite.tileX * tileSize );

            int top = sprite.offsetY( tileSize )
                + offsetY + ( sprite.tileY * tileSize );

            int right  = left + bitmap.width();
            int bottom = top  + bitmap.height();

            if (
                   right > 0
                && bottom > 0
                && left < canvas.width()
                && top < canvas.height()
            )
            {
                canvas.drawBitmap( bitmap, left, top, paint );
            }
        }
    }

    public void setOffset( int offsetX, int offsetY )
    {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
}

package rabbitescape.render;

import java.util.List;

import rabbitescape.render.androidlike.Bitmap;
import rabbitescape.render.androidlike.Canvas;
import rabbitescape.render.androidlike.Paint;

public class Renderer<T extends Bitmap, P extends Paint>
{
    public int offsetX;
    public int offsetY;
    public int tileSize;
    private final BitmapCache<T> bitmapCache;

    public Renderer(
        int offsetX,
        int offsetY,
        int tileSize,
        BitmapCache<T> bitmapCache
    )
    {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.tileSize = tileSize;
        this.bitmapCache = bitmapCache;
    }

    public void render( Canvas<T, P> canvas, List<Sprite> sprites, P paint )
    {
        for ( Sprite sprite : sprites )
        {
            if ( sprite.bitmapName != null )
            {
                drawSprite( canvas, sprite, paint );
            }
        }
    }

    private void drawSprite( Canvas<T, P> canvas, Sprite sprite, P paint )
    {
        T bitmap = bitmapCache.get( sprite.bitmapName, tileSize );

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

    public void setOffset( int offsetX, int offsetY )
    {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
}

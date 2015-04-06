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
            int posX = sprite.offsetX( tileSize )
                + offsetX + ( sprite.tileX * tileSize );

            int posY = sprite.offsetY( tileSize )
                + offsetY + ( sprite.tileY * tileSize );

            canvas.drawBitmap(
                sprite.bitmap.bitmap( tileSize ), posX, posY, paint );
        }
    }

    public void setOffset( int offsetX, int offsetY )
    {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
}

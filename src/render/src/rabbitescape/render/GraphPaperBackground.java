package rabbitescape.render;

import rabbitescape.engine.World;
import rabbitescape.render.androidlike.Bitmap;
import rabbitescape.render.androidlike.Canvas;
import rabbitescape.render.androidlike.Paint;

public class GraphPaperBackground
{
    public static <T extends Bitmap, P extends Paint> void drawBackground(
        World world,
        Renderer<T,P> renderer,
        Canvas<T, P> canvas,
        P white,
        P graphPaperMajor,
        P graphPaperMinor
    )
    {
        canvas.drawColor( white );

        int minTile = -2;
        int maxTileX = world.size.width + 2;
        int maxTileY = world.size.height + 2;
        int minX = renderer.offsetX + ( minTile  * renderer.tileSize );
        int maxX = renderer.offsetX + ( maxTileX * renderer.tileSize );
        int minY = renderer.offsetY + ( minTile  * renderer.tileSize );
        int maxY = renderer.offsetY + ( maxTileY * renderer.tileSize );
        double inc = renderer.tileSize / 4.0;

        for( int x = minX; x < maxX; x += renderer.tileSize )
        {
            for ( int sub = 1; sub < 4; ++sub )
            {
                int dx = (int)( x + ( sub * inc ) );
                canvas.drawLine( dx, minY, dx, maxY, graphPaperMinor );
            }
        }
        for( int y = minY; y < maxY; y += renderer.tileSize )
        {
            for ( int sub = 1; sub < 4; ++sub )
            {
                int dy = (int)( y + ( sub * inc ) );
                canvas.drawLine( minX, dy, maxX, dy, graphPaperMinor );
            }
        }

        for( int x = minX; x <= maxX; x += renderer.tileSize )
        {
            canvas.drawLine( x, minY, x, maxY, graphPaperMajor );
        }
        for( int y = minY; y <= maxY; y += renderer.tileSize )
        {
            canvas.drawLine( minX, y, maxX, y, graphPaperMajor );
        }
    }
}

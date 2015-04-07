package rabbitescape.ui.android;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import rabbitescape.engine.World;
import rabbitescape.render.AnimationCache;
import rabbitescape.render.AnimationLoader;
import rabbitescape.render.BitmapCache;
import rabbitescape.render.Renderer;
import rabbitescape.render.SpriteAnimator;

public class Graphics
{
    private final World world;
    private final AnimationCache animationCache;
    private final BitmapCache<AndroidBitmap> bitmapCache;
    private final AndroidPaint paint;
    public final int renderingTileSize;
    public final int levelWidthPixels;
    public final int levelHeightPixels;
    private static final Paint graphPaperMajor = new Paint( Paint.ANTI_ALIAS_FLAG );
    private static final Paint graphPaperMinor = new Paint( Paint.ANTI_ALIAS_FLAG );

    static
    {
        graphPaperMajor.setColor( Color.rgb( 205, 212, 220 ) );
        graphPaperMinor.setColor( Color.rgb( 235, 243, 255 ) );
    }

    public Graphics( BitmapCache<AndroidBitmap> bitmapCache, World world, float displayDensity )
    {
        this.world = world;
        this.bitmapCache = bitmapCache;
        this.animationCache = new AnimationCache( new AnimationLoader() );
        this.paint = new AndroidPaint( new Paint() );
        this.renderingTileSize = (int)( 32 * displayDensity );
        this.levelWidthPixels = renderingTileSize * world.size.width;
        this.levelHeightPixels = renderingTileSize * world.size.height;
    }

    public void draw( Canvas canvas, int offsetX, int offsetY, int frameNum )
    {
        AndroidCanvas androidCanvas = new AndroidCanvas( canvas );
        Renderer renderer = new Renderer( offsetX, offsetY, renderingTileSize );

        SpriteAnimator<AndroidBitmap> animator = new SpriteAnimator<AndroidBitmap>(
            world, bitmapCache, animationCache );

        drawBackground( renderer, canvas );

        renderer.render(
            androidCanvas,
            animator.getSprites( frameNum ),
            paint
        );
    }

    private void drawBackground( Renderer renderer, Canvas canvas )
    {
        // TODO: share with SwingGameLoop.DrawFrame
        canvas.drawColor( Color.WHITE );

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

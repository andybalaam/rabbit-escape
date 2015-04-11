package rabbitescape.ui.android;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import rabbitescape.engine.World;
import rabbitescape.render.AnimationCache;
import rabbitescape.render.AnimationLoader;
import rabbitescape.render.BitmapCache;
import rabbitescape.render.GraphPaperBackground;
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

    private static final AndroidPaint white = makePaint( Color.WHITE );

    private static final AndroidPaint graphPaperMajor =
        makePaint( Color.rgb( 205, 212, 220 ), Paint.ANTI_ALIAS_FLAG );

    private static final AndroidPaint graphPaperMinor =
        makePaint( Color.rgb( 235, 243, 255 ), Paint.ANTI_ALIAS_FLAG );

    private static AndroidPaint makePaint( int color )
    {
        Paint p = new Paint();
        p.setColor( color );
        return new AndroidPaint( p );
    }

    private static AndroidPaint makePaint( int color, int flags )
    {
        Paint p = new Paint( flags );
        p.setColor( color );
        return new AndroidPaint( p );
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
        Renderer<AndroidBitmap, AndroidPaint> renderer =
            new Renderer<AndroidBitmap, AndroidPaint>( offsetX, offsetY, renderingTileSize );

        SpriteAnimator<AndroidBitmap> animator = new SpriteAnimator<AndroidBitmap>(
            world, bitmapCache, animationCache );

        GraphPaperBackground.drawBackground(
            world, renderer, androidCanvas, white, graphPaperMajor, graphPaperMinor );

        renderer.render(
            androidCanvas,
            animator.getSprites( frameNum ),
            paint
        );
    }
}

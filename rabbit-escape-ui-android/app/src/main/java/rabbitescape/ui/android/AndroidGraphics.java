package rabbitescape.ui.android;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;

import rabbitescape.engine.World;
import rabbitescape.render.AnimationCache;
import rabbitescape.render.AnimationLoader;
import rabbitescape.render.BitmapCache;
import rabbitescape.render.GraphPaperBackground;
import rabbitescape.render.Renderer;
import rabbitescape.render.SpriteAnimator;

public class AndroidGraphics implements Graphics
{
    private final BitmapCache<AndroidBitmap> bitmapCache;
    private final World world;
    private final SurfaceHolder surfaceHolder;
    private final AnimationCache animationCache;
    private final AndroidPaint paint;
    public final int renderingTileSize;
    public final int levelWidthPixels;
    public final int levelHeightPixels;

    private int screenWidthPixels;
    private int screenHeightPixels;
    private int prevScrollX;
    private int prevScrollY;
    private boolean checkScroll;

    public int scrollX;
    public int scrollY;

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

    public AndroidGraphics(
        BitmapCache<AndroidBitmap> bitmapCache,
        World world,
        SurfaceHolder surfaceHolder,
        float displayDensity,
        int scrollX,
        int scrollY
    )
    {
        this.bitmapCache = bitmapCache;
        this.world = world;
        this.surfaceHolder = surfaceHolder;
        this.scrollX = scrollX;
        this.scrollY = scrollY;

        this.animationCache = new AnimationCache( new AnimationLoader() );
        this.paint = new AndroidPaint( new Paint() );
        this.renderingTileSize = (int)( 32 * displayDensity );
        this.levelWidthPixels = renderingTileSize * world.size.width;
        this.levelHeightPixels = renderingTileSize * world.size.height;

        this.checkScroll = true;
        this.screenWidthPixels = 100; // Will be set properly when we draw
        this.screenHeightPixels = 100;
    }

    @Override
    public void draw( int frame )
    {
        Canvas canvas = surfaceHolder.lockCanvas();

        if ( canvas == null )
        {
            return;
        }

        try
        {
            synchronized ( surfaceHolder )
            {
                actuallyDrawGraphics( canvas, frame );
            }
        }
        finally
        {
            surfaceHolder.unlockCanvasAndPost( canvas );
        }
    }

    @Override
    public void rememberScrollPos()
    {
        prevScrollX = scrollX;
        prevScrollY = scrollY;
    }

    @Override
    public void drawIfScrolled( int frame )
    {
        if ( prevScrollX != scrollX || prevScrollY != scrollY )
        {
            draw( frame );
            prevScrollX = scrollX;
            prevScrollY = scrollY;
        }
    }

    private void actuallyDrawGraphics( Canvas canvas, int frame )
    {
        screenWidthPixels = canvas.getWidth();
        screenHeightPixels = canvas.getHeight();
        if ( checkScroll )
        {
            scrollBy( 0, 0 );
            checkScroll = false;
        }

        drawToCanvas( canvas, -scrollX, -scrollY, frame );
    }

    private void drawToCanvas( Canvas canvas, int offsetX, int offsetY, int frameNum )
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

    public void scrollBy( float x, float y )
    {
        scrollX += x;
        scrollY += y;

        if ( levelWidthPixels < screenWidthPixels )
        {
            scrollX = -( screenWidthPixels - levelWidthPixels ) / 2;
        }
        else if ( scrollX < 0  )
        {
            scrollX = 0;
        }
        else if ( scrollX > levelWidthPixels - screenWidthPixels )
        {
            scrollX = levelWidthPixels - screenWidthPixels;
        }

        if ( levelHeightPixels < screenHeightPixels )
        {
            scrollY = -( screenHeightPixels - levelHeightPixels ) / 2;
        }
        else if ( scrollY < 0 )
        {
            scrollY = 0;
        }
        else if ( scrollY > levelHeightPixels - screenHeightPixels )
        {
            scrollY = levelHeightPixels - screenHeightPixels;
        }
    }
}

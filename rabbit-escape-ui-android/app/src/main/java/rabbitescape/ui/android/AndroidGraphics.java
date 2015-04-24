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
import rabbitescape.render.gameloop.Graphics;

public class AndroidGraphics implements Graphics
{
    private static final int MIN_INITIAL_TILE_SIZE = 32;
    private static final int MIN_TILE_SIZE = 16;

    private final BitmapCache<AndroidBitmap> bitmapCache;
    private final World world;
    private final SurfaceHolder surfaceHolder;
    private final AnimationCache animationCache;
    private final AndroidPaint paint;
    public int renderingTileSize;
    public int levelWidthPixels;
    public int levelHeightPixels;

    private int screenWidthPixels;
    private int screenHeightPixels;
    private int prevScrollX;
    private int prevScrollY;

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

        // These will be set properly when we draw, so we know screen size
        this.screenWidthPixels  = -1;
        this.screenHeightPixels = -1;
        this.renderingTileSize  = -1;
        this.levelWidthPixels   = -1;
        this.levelHeightPixels  = -1;
    }

    private int initialTileSize()
    {
        // Try to fit the whole level on screen
        int retX = screenWidthPixels  / world.size.width;
        int retY = screenHeightPixels / world.size.height;

        int ret = ( retX < retY ) ? retX : retY;

        if ( ret < MIN_INITIAL_TILE_SIZE )
        {
            ret = MIN_INITIAL_TILE_SIZE;
        }

        return ret;
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
        if (
               screenWidthPixels  != canvas.getWidth()
            || screenHeightPixels != canvas.getHeight()
        )
        {
            screenWidthPixels  = canvas.getWidth();
            screenHeightPixels = canvas.getHeight();
            setRenderingTileSize(  initialTileSize() );
            levelWidthPixels  = renderingTileSize * world.size.width;
            levelHeightPixels = renderingTileSize * world.size.height;
            scrollBy( 0, 0 );
        }

        drawToCanvas( canvas, -scrollX, -scrollY, frame );
    }

    private void setRenderingTileSize( int newSize )
    {
        // Make sure size >= 16 and at least 5 tiles are visible in each direction

        if ( newSize < MIN_TILE_SIZE )
        {
            this.renderingTileSize = MIN_TILE_SIZE;
        }
        else
        {
            int maxSize = maxSize();
            if ( newSize > maxSize )
            {
                this.renderingTileSize = maxSize;
            }
            else
            {
                this.renderingTileSize = newSize;
            }
        }
    }

    private int maxSize()
    {
        // One fifth of the shortest screen dimension
        // i.e. no less than five tiles are visible in each direction
        int retX = screenWidthPixels / 5;
        int retY = screenHeightPixels / 5;
        return ( retX < retY ) ? retX : retY;
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

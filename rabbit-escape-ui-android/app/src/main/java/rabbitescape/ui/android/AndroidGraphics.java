package rabbitescape.ui.android;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import rabbitescape.engine.World;
import rabbitescape.render.AnimationCache;
import rabbitescape.render.AnimationLoader;
import rabbitescape.render.BitmapCache;
import rabbitescape.render.GraphPaperBackground;
import rabbitescape.render.Polygon;
import rabbitescape.render.PolygonBuilder;
import rabbitescape.render.Renderer;
import rabbitescape.render.SoundPlayer;
import rabbitescape.render.Sprite;
import rabbitescape.render.SpriteAnimator;
import rabbitescape.render.gameloop.Graphics;
import rabbitescape.render.gameloop.WaterDynamics;

public class AndroidGraphics implements Graphics
{
    private static final float MIN_INITIAL_TILE_SIZE = 32f;
    private static final float MIN_TILE_SIZE = 16f;

    private final BitmapCache<AndroidBitmap> bitmapCache;
    private final SoundPlayer soundPlayer;
    private final World world;
    private final WaterDynamics waterDynamics;
    private final AnimationCache animationCache;
    private final AndroidPaint paint;

    /**
     * Set when the surface becomes available.
     */
    public SurfaceHolder surfaceHolder;

    public float renderingTileSize;
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


    private static final AndroidPaint waterColor =
            makePaint( Color.argb( 100, 10, 100, 220 ), Paint.ANTI_ALIAS_FLAG );

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
        SoundPlayer soundPlayer,
        World world,
        WaterDynamics waterDynamics,
        int scrollX,
        int scrollY
    )
    {
        this.bitmapCache = bitmapCache;
        this.soundPlayer = soundPlayer;
        this.world = world;
        this.waterDynamics = waterDynamics;
        this.scrollX = scrollX;
        this.scrollY = scrollY;

        this.surfaceHolder = null;

        this.animationCache = new AnimationCache( new AnimationLoader() );
        this.paint = new AndroidPaint( new Paint() );

        // These will be set properly when we draw, so we know screen size
        this.screenWidthPixels  = -1;
        this.screenHeightPixels = -1;
        this.renderingTileSize  = -1;
        this.levelWidthPixels   = -1;
        this.levelHeightPixels  = -1;
    }

    private float initialTileSize()
    {
        // Try to fit the whole level on screen
        float retX = screenWidthPixels  / world.size.width;
        float retY = screenHeightPixels / world.size.height;

        float ret = ( retX < retY ) ? retX : retY;

        if ( ret < MIN_INITIAL_TILE_SIZE )
        {
            ret = MIN_INITIAL_TILE_SIZE;
        }

        return ret;
    }

    @Override
    public void draw( int frame )
    {
        if ( surfaceHolder == null )
        {
            System.err.println( "Error: AndroidGraphics - drawing without a surfaceHolder!" );
            return;
        }

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

    @Override
    public void dispose()
    {
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
            adjustRenderingTileSize( initialTileSize() );
        }

        drawToCanvas( canvas, -scrollX, -scrollY, frame );
    }

    public void adjustRenderingTileSize( float newSize )
    {
        // Make sure size >= 16 and at least 5 tiles are visible in each direction

        this.renderingTileSize = chooseRenderingTileSize( newSize );

        levelWidthPixels  = (int)( renderingTileSize * world.size.width );
        levelHeightPixels = (int)( renderingTileSize * world.size.height );
        scrollBy(0, 0);
    }

    private float chooseRenderingTileSize( float suggestedSize )
    {
        if ( suggestedSize < MIN_TILE_SIZE )
        {
            return MIN_TILE_SIZE;
        }
        else
        {
            float maxSize = maxSize();
            if ( suggestedSize > maxSize )
            {
                return maxSize;
            }
            else
            {
                return suggestedSize;
            }
        }
    }

    private float maxSize()
    {
        // One fifth of the shortest screen dimension
        // i.e. no less than five tiles are visible in each direction
        float retX = screenWidthPixels / 5;
        float retY = screenHeightPixels / 5;
        return ( retX < retY ) ? retX : retY;
    }

    private void drawToCanvas( Canvas canvas, int offsetX, int offsetY, int frameNum )
    {
        AndroidCanvas androidCanvas = new AndroidCanvas( canvas );
        Renderer<AndroidBitmap, AndroidPaint> renderer =
            new Renderer<AndroidBitmap, AndroidPaint>(
                offsetX, offsetY, (int)renderingTileSize, bitmapCache );

        SpriteAnimator animator = new SpriteAnimator( world, animationCache );

        GraphPaperBackground.drawBackground(
            world, renderer, androidCanvas, white, graphPaperMajor, graphPaperMinor );

        drawPolygons(waterDynamics.polygons, androidCanvas, renderer );

        List<Sprite> sprites = animator.getSprites( frameNum, waterDynamics );

        soundPlayer.play( sprites );

        renderer.render( androidCanvas, sprites, paint );
    }

    void drawPolygons( ArrayList<PolygonBuilder> polygons, AndroidCanvas androidCanvas, Renderer<AndroidBitmap, AndroidPaint> renderer )
    {
        float f = renderer.tileSize / 32f;
        for ( PolygonBuilder pb: polygons )
        {
            Polygon p = pb.polygon( f, renderer.offsetX, renderer.offsetY );
            androidCanvas.drawFilledPoly( p.x, p.y, waterColor );
        }
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

    public void scaleRenderingTileSize( float scaleFactor, float focusX, float focusY )
    {
        float newFocusX = scaleFactor * ( scrollX + focusX );
        float newFocusY = scaleFactor * ( scrollY + focusY );

        adjustRenderingTileSize( renderingTileSize * scaleFactor );

        float movedFocusX = scrollX + focusX;
        float movedFocusY = scrollY + focusY;

        scrollBy( newFocusX - movedFocusX, newFocusY - movedFocusY );
    }
}

package rabbitescape.ui.android;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;

import java.util.List;

import rabbitescape.engine.Thing;
import rabbitescape.engine.World;
import rabbitescape.engine.util.MathUtil;
import rabbitescape.engine.util.Util;
import rabbitescape.render.AnimationCache;
import rabbitescape.render.AnimationLoader;
import rabbitescape.render.BitmapCache;
import rabbitescape.render.GraphPaperBackground;
import rabbitescape.render.PolygonBuilder;
import rabbitescape.render.Overlay;
import rabbitescape.render.Renderer;
import rabbitescape.render.SoundPlayer;
import rabbitescape.render.Sprite;
import rabbitescape.render.SpriteAnimator;
import rabbitescape.render.Vertex;
import rabbitescape.render.WaterParticle;
import rabbitescape.render.WaterRegionRenderer;
import rabbitescape.render.androidlike.Rect;
import rabbitescape.render.gameloop.Graphics;
import rabbitescape.render.gameloop.WaterAnimation;

public class AndroidGraphics implements Graphics
{
    private static final float MIN_INITIAL_TILE_SIZE = 32f;
    private static final float MIN_TILE_SIZE = 16f;

    private final BitmapCache<AndroidBitmap> bitmapCache;
    private final SoundPlayer soundPlayer;
    private final World world;
    private final WaterAnimation waterAnimation;
    private final AnimationCache animationCache;
    private final AndroidPaint paint;

    /**
     * Set when the surface becomes available.
     */
    public SurfaceHolder surfaceHolder;

    public float renderingTileSize;
    private int levelWidthPixels;
    private int levelHeightPixels;

    private int screenWidthPixels;
    private int screenHeightPixels;
    private int prevScrollX;
    private int prevScrollY;

    public int scrollX;
    public int scrollY;

    private int lastFrame = -1;
    private boolean soundOn = true;

    private static final AndroidPaint white = makePaint( Color.WHITE );

    private static final AndroidPaint graphPaperMajor =
        makePaint( Color.rgb( 205, 212, 220 ), Paint.ANTI_ALIAS_FLAG );

    private static final AndroidPaint graphPaperMinor =
        makePaint( Color.rgb( 235, 243, 255 ), Paint.ANTI_ALIAS_FLAG );

    private static final AndroidPaint dullOverlay =
        makePaint( Color.argb( 200, 70, 70, 70 ) );

    private static final AndroidPaint greenText =
            makePaint( Color.rgb( 100, 255, 100 ), Paint.ANTI_ALIAS_FLAG );

    private static final int waterR = 130, waterG = 167, waterB = 221;

    private static final AndroidPaint waterColor =
        makePaint( Color.argb( 255, waterR, waterG, waterB ) );

    static
    {
        waterColor.paint.setStyle( Paint.Style.FILL );
    }

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

    AndroidGraphics(
        BitmapCache<AndroidBitmap> bitmapCache,
        SoundPlayer soundPlayer,
        World world,
        WaterAnimation waterAnimation,
        int scrollX,
        int scrollY
    )
    {
        this.bitmapCache = bitmapCache;
        this.soundPlayer = soundPlayer;
        this.world = world;
        this.waterAnimation = waterAnimation;
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
        lastFrame = frame;

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

    public void redraw()
    {
        if ( -1 == lastFrame )
        {
            return;
        }
        soundOn = false;
        draw( lastFrame );
        soundOn = true;
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

    private void adjustRenderingTileSize( float newSize )
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

        drawWater( waterAnimation, androidCanvas, renderer );

        List<Sprite> sprites = animator.getSprites( frameNum );

        if ( soundOn )
        {
            soundPlayer.play(sprites);
        }

        renderer.render(androidCanvas, sprites, paint);

        if ( world.paused )
        {
            tacticalOverlay( renderer, androidCanvas, world );
        }
    }

    private void tacticalOverlay( Renderer renderer, AndroidCanvas androidCanvas, World world )
    {
        androidCanvas.drawColor( dullOverlay );

        Overlay overlay = new Overlay( world );

        greenText.paint.setTextAlign( Paint.Align.CENTER );

        float h = renderer.tileSize / 4 ;
        greenText.paint.setTextSize( h );

        for ( Thing t : overlay.items ) {
            String notation = overlay.at(t.x, t.y);

            String[] lines = Util.split(notation, "\n");

            for (int i = 0; i < lines.length; i++)
            {
                int x = renderer.offsetX + t.x * renderer.tileSize;
                int y = renderer.offsetY + t.y * renderer.tileSize + (int)( (float)i * h );
                x += ( renderer.tileSize ) / 2; // centre
                y += ( renderer.tileSize - h * lines.length ) / 2 ;
                androidCanvas.drawText( lines[i], (float)x, (float)y, greenText);
            }
        }

    }

    private void drawWater(
        WaterAnimation wa,
        AndroidCanvas androidCanvas,
        Renderer<AndroidBitmap, AndroidPaint> renderer
    )
    {
        float f = renderer.tileSize / 32f;
        Vertex offset = new Vertex( renderer.offsetX, renderer.offsetY );

        // shade whole background sqaure cells with any water
        for ( WaterRegionRenderer wrr : wa.lookupRenderer) {
            Rect rect = new Rect(
                renderer.tileSize * wrr.getRegion().x + renderer.offsetX,
                renderer.tileSize * wrr.getRegion().y + renderer.offsetY,
                renderer.tileSize * wrr.getRegion().x + renderer.tileSize + renderer.offsetX,
                renderer.tileSize * wrr.getRegion().y + renderer.tileSize + renderer.offsetY
            );
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setARGB(wrr.backShadeAlpha(), waterR, waterG, waterB);
            androidCanvas.drawRect(rect, new AndroidPaint(paint) );
        }

        // draw polygons to represent pooled water
        for ( PolygonBuilder pb: wa.calculatePolygons() )
        {
            rabbitescape.render.androidlike.Path rePath = pb.path(
                    f, offset );
            androidCanvas.drawPath( rePath, waterColor );
        }

        // draw particles to represent falling water
        for ( WaterRegionRenderer wrr : wa.lookupRenderer )
        {
            for ( WaterParticle wp : wrr.particles )
            {
                Paint paint = new Paint();
                paint.setStyle(Paint.Style.FILL);
                paint.setARGB(wp.alpha, waterR, waterG, waterB);
                rabbitescape.render.androidlike.Path rePath =
                    wp.polygon().path( f, offset );
                androidCanvas.drawPath( rePath, new AndroidPaint(paint) );
            }
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

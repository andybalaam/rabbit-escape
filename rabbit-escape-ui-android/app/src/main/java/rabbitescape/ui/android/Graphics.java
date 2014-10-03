package rabbitescape.ui.android;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import rabbitescape.engine.World;
import rabbitescape.render.AnimationCache;
import rabbitescape.render.AnimationLoader;
import rabbitescape.render.BitmapCache;
import rabbitescape.render.Renderer;
import rabbitescape.render.Sprite;

public class Graphics
{
    private final World world;
    private final AnimationCache animationCache;
    private final BitmapCache<AndroidBitmap> bitmapCache;
    private final AndroidPaint paint;
    public final int renderingTileSize;
    public final int levelWidthPixels;
    public final int levelHeightPixels;

    public Graphics( BitmapCache<AndroidBitmap> bitmapCache, World world )
    {
        this.world = world;
        this.bitmapCache = bitmapCache;
        this.animationCache = new AnimationCache( new AnimationLoader() );
        this.paint = new AndroidPaint( new Paint() );
        this.renderingTileSize = 32;
        this.levelWidthPixels = renderingTileSize * world.size.width;
        this.levelHeightPixels = renderingTileSize * world.size.height;
    }

    public void draw( Canvas canvas, int offsetX, int offsetY, int frameNum )
    {
        int imagesTileSize = 32;

        AndroidCanvas androidCanvas = new AndroidCanvas( canvas );
        Renderer renderer = new Renderer( offsetX, offsetY, renderingTileSize );

        AndroidSpriteAnimator animator = new AndroidSpriteAnimator(
            world, world.describeChanges(), imagesTileSize, bitmapCache, animationCache );

        canvas.drawColor( Color.WHITE );

        renderer.render(
            androidCanvas,
            animator.getSprites( frameNum ),
            paint
        );
    }
}

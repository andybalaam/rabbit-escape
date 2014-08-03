package rabbitescape.ui.swing;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static rabbitescape.engine.Direction.*;

import rabbitescape.engine.Block;
import rabbitescape.engine.ChangeDescription;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.World;
import rabbitescape.render.Animation;
import rabbitescape.render.AnimationCache;
import rabbitescape.render.BitmapCache;
import rabbitescape.render.Sprite;

public class SpriteAnimator
{
    private final World world;
    private final SwingBitmapScaler scaler;
    private final String land_block;
    private final String land_rising_right;
    private final String land_rising_left;
    private final int tileSize;
    private final BitmapCache<SwingBitmap> bitmapCache;
    private final AnimationCache animationCache;

    public SpriteAnimator(
        World world,
        ChangeDescription changes,
        int tileSize,
        BitmapCache<SwingBitmap> bitmapCache,
        AnimationCache animationCache
    )
    {
        this.world = world;
        this.scaler = new SwingBitmapScaler();
        this.tileSize = tileSize;
        this.bitmapCache = bitmapCache;
        this.animationCache = animationCache;

        this.land_block = "/rabbitescape/ui/swing/images32/land-block.png";

        this.land_rising_right =
            "/rabbitescape/ui/swing/images32/land-rising-right.png";

        this.land_rising_left =
            "/rabbitescape/ui/swing/images32/land-rising-left.png";
    }

    public Sprite[] getSprites( int frameNum )
    {
        List<Sprite> ret = new ArrayList<Sprite>();

        for ( Block block : world.blocks )
        {
            ret.add(
                new Sprite(
                    bitmapForBlock( block ),
                    scaler,
                    block.x,
                    block.y,
                    tileSize,
                    0,
                    0
                )
            );
        }

        for ( Rabbit rabbit : world.rabbits )
        {
            String frame = rabbit.state.name().toLowerCase( Locale.ENGLISH );
            Animation animation = animationCache.get( frame );

            if ( animation == null )
            {
                continue;  // TODO: Throw here - we should find all animations
            }

            // TODO: don't make a new one of these every time?
            SwingAnimation swingAnimation = new SwingAnimation(
                bitmapCache, animation );

            SwingBitmapAndOffset bmp = swingAnimation.get( frameNum );

            ret.add(
                new Sprite(
                    bmp.bitmap,
                    scaler,
                    rabbit.x,
                    rabbit.y,
                    tileSize,
                    bmp.offsetX,
                    bmp.offsetY
                )
            );
        }

        return ret.toArray( new Sprite[] {} );
    }

    private SwingBitmap bitmapForBlock( Block block )
    {
        if ( block.riseDir == RIGHT )
        {
            return bitmapCache.get( land_rising_right );
        }
        else if ( block.riseDir == LEFT )
        {
            return bitmapCache.get( land_rising_left );
        }
        else
        {
            return bitmapCache.get( land_block );
        }
    }
}

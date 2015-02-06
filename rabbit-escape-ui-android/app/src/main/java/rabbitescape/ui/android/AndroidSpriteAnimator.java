package rabbitescape.ui.android;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rabbitescape.engine.Block;
import rabbitescape.engine.ChangeDescription;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.Thing;
import rabbitescape.engine.World;
import rabbitescape.render.Animation;
import rabbitescape.render.AnimationCache;
import rabbitescape.render.BitmapCache;
import rabbitescape.render.Sprite;
import rabbitescape.render.androidlike.Bitmap;

public class AndroidSpriteAnimator
{
    private final World world;
    private final int imagesTileSize;
    private final BitmapCache<AndroidBitmap> bitmapCache;
    private final AnimationCache animationCache;
    private final AndroidBitmapScaler scaler;

    public AndroidSpriteAnimator(
        World world,
        ChangeDescription changeDescription,
        int imagesTileSize,
        BitmapCache<AndroidBitmap> bitmapCache,
        AnimationCache animationCache
    )
    {
        this.world = world;
        this.imagesTileSize = imagesTileSize;
        this.bitmapCache = bitmapCache;
        this.animationCache = animationCache;
        this.scaler = new AndroidBitmapScaler();
    }

    public Sprite[] getSprites( int frameNum )
    {
        List<Sprite> ret = new ArrayList<Sprite>();

        for ( Block block : world.blocks )
        {
            addBlock( ret, block );
        }

        for ( Rabbit rabbit : world.rabbits )
        {
            addThing( frameNum, rabbit, ret );
        }

        for ( Thing thing : world.things )
        {
            addThing( frameNum, thing, ret );
        }

        for ( Thing thing : world.changes.tokensAboutToAppear() )
        {
            addThing( frameNum, thing, ret );
        }

        return ret.toArray( new Sprite[ret.size()] );
    }

    private void addBlock( List<Sprite> ret, Block block )
    {
        ret.add(
            new Sprite(
                bitmapForBlock( block ),
                scaler,
                block.x,
                block.y,
                imagesTileSize,
                0,
                0
            )
        );
    }

    private void addThing( int frameNum, Thing thing, List<Sprite> ret )
    {
        String frame = thing.state.name().toLowerCase( Locale.ENGLISH );
        Animation animation = animationCache.get( frame );

        if ( animation == null )
        {
            Log.e( "rabbit-escape", "Missing animation for state " + thing.state );
            return;
        }

        // TODO: don't make a new one of these every time?
        AndroidAnimation androidAnimation = new AndroidAnimation( bitmapCache, animation );

        AndroidBitmapAndOffset bmp = androidAnimation.get( frameNum );

        ret.add(
            new Sprite(
                bmp.bitmap,
                scaler,
                thing.x,
                thing.y,
                imagesTileSize,
                bmp.offsetX,
                bmp.offsetY
            )
        );
    }

    private AndroidBitmap bitmapForBlock( Block block )
    {
        return bitmapCache.get( bitmapNameForBlock( block ) );
    }

    private String bitmapNameForBlock( Block block )
    {
        switch( block.type )
        {
            case solid_flat:      return "land_block_1"; // TODO: vary these like in desktop version
            case solid_up_right:  return "land_rising_right_1";
            case solid_up_left:   return "land_rising_left_1";
            case bridge_up_right: return "bridge_rising_right";
            case bridge_up_left:  return "bridge_rising_left";
            default:
                throw new RuntimeException(
                    "Unknown block type " + block.type );
        }
    }

}

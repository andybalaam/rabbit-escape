package rabbitescape.render;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rabbitescape.engine.*;
import rabbitescape.render.androidlike.Bitmap;

public class SpriteAnimator<T extends Bitmap>
{
    private final World world;
    private final BitmapCache<T> bitmapCache;
    private final AnimationCache animationCache;

    private static final String[] land_block = new String[]
    {
        "land_block_1",
        "land_block_2",
        "land_block_3",
        "land_block_4",
    };

    private static final String[] land_rising_right = new String[]
    {
        "land_rising_right_1",
        "land_rising_right_2",
        "land_rising_right_3",
        "land_rising_right_4"
    };

    private static final String[] land_rising_left = new String[]
    {
        "land_rising_left_1",
        "land_rising_left_2",
        "land_rising_left_3",
        "land_rising_left_4"
    };

    private static final String bridge_rising_right = "bridge_rising_right";
    private static final String bridge_rising_left  = "bridge_rising_left";

    public SpriteAnimator(
        World world,
        BitmapCache<T> bitmapCache,
        AnimationCache animationCache
    )
    {
        this.world = world;
        this.bitmapCache = bitmapCache;
        this.animationCache = animationCache;
    }

    public List<Sprite<T>> getSprites( int frameNum )
    {
        List<Sprite<T>> ret = new ArrayList<>();

        for ( Block block : world.blocks )
        {
            addBlock( block, ret );
        }

        for ( Thing thing : world.things )
        {
            addThing( frameNum, thing, null, ret );
        }

        for ( Rabbit rabbit : world.rabbits )
        {
            addThing( frameNum, rabbit, null, ret );
        }

        for ( Thing thing : world.changes.tokensAboutToAppear() )
        {
            addThing( frameNum, thing, null, ret );
        }

        // TODO: probably easier if we just had a rabbit entering animation
        if ( frameNum == 0 )
        {
            for ( Thing thing : world.changes.rabbitsJustEntered() )
            {
                addThing( -1, thing, "rabbit_entering", ret );
            }
        }

        return ret;
    }

    private void addBlock( Block block, List<Sprite<T>> ret )
    {
        ret.add(
            new Sprite<T>(
                bitmapForBlock( block ),
                null,
                block.x,
                block.y,
                0,
                0
            )
        );
    }

    private void addThing(
        int frameNum,
        Thing thing,
        String soundEffectOverride,
        List<Sprite<T>> ret
    )
    {
        Frame frame = frameForThing( frameNum, thing );

        ScaledBitmap<T> bitmap = null;
        int offsetX = 0;
        int offsetY = 0;
        if ( frame != null )
        {
            bitmap = bitmapCache.get( frame.name );
            offsetX = frame.offsetX;
            offsetY = frame.offsetY;
        }

        ret.add(
            new Sprite<T>(
                bitmap,
                soundEffectForFrame( soundEffectOverride, frame ),
                thing.x,
                thing.y,
                offsetX,
                offsetY
            )
        );
    }

    private String soundEffectForFrame(
        String soundEffectOverride, Frame frame )
    {
        if ( soundEffectOverride != null )
        {
            return soundEffectOverride;
        }

        if ( frame != null )
        {
            return frame.soundEffect;
        }

        return null;
    }

    private Frame frameForThing( int frameNum, Thing thing )
    {
        if ( frameNum == -1 )
        {
            return null;
        }

        String frameName = thing.state.name().toLowerCase( Locale.ENGLISH );
        Animation animation = animationCache.get( frameName );

        if ( animation == null )
        {
            System.out.println( "Missing animation for state " + thing.state );
            return null;
        }

        return animation.get( frameNum );
    }

    private ScaledBitmap<T> bitmapForBlock( Block block )
    {
        return bitmapCache.get( bitmapNameForBlock( block ) );
    }

    private String bitmapNameForBlock( Block block )
    {
        switch( block.type )
        {
            case solid_flat:      return land_block[block.variant];
            case solid_up_right:  return land_rising_right[block.variant];
            case solid_up_left:   return land_rising_left[block.variant];
            case bridge_up_right: return bridge_rising_right;
            case bridge_up_left:  return bridge_rising_left;
            default:
                throw new RuntimeException(
                    "Unknown block type " + block.type );
        }
    }
}

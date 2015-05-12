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
            addThing( frameNum, thing, ret );
        }

        for ( Rabbit rabbit : world.rabbits )
        {
            addThing( frameNum, rabbit, ret );
        }

        for ( Thing thing : world.changes.tokensAboutToAppear() )
        {
            addThing( frameNum, thing, ret );
        }

        return ret;
    }

    private void addBlock( Block block, List<Sprite<T>> ret )
    {
        ret.add(
            new Sprite<T>(
                bitmapForBlock( block ),
                block.x,
                block.y,
                0,
                0
            )
        );
    }

    private void addThing( int frameNum, Thing thing, List<Sprite<T>> ret )
    {
        String frameName = thing.state.name().toLowerCase( Locale.ENGLISH );
        Animation animation = animationCache.get( frameName );

        if ( animation == null )
        {
            System.out.println( "Missing animation for state " + thing.state );
            return;
        }

        Frame frame = animation.get( frameNum );

        ret.add(
            new Sprite<T>(
                bitmapCache.get( frame.name ),
                thing.x,
                thing.y,
                frame.offsetX,
                frame.offsetY
            )
        );
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

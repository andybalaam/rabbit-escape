package rabbitescape.ui.swing;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rabbitescape.engine.*;
import rabbitescape.render.Animation;
import rabbitescape.render.AnimationCache;
import rabbitescape.render.BitmapCache;
import rabbitescape.render.FrameNameAndOffset;
import rabbitescape.render.ScaledBitmap;
import rabbitescape.render.Sprite;

public class SpriteAnimator
{
    private final World world;
    private final String[] land_block;
    private final String[] land_rising_right;
    private final String[] land_rising_left;
    private final String bridge_rising_right;
    private final String bridge_rising_left;
    private final BitmapCache<SwingBitmap> bitmapCache;
    private final AnimationCache animationCache;

    public SpriteAnimator(
        World world,
        ChangeDescription changes,
        BitmapCache<SwingBitmap> bitmapCache,
        AnimationCache animationCache
    )
    {
        this.world = world;
        this.bitmapCache = bitmapCache;
        this.animationCache = animationCache;

        this.land_block = new String[]
        {
            "land_block_1",
            "land_block_2",
            "land_block_3",
            "land_block_4",
        };

        this.land_rising_right = new String[]
        {
            "land_rising_right_1",
            "land_rising_right_2",
            "land_rising_right_3",
            "land_rising_right_4"
        };

        this.land_rising_left = new String[]
        {
            "land_rising_left_1",
            "land_rising_left_2",
            "land_rising_left_3",
            "land_rising_left_4"
        };

        this.bridge_rising_right = "bridge_rising_right";
        this.bridge_rising_left = "bridge_rising_left";
    }

    public List<Sprite<SwingBitmap>> getSprites( int frameNum )
    {
        List<Sprite<SwingBitmap>> ret = new ArrayList<>();

        for ( Block block : world.blocks )
        {
            ret.add(
                new Sprite<SwingBitmap>(
                    bitmapForBlock( block ),
                    block.x,
                    block.y,
                    0,
                    0
                )
            );
        }

        for ( Thing thing : world.things )
        {
            drawThing( frameNum, ret, thing );
        }

        for ( Rabbit rabbit : world.rabbits )
        {
            drawThing( frameNum, ret, rabbit );
        }

        for ( Thing thing : world.changes.tokensAboutToAppear() )
        {
            drawThing( frameNum, ret, thing );
        }

        return ret;
    }

    private void drawThing(
        int frameNum, List<Sprite<SwingBitmap>> ret, Thing thing )
    {
        String frameName = thing.state.name().toLowerCase( Locale.ENGLISH );
        Animation animation = animationCache.get( frameName );

        if ( animation == null )
        {
            System.out.println( "Missing animation for state " + thing.state );
            return;
        }

        FrameNameAndOffset frame = animation.get( frameNum );

        ret.add(
            new Sprite<SwingBitmap>(
                bitmapCache.get( frame.name ),
                thing.x,
                thing.y,
                frame.offsetX,
                frame.offsetY
            )
        );
    }

    private ScaledBitmap<SwingBitmap> bitmapForBlock( Block block )
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

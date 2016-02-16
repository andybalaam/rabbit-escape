package rabbitescape.render;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rabbitescape.engine.*;

public class SpriteAnimator
{
    private final World world;
    private final AnimationCache animationCache;

    private static final String[] metal_block = new String[]
        {
            "metal_block_1",
            "metal_block_2",
            "metal_block_3",
            "metal_block_4",
        };

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
        AnimationCache animationCache
    )
    {
        this.world = world;
        this.animationCache = animationCache;
    }

    public List<Sprite> getSprites( int frameNum )
    {
        List<Sprite> ret = new ArrayList<>();

        for ( Block block : world.blockTable )
        {
            addBlock( block, ret );
        }

        for ( Thing thing : world.things )
        {
            if ( !( thing instanceof Fire ) )
            {
                addThing( frameNum, thing, null, ret );
            }
        }

        for ( Rabbit rabbit : world.rabbits )
        {
            addThing( frameNum, rabbit, null, ret );
        }

        for ( Thing thing : world.things )
        {
            if ( thing instanceof Fire )
            {
                addThing( frameNum, thing, null, ret );
            }
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

        VoidMarker.mark( world, ret, world.voidStyle );

        return ret;
    }

    private void addBlock( Block block, List<Sprite> ret )
    {
        ret.add(
            new Sprite(
                bitmapNameForBlock( block ),
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
        List<Sprite> ret
    )
    {
        Frame frame = frameForThing( frameNum, thing );

        String bitmapName = null;
        int offsetX = 0;
        int offsetY = 0;
        if ( frame != null )
        {
            bitmapName = frame.name;
            offsetX = frame.offsetX;
            offsetY = frame.offsetY;
        }

        ret.add(
            new Sprite(
                bitmapName,
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

    private String bitmapNameForBlock( Block block )
    {
        switch( block.type )
        {
            case metal_flat:      return metal_block[block.variant];
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

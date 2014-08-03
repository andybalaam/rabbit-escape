package rabbitescape.ui.swing;

import java.util.ArrayList;
import java.util.List;

import static rabbitescape.engine.ChangeDescription.State.*;
import static rabbitescape.engine.Direction.*;

import rabbitescape.engine.Block;
import rabbitescape.engine.ChangeDescription;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.World;
import rabbitescape.render.BitmapCache;
import rabbitescape.render.Sprite;

public class SpriteAnimator
{
    private final World world;
    private final SwingBitmapScaler scaler;
    private final String[] walk_frames;
    private final String[] bash_frames;
    private final String land_block;
    private final String land_rising_right;
    private final String land_rising_left;
    private final int tileSize;
    private final BitmapCache<SwingBitmap> bitmapCache;

    public SpriteAnimator(
        World world, ChangeDescription changes, int tileSize )
    {
        this.world = world;
        this.scaler = new SwingBitmapScaler();
        this.tileSize = tileSize;
        this.bitmapCache = new BitmapCache<SwingBitmap>(
            new SwingBitmapLoader(), 500 );

        walk_frames = new String[] {
            "/rabbitescape/ui/swing/images32/rabbit-walk-01.png",
            "/rabbitescape/ui/swing/images32/rabbit-walk-02.png",
            "/rabbitescape/ui/swing/images32/rabbit-walk-03.png",
            "/rabbitescape/ui/swing/images32/rabbit-walk-04.png",
            "/rabbitescape/ui/swing/images32/rabbit-walk-05.png",
            "/rabbitescape/ui/swing/images32/rabbit-walk-06.png",
            "/rabbitescape/ui/swing/images32/rabbit-walk-07.png",
            "/rabbitescape/ui/swing/images32/rabbit-walk-08.png",
            "/rabbitescape/ui/swing/images32/rabbit-walk-09.png",
            "/rabbitescape/ui/swing/images32/rabbit-walk-10.png"
        };

        bash_frames = new String[] {
            "/rabbitescape/ui/swing/images32/rabbit-bash-01.png",
            "/rabbitescape/ui/swing/images32/rabbit-bash-02.png",
            "/rabbitescape/ui/swing/images32/rabbit-bash-03.png",
            "/rabbitescape/ui/swing/images32/rabbit-bash-04.png",
            "/rabbitescape/ui/swing/images32/rabbit-bash-05.png",
            "/rabbitescape/ui/swing/images32/rabbit-bash-06.png",
            "/rabbitescape/ui/swing/images32/rabbit-bash-07.png",
            "/rabbitescape/ui/swing/images32/rabbit-bash-08.png",
            "/rabbitescape/ui/swing/images32/rabbit-bash-09.png",
            "/rabbitescape/ui/swing/images32/rabbit-bash-10.png"
        };

        land_block = "/rabbitescape/ui/swing/images32/land-block.png";

        land_rising_right =
            "/rabbitescape/ui/swing/images32/land-rising-right.png";

        land_rising_left =
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
            String frame = rabbit.state == RABBIT_BASHING_RIGHT ?
                  bash_frames[frameNum]
                : walk_frames[frameNum];

            ret.add(
                new Sprite(
                    bitmapCache.get( frame ),
                    scaler,
                    rabbit.x,
                    rabbit.y,
                    tileSize,
                    0,
                    0
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

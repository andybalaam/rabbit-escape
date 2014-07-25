package rabbitescape.ui.swing;

import java.util.ArrayList;
import java.util.List;

import static rabbitescape.engine.ChangeDescription.State.*;

import rabbitescape.engine.ChangeDescription;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.World;
import rabbitescape.render.Sprite;

public class SpriteAnimator
{
    private final World world;
    private final SwingBitmapScaler scaler;
    private final SwingBitmap[] walk_frames;
    private final SwingBitmap[] bash_frames;
    private final int tileSize;

    public SpriteAnimator(
        World world, ChangeDescription changes, int tileSize )
    {
        this.world = world;
        this.scaler = new SwingBitmapScaler();
        this.tileSize = tileSize;

        SwingBitmapLoader bitmapLoader = new SwingBitmapLoader();

        walk_frames = new SwingBitmap[] {
            bitmapLoader.load(
                "/rabbitescape/ui/swing/images32/rabbit-walk-01.png" ),
            bitmapLoader.load(
                "/rabbitescape/ui/swing/images32/rabbit-walk-02.png" ),
                bitmapLoader.load(
                "/rabbitescape/ui/swing/images32/rabbit-walk-03.png" ),
            bitmapLoader.load(
                "/rabbitescape/ui/swing/images32/rabbit-walk-04.png" ),
            bitmapLoader.load(
                "/rabbitescape/ui/swing/images32/rabbit-walk-05.png" ),
            bitmapLoader.load(
                "/rabbitescape/ui/swing/images32/rabbit-walk-06.png" ),
            bitmapLoader.load(
                "/rabbitescape/ui/swing/images32/rabbit-walk-07.png" ),
            bitmapLoader.load(
                "/rabbitescape/ui/swing/images32/rabbit-walk-08.png" ),
            bitmapLoader.load(
                "/rabbitescape/ui/swing/images32/rabbit-walk-09.png" ),
            bitmapLoader.load(
                "/rabbitescape/ui/swing/images32/rabbit-walk-10.png" )
        };

        bash_frames = new SwingBitmap[] {
            bitmapLoader.load(
                "/rabbitescape/ui/swing/images32/rabbit-bash-01.png" ),
            bitmapLoader.load(
                "/rabbitescape/ui/swing/images32/rabbit-bash-02.png" ),
                bitmapLoader.load(
                "/rabbitescape/ui/swing/images32/rabbit-bash-03.png" ),
            bitmapLoader.load(
                "/rabbitescape/ui/swing/images32/rabbit-bash-04.png" ),
            bitmapLoader.load(
                "/rabbitescape/ui/swing/images32/rabbit-bash-05.png" ),
            bitmapLoader.load(
                "/rabbitescape/ui/swing/images32/rabbit-bash-06.png" ),
            bitmapLoader.load(
                "/rabbitescape/ui/swing/images32/rabbit-bash-07.png" ),
            bitmapLoader.load(
                "/rabbitescape/ui/swing/images32/rabbit-bash-08.png" ),
            bitmapLoader.load(
                "/rabbitescape/ui/swing/images32/rabbit-bash-09.png" ),
            bitmapLoader.load(
                "/rabbitescape/ui/swing/images32/rabbit-bash-10.png" )
        };
    }

    public Sprite[] getSprites( int frameNum )
    {
        List<Sprite> ret = new ArrayList<Sprite>();

        for ( Rabbit rabbit : world.rabbits )
        {
            SwingBitmap frame = rabbit.state == RABBIT_BASHING_RIGHT ?
                  bash_frames[frameNum]
                : walk_frames[frameNum];

            ret.add(
                new Sprite(
                    frame,
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
}

package rabbitescape.ui.swing;

import java.util.ArrayList;
import java.util.List;

import rabbitescape.engine.ChangeDescription;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.World;
import rabbitescape.render.Sprite;

public class SpriteAnimator
{
    private final World world;
    private final SwingBitmapScaler scaler;
    private final SwingBitmap x;
    private final int tileSize;

    public SpriteAnimator(
        World world, ChangeDescription changes, int tileSize )
    {
        this.world = world;
        this.scaler = new SwingBitmapScaler();
        this.tileSize = tileSize;

        SwingBitmapLoader bitmapLoader = new SwingBitmapLoader();

        x = bitmapLoader.load( "/rabbitescape/ui/swing/x.png" );
    }

    public Sprite[] getSprites( int frameNum )
    {
        List<Sprite> ret = new ArrayList<Sprite>();

        for ( Rabbit rabbit : world.rabbits )
        {
            ret.add(
                new Sprite( x, scaler, rabbit.x, rabbit.y, tileSize, 0, 0 ) );
        }

        return ret.toArray( new Sprite[] {} );
    }
}

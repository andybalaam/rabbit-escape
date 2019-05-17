package rabbitescape.ui.swing;

import static rabbitescape.engine.util.Util.*;

import java.io.IOException;
import java.util.Arrays;

import rabbitescape.render.BitmapLoader;
import rabbitescape.render.FailedToLoadImage;

public class SwingBitmapLoader implements BitmapLoader<SwingBitmap>
{
    private static int[] SIZES = new int[] { 32, 64, 128 };

    @Override
    public SwingBitmap load( String name, int tileSize )
    {
        reAssert( Arrays.binarySearch( SIZES, tileSize ) >= 0 );

        String resourcePath =
            "/rabbitescape/ui/swing/images" + tileSize + "/" + name + ".png";

        try
        {
            return new SwingBitmap(
                name,
                javax.imageio.ImageIO.read(
                    getClass().getResource( resourcePath )
                )
            );
        }
        catch ( IOException | IllegalArgumentException e )
        {
            throw new FailedToLoadImage( resourcePath, e );
        }
    }

    @Override
    public int sizeFor( int tileSize )
    {
        // Return the smallest size that is >= tileSize

        for ( int size : SIZES )
        {
            if ( tileSize <= size )
            {
                return size;
            }
        }
        return SIZES[ SIZES.length - 1 ];
    }
}

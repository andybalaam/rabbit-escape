package rabbitescape.ui.android;

import android.content.res.Resources;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import rabbitescape.render.BitmapLoader;
import rabbitescape.render.FailedToLoadImage;

import static rabbitescape.engine.util.Util.reAssert;

public class AndroidBitmapLoader implements BitmapLoader<AndroidBitmap>
{
    private static final int[] SIZES = new int[] { 32, 64, 128 };

    private final Resources resources;

    public AndroidBitmapLoader( Resources resources )
    {
        this.resources = resources;
    }

    @Override
    public AndroidBitmap load( String name, int tileSize )
    {
        reAssert( Arrays.binarySearch( SIZES, tileSize ) >= 0 );

        String resourcePath = "images" + tileSize + "/" + name + ".png";

        try
        {
            InputStream assetStream = resources.getAssets().open( resourcePath );
            try
            {
                return new AndroidBitmap( BitmapFactory.decodeStream( assetStream ) );
            }
            finally
            {
                assetStream.close();
            }
        }
        catch ( IOException e )
        {
            throw new FailedToLoadImage( name, e );
        }
    }

    @Override
    public int sizeFor( int tileSize )
    {
        // Return the smallest size that is >= tileSize

        // Could share this code with SwingBitmapLoader, but I'm
        // not convinced we will always want the same implementation
        // here and there.  E.g. maybe on one platform scaling up is
        // better than down.

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

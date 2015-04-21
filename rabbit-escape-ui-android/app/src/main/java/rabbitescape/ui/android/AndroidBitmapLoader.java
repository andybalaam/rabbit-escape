package rabbitescape.ui.android;

import android.content.res.Resources;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

import rabbitescape.render.BitmapLoader;
import rabbitescape.render.FailedToLoadImage;

public class AndroidBitmapLoader implements BitmapLoader<AndroidBitmap>
{
    private final Resources resources;

    public AndroidBitmapLoader( Resources resources )
    {
        this.resources = resources;
    }

    @Override
    public AndroidBitmap load( String name, int tileSize )
    {
        if ( tileSize != 64 )
        {
            throw new RuntimeException( "A Tile size is supposed to be hard-coded to 64!" );
        }

        try
        {
            InputStream assetStream = resources.getAssets().open( "images64/" + name + ".png" );
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
        if ( tileSize != 64 )
        {
            throw new RuntimeException( "B Tile size is supposed to be hard-coded to 64!" );
        }
        return tileSize;
    }
}

package rabbitescape.ui.android;

import android.content.res.Resources;
import android.graphics.BitmapFactory;

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
    public AndroidBitmap load( String name )
    {
        // If this is slow, consider using reflection on R.drawable.class
        // (see http://daniel-codes.blogspot.co.uk/2009/12/dynamically-retrieving-resources-in.html)
        // or allow passing an int ID instead of a name.

        int id = resources.getIdentifier( name, "drawable", "rabbitescape.ui.android" );

        if ( id == 0 )
        {
            throw new FailedToLoadImage( name );
        }

        return new AndroidBitmap( BitmapFactory.decodeResource( resources, id ) );
    }
}

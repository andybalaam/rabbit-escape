package rabbitescape.ui.android;

import android.content.res.Resources;

import rabbitescape.render.BitmapCache;

/**
 * I feel dirty, but I don't know how to ensure we
 * don't leak Bitmaps if we make a new cache for every
 * GameActivity.  If I clean up in onPause I get errors
 * because some of the Bitmaps I am recycling are still
 * being used in another thread.
 *
 * So, a singleton that is also stateful because we
 * update the loader because we get a new Resources
 * instance, just in case old ones become invalid.
 *
 * Please, tell me how to do this better.
 */
public class SingletonBitmapCache
{
    private static BitmapCache<AndroidBitmap> theInstance;

    public static synchronized BitmapCache<AndroidBitmap> instance(
        Resources resources )
    {
        if ( theInstance == null )
        {
            theInstance = new BitmapCache<AndroidBitmap>(
                new AndroidBitmapLoader( resources ),
                new AndroidBitmapScaler(),
                cacheSize()
            );
        }
        else
        {
            theInstance.setLoader( new AndroidBitmapLoader( resources ) );
        }

        return theInstance;
    }

    private static long cacheSize()
    {
        // About a quarter of our memory will go on bitmaps.
        // When I set it to a third on my Samsung S3,
        // I couldn't make it crash so I reduced the value
        // for a bit of safety.
        return Runtime.getRuntime().maxMemory() / 4;
    }


    /**
     * Don't construct one of these.
     */
    private SingletonBitmapCache()
    {
    }
}

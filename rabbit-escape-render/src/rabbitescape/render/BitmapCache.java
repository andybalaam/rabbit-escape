package rabbitescape.render;

import rabbitescape.render.androidlike.Bitmap;

public class BitmapCache<T extends Bitmap>
{
    private BitmapLoader<T> loader;
    private final BitmapScaler<T> scaler;
    private final ReLruCache<T> cache;

    public BitmapCache(
        BitmapLoader<T> loader,
        BitmapScaler<T> scaler,
        long cacheSize
    )
    {
        this.loader = loader;
        this.scaler = scaler;
        this.cache = new ReLruCache<T>( cacheSize );
    }

    public T get( String fileName, int tileSize )
    {
        T ret = cache.get( fileName + tileSize );

        if ( ret == null )
        {
            ret = loadBitmap( fileName, tileSize );
            cache.put( fileName + tileSize, ret );
        }

        return ret;
    }

    public void recycle()
    {
        cache.recycle();
    }

    public void setLoader( BitmapLoader<T> loader )
    {
        this.loader = loader;
    }

    private T loadBitmap( String fileName, int tileSize )
    {
        // TODO: move this logic into loader - some loaders
        //       (including android?) can scale as they load,
        //       meanng we have less garbage to collect.

        int unscaledSize = loader.sizeFor( tileSize );
        T unscaled = loader.load( fileName, unscaledSize );

        if ( unscaledSize == tileSize )
        {
            return unscaled;
        }
        else
        {
            double scale = tileSize / ( double )unscaledSize;
            T ret = scaler.scale( unscaled, scale );
            unscaled.recycle();
            return ret;
        }
    }
}

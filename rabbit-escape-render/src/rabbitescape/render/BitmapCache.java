package rabbitescape.render;

import rabbitescape.render.androidlike.Bitmap;

public class BitmapCache<T extends Bitmap>
{
    private final BitmapLoader<T> loader;
    private final BitmapScaler<T> scaler;
    private final ReLruCache<ScaledBitmap<T>> cache;

    public BitmapCache(
        BitmapLoader<T> loader, BitmapScaler<T> scaler, int maxSize )
    {
        this.loader = loader;
        this.scaler = scaler;
        this.cache = new ReLruCache<ScaledBitmap<T>>( maxSize );
    }

    public ScaledBitmap<T> get( String fileName )
    {
        ScaledBitmap<T> ret = cache.get( fileName );

        if ( ret == null )
        {
            ret = new ScaledBitmap<T>( scaler, loader, fileName );
            cache.put( fileName, ret );
        }

        return ret;
    }
}

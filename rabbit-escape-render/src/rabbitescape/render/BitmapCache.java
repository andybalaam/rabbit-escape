package rabbitescape.render;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import rabbitescape.render.androidlike.Bitmap;

public class BitmapCache<T extends Bitmap>
{
    private final int imagesTileSize = 32;

    private final BitmapLoader<T> loader;
    private final BitmapScaler<T> scaler;
    private final int size;
    private final Map<String, ScaledBitmap<T>> cache;
    private final Queue<String> usedKeys;

    public BitmapCache(
        BitmapLoader<T> loader, BitmapScaler<T> scaler, int size )
    {
        this.loader = loader;
        this.scaler = scaler;
        this.size = size;
        this.cache = new HashMap<>();
        usedKeys = new LinkedList<>();
    }

    public ScaledBitmap<T> get( String fileName )
    {
        ScaledBitmap<T> ret = cache.get( fileName );

        if ( ret == null )
        {
            if ( usedKeys.size() == size )
            {
                String purgedKey = usedKeys.remove();
                ScaledBitmap<T> removed = cache.remove( purgedKey );
                if ( removed != null )
                {
                    removed.recycle();
                }
            }

            ret = new ScaledBitmap<T>(
                scaler, loader.load( fileName ), imagesTileSize );

            cache.put( fileName, ret );
        }

        usedKeys.remove( fileName );
        usedKeys.add( fileName );
        return ret;
    }
}

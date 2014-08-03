package rabbitescape.render;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import rabbitescape.render.androidlike.Bitmap;

public class BitmapCache<T extends Bitmap>
{
    private final BitmapLoader<T> loader;
    private final int size;
    private final Map<String, T> cache;
    private final Queue<String> usedKeys;

    public BitmapCache( BitmapLoader<T> loader, int size )
    {
        this.loader = loader;
        this.size = size;
        this.cache = new HashMap<>();
        usedKeys = new LinkedList<>();
    }

    public T get( String fileName )
    {
        T ret = cache.get( fileName );

        if ( ret == null )
        {
            if ( usedKeys.size() == size )
            {
                String purgedKey = usedKeys.remove();
                cache.remove( purgedKey );
            }

            ret = loader.load( fileName );
            cache.put( fileName, ret );
        }

        usedKeys.remove( fileName );
        usedKeys.add( fileName );
        return ret;
    }
}

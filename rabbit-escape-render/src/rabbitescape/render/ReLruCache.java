package rabbitescape.render;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class ReLruCache<T extends SizedRecyclable>
{
    private final int maxSize;
    private final Map<String, T> map;
    private final Queue<String> usedKeys;

    public ReLruCache( int maxSize )
    {
        this.maxSize = maxSize;
        this.map = new HashMap<>();
        this.usedKeys = new LinkedList<>();
    }

    public void put( String key, T value )
    {
        if ( value == null )
        {
            throw new IllegalArgumentException( "Null values are not allowed in LruCache" );
        }

        makeSpaceFor( value );

        usedKeys.add( key );
        map.put( key, value );
    }

    public T get( String key )
    {
        T ret = map.get( key );

        if ( ret != null )
        {
            usedKeys.remove( key ); // Move key to front
            usedKeys.add( key );    // because it was used
        }

        return ret;
    }

    public int currentSize()
    {
        return sumOfValueSizes( map );
    }

    // ---

    private void makeSpaceFor( T value )
    {
        int valueSize = value.size();
        while ( !isEmpty() && currentSize() + valueSize > maxSize )
        {
            recycleOldest();
        }
    }

    private boolean isEmpty()
    {
        return map.isEmpty();
    }

    private void recycleOldest()
    {
        String oldestKey = usedKeys.remove();
        T value = map.remove( oldestKey );
        value.recycle();
    }

    private int sumOfValueSizes( Map<String, T> map2 )
    {
        int ret = 0;

        for ( T value : map2.values() )
        {
            ret += value.size();
        }

        return ret;
    }
}


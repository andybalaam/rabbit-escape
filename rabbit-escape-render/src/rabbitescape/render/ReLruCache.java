package rabbitescape.render;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class ReLruCache<T extends SizedRecyclable>
{
    private final long maxSize;
    private long curSize;
    private final Map<String, T> map;
    private final Queue<String> usedKeys;

    public ReLruCache( long maxSize )
    {
        this.maxSize = maxSize;
        this.curSize = 0;
        this.map = new HashMap<>();
        this.usedKeys = new LinkedList<>();
    }

    public void put( String key, T value )
    {
        if ( value == null )
        {
            throw new IllegalArgumentException(
                "Null values are not allowed in LruCache" );
        }

        makeSpaceFor( value );

        T existing = map.get( key );
        if ( existing != null )
        {
            usedKeys.remove( key );
            curSize -= existing.size();
        }

        usedKeys.add( key );
        map.put( key, value );
        curSize += value.size();
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

    public long currentSize()
    {
        // TODO: when values coming in here are already sized:
        // return curSize;

        long ret = 0;
        for ( T value : map.values() )
        {
            ret += value.size();
        }
        return ret;
    }

    // ---

    private void makeSpaceFor( T value )
    {
        long valueSize = value.size();
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
        curSize -= value.size();
        value.recycle();
    }
}


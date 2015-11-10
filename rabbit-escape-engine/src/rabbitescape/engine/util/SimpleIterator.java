package rabbitescape.engine.util;

import java.util.Iterator;

/**
 * Makes an iterator from a class that just has a next() method that
 * returns null when it's finished.
 */
public class SimpleIterator
{
    public static <T> Iterator<T> simpleIterator( final Nextable<T> n )
    {
        return new Iterator<T>()
        {
            T next = n.next();

            @Override
            public boolean hasNext()
            {
                return next != null;
            }

            @Override
            public T next()
            {
                T ret = next;
                next = n.next();
                return ret;
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException();
            }
        };
    }
}

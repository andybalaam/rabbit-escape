package rabbitescape.engine.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class Util
{
    public static interface Function<T, R>
    {
        public R apply( T t );
    }

    /**
     * Always assert something, without needing -ea set on the JVM.
     */
    public static void reAssert( boolean assertion )
    {
        if ( !assertion )
        {
            throw new AssertionError();
        }
    }

    public static <T> T getNth( Iterable<T> input, int n )
    {
        int i = 0;
        for( T item : input )
        {
            if ( i == n )
            {
                return item;
            }
            ++i;
        }

        throw new ArrayIndexOutOfBoundsException( n );
    }

    public static <T, R> Iterable<R> map(
        Function<T, R> function, Iterable<T> iterable )
    {
        List<R> ret = new ArrayList<>();

        for ( T t : iterable )
        {
            ret.add( function.apply( t ) );
        }

        return ret;
    }

    public static <T> List<T> list( Iterable<T> input )
    {
        List<T> ret = new ArrayList<>();

        for ( T item : input )
        {
            ret.add( item );
        }

        return ret;
    }

    public static <T> List<T> list( T[] input )
    {
        return Arrays.asList( input );
    }

    public static String[] stringArray( List<String> list )
    {
        return list.toArray( new String[ list.size() ] );
    }

    public static Character[] characterArray( List<Character> list )
    {
        return list.toArray( new Character[ list.size() ] );
    }

    public static Integer[] integerArray( List<Integer> list )
    {
        return list.toArray( new Integer[ list.size() ] );
    }

    public static Map<String, Object> newMap( String... keysAndValues )
    {
        reAssert( keysAndValues.length % 2 == 0 );

        Map<String, Object> ret = new HashMap<>();

        for ( int i = 0; i < keysAndValues.length; i += 2 )
        {
            ret.put( keysAndValues[i], keysAndValues[ i + 1 ] );
        }

        return ret;
    }

    public static Iterable<Character> asChars( final String str )
    {
        class CharIterator implements Iterator<Character>
        {
            private final String str;
            private final int strLength;
            private int i = 0;

            public CharIterator( String str )
            {
                this.str = str;
                this.strLength = str.length();
                this.i = -1;
            }

            @Override
            public boolean hasNext()
            {
                return i < strLength - 1;
            }

            @Override
            public Character next()
            {
                ++i;
                if ( i >= strLength )
                {
                    throw new NoSuchElementException();
                }
                return str.charAt( i );
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException();
            }
        }

        return new Iterable<Character>()
        {
            @Override
            public Iterator<Character> iterator()
            {
                return new CharIterator( str );
            }
        };
    }

    public static String stringFromChars( Iterable<Character> chars )
    {
        StringBuilder ret = new StringBuilder();

        for ( Character c : chars )
        {
            ret.append( c );
        }

        return ret.toString();
    }

    public static String join( String glue, String[] items )
    {
        return join( glue, Arrays.asList( items ) );
    }

    public static String join( String glue, Iterable<String> items )
    {
        StringBuilder ret = new StringBuilder();

        boolean first = true;
        for ( String item : items )
        {
            if ( first )
            {
                first = false;
            }
            else
            {
                ret.append( glue );
            }

            ret.append( item );
        }

        return ret.toString();
    }

    public static Iterable<Integer> range( final int max )
    {
        return new Iterable<Integer>()
        {
            @Override
            public Iterator<Integer> iterator()
            {
                class MyIt implements Iterator<Integer>
                {
                    private final int max;
                    private int i;

                    public MyIt( int max )
                    {
                        this.max = max;
                    }

                    @Override
                    public boolean hasNext()
                    {
                        return i < max;
                    }

                    @Override
                    public Integer next()
                    {
                        return i++;
                    }

                    @Override
                    public void remove()
                    {
                        throw new UnsupportedOperationException();
                    }
                }

                return new MyIt( max );
            }
        };
    }

    public static <T> Iterable<T> chain(
        final Iterable<? extends T> it1, final Iterable<? extends T> it2 )
    {
        return new Iterable<T>()
        {
            @Override
            public Iterator<T> iterator()
            {
                class MyIt implements Iterator<T>
                {
                    private final Iterator<? extends T> i1;
                    private final Iterator<? extends T> i2;

                    public MyIt(
                        Iterator<? extends T> i1, Iterator<? extends T> i2 )
                    {
                        this.i1 = i1;
                        this.i2 = i2;
                    }

                    @Override
                    public boolean hasNext()
                    {
                        return i1.hasNext() || i2.hasNext();
                    }

                    @Override
                    public T next()
                    {
                        return i1.hasNext() ? i1.next() : i2.next();
                    }

                    @Override
                    public void remove()
                    {
                        throw new UnsupportedOperationException();
                    }
                }

                return new MyIt( it1.iterator(), it2.iterator() );
            }
        };
    }
}

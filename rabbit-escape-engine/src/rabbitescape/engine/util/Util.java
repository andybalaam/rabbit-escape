package rabbitescape.engine.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Util
{
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

    public static <T> List<T> list( Iterable<T> input )
    {
        List<T> ret = new ArrayList<T>();

        for ( T item : input )
        {
            ret.add( item );
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
}

package rabbitescape.engine.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import rabbitescape.engine.err.RabbitEscapeException;

public class Util
{
    public static interface Function<T, R>
    {
        public R apply( T t );
    }

    public static class ReadingResourceFailed extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;

        public final String name;

        public ReadingResourceFailed( Throwable cause, String name )
        {
            super( cause );
            this.name = name;
        }

        public ReadingResourceFailed( String name )
        {
            this.name = name;
        }
    }

    public static class MissingResource extends ReadingResourceFailed
    {
        private static final long serialVersionUID = 1L;

        public MissingResource( String name )
        {
            super( name );
        }
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

    public static <T, R> R[] map(
        Function<T, R> function, T[] input, R[] retType )
    {
        List<R> ret = new ArrayList<>();

        for ( T t : input )
        {
            ret.add( function.apply( t ) );
        }

        return ret.toArray( retType );
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

    public static String[] stringArray( Iterable<String> items )
    {
        return stringArray( list( items ) );
    }

    public static Character[] characterArray( List<Character> list )
    {
        return list.toArray( new Character[ list.size() ] );
    }

    public static Character[] characterArray( Iterable<Character> items )
    {
        return characterArray( list( items ) );
    }

    public static Integer[] integerArray( List<Integer> list )
    {
        return list.toArray( new Integer[ list.size() ] );
    }

    public static Integer[] integerArray( Iterable<Integer> items )
    {
        return integerArray( list( items ) );
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

    /**
     * Split input by finding all occurrences of delimiter.  Works like a
     * sane version of String.split, but with no regular expressions.
     *
     * @param delimiter is not a regular expression, just a string.
     *
     * @return An array of strings that is always n + 1 long, where n is the
     *         number of times the string delimiter appears in input.
     */
    public static String[] split( String input, String delimiter )
    {
        List<String> ret = new ArrayList<String>();

        int lastI = 0;
        int i = input.indexOf( delimiter );
        while ( i != -1 )
        {
            ret.add( input.substring( lastI, i ) );
            lastI = i + delimiter.length();
            i = input.indexOf( delimiter, lastI );
        }

        ret.add( input.substring( lastI ) );

        return ret.toArray( new String[ ret.size() ] );
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

    private static abstract class UntilNullIterator<T> implements Iterator<T>
    {
        private T nextItem;
        private boolean start;

        public UntilNullIterator()
        {
            this.nextItem = null;
            this.start = true;
        }

        @Override
        public boolean hasNext()
        {
            if ( start ) advance();

            return nextItem != null;
        }

        @Override
        public T next()
        {
            if ( start ) advance();

            T item = nextItem;
            advance();
            return item;
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }

        private void advance()
        {
            start = false;
            nextItem = nextOrNull();
        }

        protected abstract T nextOrNull();
    }

    public static <T> Iterable<T> filter(
        final Function<T, Boolean> predicate,
        final Iterable<T> input
    )
    {
        class It extends UntilNullIterator<T>
        {
            private final Function<T, Boolean> predicate;
            private final Iterator<T> it;

            public It( Function<T, Boolean> predicate, Iterator<T> it )
            {
                this.predicate = predicate;
                this.it = it;
            }

            @Override
            protected T nextOrNull()
            {
                T item = null;
                while ( it.hasNext() )
                {
                    T tmp = it.next();
                    if ( predicate.apply( tmp ) )
                    {
                        item = tmp;
                        break;
                    }
                }
                return item;
            }
        }

        return new Iterable<T>()
        {
            @Override
            public Iterator<T> iterator()
            {
                return new It( predicate, input.iterator() );
            }
        };
    }

    public static Function<String, String> stripLast( final int i )
    {
        return new Function<String, String>()
        {
            @Override
            public String apply( String input )
            {
                return input.substring( 0, Math.max( 0, input.length() - i ) );
            }
        };
    }

    public static Function<String, Boolean> endsWith( final String suffix )
    {
        return new Function<String, Boolean>()
        {
            @Override
            public Boolean apply( String input )
            {
                return input.endsWith( suffix );
            }
        };
    }

    public static <T> Iterable<T> sorted( Iterable<T> input )
    {
        TreeSet<T> ret = new TreeSet<T>();

        for ( T t : input )
        {
            ret.add( t );
        }

        return ret;
    }

    private static class ReaderIterator extends UntilNullIterator<String>
    {
        private final String name;
        private final BufferedReader reader;

        public ReaderIterator( String name, BufferedReader reader )
        {
            this.name = name;
            this.reader = reader;
        }

        @Override
        protected String nextOrNull()
        {
            try
            {
                return reader.readLine();
            }
            catch ( IOException e )
            {
                throw new ReadingResourceFailed( e, name );
            }
        }
    }

    public static Iterable<String> streamLines( InputStream input )
    {
        return streamLines( null, input );
    }

    private static Iterable<String> streamLines(
        final String name, final InputStream input )
    {
        return new Iterable<String>()
        {
            @Override
            public Iterator<String> iterator()
            {
                return new ReaderIterator(
                    name, new BufferedReader( new InputStreamReader( input ) ) );
            }
        };
    }

    public static Iterable<String> resourceLines( String name )
    {
        InputStream res = Util.class.getResourceAsStream( name );
        if ( res == null )
        {
            throw new MissingResource( name );
        }
        return streamLines( name, res );
    }

    public static <T> T[] concat( T[] left, T[] right )
    {
        return list(
            chain(
                Arrays.asList( left ), Arrays.asList( right )
            )
        ).toArray( left );
    }

    public static <T> boolean equalsOrBothNull( T left, T right )
    {
        if ( left == null )
        {
            return ( right == null );
        }
        else
        {
            return left.equals( right );
        }
    }

    /**
     * Use in preference to String.isEmpty since that is not available
     * in Android 2.2.
     */
    public static boolean isEmpty( String str )
    {
        return ( str.length() == 0 );
    }
}

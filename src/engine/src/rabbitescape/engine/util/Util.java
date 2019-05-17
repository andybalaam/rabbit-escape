package rabbitescape.engine.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * As reAssert( boolean assertion ), but with an explanation.
     */
    public static void reAssert( boolean assertion, String message )
    {
        if ( !assertion )
        {
            throw new AssertionError( message );
        }
    }

    public static <T> T getNth( Iterable<T> input, int n )
    {
        int i = 0;
        for ( T item : input )
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
        Function<T, R> function, 
        Iterable<T> iterable 
    )
    {
        List<R> ret = new ArrayList<>();

        for ( T t : iterable )
        {
            ret.add( function.apply( t ) );
        }

        return ret;
    }

    public static <T, R> Iterable<R> map(
        Function<T, R> function, 
        T[] input 
    )
    {
        List<R> ret = new ArrayList<>();

        for ( T t : input )
        {
            ret.add( function.apply( t ) );
        }

        return ret;
    }

    public static <T, R> R[] map(
        Function<T, R> function, 
        T[] input, 
        R[] retType 
    )
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

    public static <T> List<T> list( Iterator<T> input )
    {
        List<T> ret = new ArrayList<>();

        while ( input.hasNext() )
        {
            ret.add( input.next() );
        }

        return ret;
    }

    public static <T> List<T> filterIn( 
        Iterable<T> i,
        Class<? extends T> filter 
    )
    {
        ArrayList<T> filtered = new ArrayList<T>();
        for ( T o: i)
        {
            if ( filter.isInstance( o ) )
            {
                filtered.add( o );
            }
        }
        return filtered;
    }

    public static <T> List<T> filterOut( 
       Iterable<T> i,
       Class<? extends T> filter 
    )
    {
        ArrayList<T> filtered = new ArrayList<T>();
        for ( T o: i)
        {
            if ( !filter.isInstance( o ) )
            {
                filtered.add( o );
            }
        }
        return filtered;
    }

    public static String[] stringArray( List<String> list )
    {
        return list.toArray( new String[list.size()] );
    }

    public static String[] stringArray( Iterable<String> items )
    {
        return stringArray( list( items ) );
    }

    public static Character[] characterArray( List<Character> list )
    {
        return list.toArray( new Character[list.size()] );
    }

    public static Character[] characterArray( Iterable<Character> items )
    {
        return characterArray( list( items ) );
    }

    public static Integer[] integerArray( List<Integer> list )
    {
        return list.toArray( new Integer[list.size()] );
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
            ret.put( keysAndValues[i], keysAndValues[i + 1] );
        }

        return ret;
    }

    @SuppressWarnings( "unchecked" )
    public static <T> Set<T> newSet( T... items )
    {
        return new HashSet<>( Arrays.asList( items ) );
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

    /**
     * @brief         Split a long string into lines
     * @param s       The string to split.
     * @param maxChar The maximum requested line length.
     * @return        Each line is an element in the array.
     * Lines will be split at spaces. Words will never be split.
     */
    public static String[] wrap(String s, int maxChar)
    {
        // Match a bunch of anything apart from a space
        Pattern p = Pattern.compile( "([^ ]+)" );
        Matcher m = p.matcher( s );
        ArrayList<String> al = new ArrayList<String>();
        if ( !m.find() ) // No spaces to split on
        {
            return new String[] { s };
        }
        String line = m.group(1); // No space before first word
        while ( m.find() )
        {
            String word = m.group(1);
            int lineLength = line.length() + word.length() + 1;// + 1 for " "
            if ( lineLength <= maxChar )
            {
                line = line + " " + word; // Replace space between words
            }
            else // Previous line is full
            {
                al.add( line ); // Store it
                line = word;    // and start the next one
            }
        }
        al.add( line ); // Don't forget the last line
        return al.toArray( new String[al.size()] );
    }

    /**
     * As wrap(), but returns a single string with newlines.
     */
    public static String wrapToNewline( String s, int maxChar)
    {
        return Util.join( "\n", wrap( s, maxChar ) );
    }

    public static String join( String glue, Object[] items )
    {
        return join( glue, Arrays.asList(items ));
    }

    public static String join( String glue, String[] items )
    {
        return join( glue, Arrays.asList( items ) );
    }

    public static <T> String join( String glue, Iterable<T> items )
    {
        StringBuilder ret = new StringBuilder();

        boolean first = true;
        for ( T item : items )
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
        return split( input, delimiter, -1 );
    }

    /**
     * Split input by finding all occurrences of delimiter. Works like a sane
     * version of String.split, but with no regular expressions.
     *
     * @param delimiter is not a regular expression, just a string.
     *
     * @param maxSplits the maximum number of times the string should be split,
     *                  or -1 for as many as possible.
     *
     * @return An array of strings that is always 1 + min(n, maxSplits)
     *         long, where n is the number of times the string delimiter
     *         appears in input.
     */
    public static String[] split(
        String input, String delimiter, int maxSplits )
    {
        List<String> ret = new ArrayList<String>();

        int lastI = 0;
        int i = input.indexOf( delimiter );
        while ( i != -1 && ( maxSplits == -1 || ret.size() < maxSplits ) )
        {
            ret.add( input.substring( lastI, i ) );
            lastI = i + delimiter.length();
            i = input.indexOf( delimiter, lastI );
        }

        ret.add( input.substring( lastI ) );

        return ret.toArray( new String[ret.size()] );
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

    /**
     * This can be used to chain objects of different types (unlike concat).
     * The returned Iterable will be of the first common superclass.
     */
    @SafeVarargs
    public static <T> Iterable<T> chain(
        final Iterable<? extends T>... itArray )
    {
        return new Iterable<T>()
        {
            @Override
            public Iterator<T> iterator()
            {
                class MyIt implements Iterator<T>
                {
                    /** Iterator of Iterators */
                    Iterator<Iterator<? extends T>> iI;

                    /** The current sub-Iterator */
                    Iterator<? extends T> i;

                    public MyIt( Iterator<Iterator<? extends T>> iI )
                    {
                        this.iI = iI;
                        i = iI.next();
                    }

                    @Override
                    public boolean hasNext()
                    {
                        if ( i.hasNext() )
                        {
                            return true;
                        }
                        else
                        {
                            if ( iI.hasNext() )
                            {
                                i = iI.next();
                                return this.hasNext();
                            }
                            else
                            {
                                return false;
                            }
                        }
                    }

                    @Override
                    public T next()
                    {
                        if ( i.hasNext() )
                        {
                            return i.next();
                        }
                        else
                        {
                            i = iI.next();
                            return this.next();
                        }
                    }

                    @Override
                    public void remove()
                    {
                        throw new UnsupportedOperationException();
                    }


                }

                List<Iterator<? extends T>> newIL =
                    new ArrayList<Iterator<? extends T>>();
                for ( Iterable<? extends T> it: itArray )
                {
                    newIL.add( it.iterator() );
                }

                return new MyIt(newIL.iterator());
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
                return new ReaderIterator( name,
                    new BufferedReader( new InputStreamReader( input ) ) );
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

    public static <T> T[] concat( T[] a, T[] b, T[] c, T[] d )
    {
        // Someone tell us how to make this better.

        return list(
            chain(
                  list( a )
                , list( b )
                , list( c )
                , list( d )
            )
        ).toArray( a );
    }

    public static <T> T[] concat( T[] a, T[] b, T[] c )
    {
        return list(
            chain(
                  list( a )
                , list( b )
                , list( c )
            )
        ).toArray( a );
    }

    /**
     * Use chain instead for arguments of different classes.
     */
    public static <T> T[] concat( T[] a, T[] b )
    {
        return list(
            chain(
                  list( a )
                , list( b )
            )
        ).toArray( a );
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

    public static <T> List<String> toStringList( T[] values )
    {
        ArrayList<String> ret = new ArrayList<String>();
        for( T t : values )
        {
            ret.add( t.toString() );
        }
        return ret;
    }

    public static class IdxObj<T>
    {
        public static <T> IdxObj<T> make( int index, T object )
        {
            return new IdxObj<T>( index, object );
        }

        public final int index;
        public final T object;

        public IdxObj( int index, T object )
        {
            if ( object == null )
            {
                throw new NullPointerException();
            }

            this.index = index;
            this.object = object;
        }


        @Override
        public int hashCode()
        {
            return ( 31 * index ) + object.hashCode();
        }

        @Override
        public boolean equals( Object otherObj )
        {
            if ( ! ( otherObj instanceof IdxObj ) )
            {
                return false;
            }

            @SuppressWarnings( "unchecked" )
            IdxObj<T> other = (IdxObj<T>)otherObj;

            return (
                   index == other.index
                && object.equals( other.object )
            );
        }

        @Override
        public String toString()
        {
            return "IdxObj( " + index + ", " + object + " )";
        }
    }

    public static <T> Iterable<IdxObj<T>> enumerate1( final T[] array )
    {
        return enumerate1( Arrays.asList( array ) );
    }

    public static <T> Iterable<IdxObj<T>> enumerate( final T[] array )
    {
        return enumerate( Arrays.asList( array ) );
    }

    public static <T> Iterable<IdxObj<T>> enumerate1( final Iterable<T> i )
    {
        return enumerateN( i, 1 );
    }

    public static <T> Iterable<IdxObj<T>> enumerate( final Iterable<T> i )
    {
        return enumerateN( i, 0 );
    }

    private static <T> Iterable<IdxObj<T>> enumerateN(
        final Iterable<T> i, final int startAtIndex )
    {
        return new Iterable<IdxObj<T>>()
        {
            @Override
            public Iterator<IdxObj<T>> iterator()
            {
                return new Iterator<IdxObj<T>>()
                {
                    private final Iterator<T> it = i.iterator();
                    private int index = startAtIndex - 1;

                    @Override
                    public boolean hasNext()
                    {
                        return it.hasNext();
                    }

                    @Override
                    public IdxObj<T> next()
                    {
                        // If next throws, we don't update index either.
                        T n = it.next();
                        ++index;

                        return IdxObj.make( index, n );
                    }

                    @Override
                    public void remove()
                    {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    /**
     * @brief Replace all matches, but preserves the first capturing group.
     *        Compare to String.replaceAll, which replaces the whole match.
     * @param patternFlags The flags from java.util.regex.Pattern.
     */
    public static String regexRemovePreserveGroup( String s, String regex,
                                                   int patternFlags )
    {
        Pattern p = Pattern.compile( regex, patternFlags );
        Matcher dsMatcher = p.matcher( s );
        StringBuffer sb = new StringBuffer();
        while ( dsMatcher.find() )
        {
            dsMatcher.appendReplacement( sb, "$1" );
        }
        dsMatcher.appendTail( sb );

        return sb.toString();
    }

    public static String regexRemovePreserveGroup( String s, String regex )
    {
        return regexRemovePreserveGroup( s, regex, 0 );
    }

    public static String regexReplace( String s, String regex,
                                       String replacement )
    {
        Pattern p = Pattern.compile( regex );
        Matcher dsMatcher = p.matcher( s );
        StringBuffer sb = new StringBuffer();
        while ( dsMatcher.find() )
        {
            dsMatcher.appendReplacement( sb, replacement );
        }
        dsMatcher.appendTail( sb );

        return sb.toString();
    }

    public static List<String> stringPropertyNames( Properties props )
    {
        List<String> ret = new ArrayList<String>();

        Enumeration<?> names = props.propertyNames();
        while( names.hasMoreElements() )
        {
            // Safe cast since propertyNames() always returns Strings.
            ret.add( (String)names.nextElement() );
        }
        return ret;
    }
}

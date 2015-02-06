package rabbitescape.engine;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class Tools
{
    public static Matcher<String[]> equalTo( final String... lines )
    {
        return new BaseMatcher<String[]>()
        {
            @Override
            public boolean matches( Object otherLines )
            {
                //noinspection SimplifiableIfStatement
                if ( !( otherLines instanceof String[] ) )
                {
                    return false;
                }

                return Arrays.deepEquals( lines, (String[])otherLines );
            }

            @Override
            public void describeTo( Description _desc )
            {
                _desc.appendText( Arrays.toString( lines ) );
            }
        };
    }

    public static <T> Matcher<List<T>> equalToList( final T[] list )
    {
        return new BaseMatcher<List<T>>()
        {
            @Override
            public boolean matches( Object otherList )
            {
                //noinspection SimplifiableIfStatement
                if ( !( otherList instanceof List<?> ) )
                {
                    return false;
                }

                List<?> other = (List<?>)( otherList );
                return other.equals( Arrays.asList( list ) );
            }

            @Override
            public void describeTo( Description _desc )
            {
                _desc.appendText( Arrays.toString( list ) );
            }
        };
    }

    public static Matcher<Integer> greaterThan( final int i )
    {
        return new BaseMatcher<Integer>()
        {
            @Override
            public boolean matches( Object other )
            {
                if ( other instanceof Integer )
                {
                    Integer o = (Integer)other;
                    return ( o > i );
                }
                else
                {
                    return false;
                }
            }

            @Override
            public void describeTo( Description desc )
            {
                desc.appendText( "greater than " + i );
            }
        };
    }

    public static Matcher<Integer> lessThan( final int i )
    {
        return new BaseMatcher<Integer>()
        {
            @Override
            public boolean matches( Object other )
            {
                if ( other instanceof Integer )
                {
                    Integer o = (Integer)other;
                    return ( o < i );
                }
                else
                {
                    return false;
                }
            }

            @Override
            public void describeTo( Description desc )
            {
                desc.appendText( "less than " + i );
            }
        };
    }
}

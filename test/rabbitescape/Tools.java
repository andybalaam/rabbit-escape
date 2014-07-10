package rabbitescape;

import java.util.Arrays;

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
}

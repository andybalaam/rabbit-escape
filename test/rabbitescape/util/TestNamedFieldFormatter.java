package rabbitescape.util;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.IsEqual.*;
import static rabbitescape.util.NamedFieldFormatter.*;
import static rabbitescape.util.Util.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class TestNamedFieldFormatter
{
    @Test
    public void Empty_string_comes_out_empty()
    {
        assertThat( format( "", map() ), equalTo( "" ) );
    }

    @Test
    public void No_substitutions_returns_format_string()
    {
        assertThat( format( "a b c", map() ), equalTo( "a b c" ) );
    }

    @Test
    public void Substitute_each_param_once()
    {
        assertThat(
            format( "a ${b}${cc}", map( "b", "BBB", "cc", "C" ) ),
            equalTo( "a BBBC" )
        );
    }

    @Test
    public void Substitute_some_params_multiple_times()
    {
        assertThat(
            format( "${cc}a ${b}${cc}${cc}", map( "b", "BBB", "cc", "C" ) ),
            equalTo( "Ca BBBCC" )
        );
    }

    private static Map<String, Object> map( Object... keysAndValues )
    {
        HashMap<String, Object> ret = new HashMap<String, Object>();

        reAssert( keysAndValues.length % 2 == 0 );

        for ( int i = 0; i < keysAndValues.length; i += 2 )
        {
            ret.put( (String)keysAndValues[ i ], keysAndValues[ i + 1 ] );
        }

        return ret;
    }
}

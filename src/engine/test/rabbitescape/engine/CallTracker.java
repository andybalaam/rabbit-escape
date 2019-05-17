package rabbitescape.engine;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import rabbitescape.engine.util.NamedFieldFormatter;

import java.util.HashMap;
import java.util.Map;

import static rabbitescape.engine.util.Util.*;

public class CallTracker
{
    StringBuffer calls = new StringBuffer();

    public void track( String method, Object... arguments )
    {
        Map<String, Object> map = new HashMap<>();
        map.put( "method",    method );

        map.put(
            "arguments",
            join(
                 ",",
                 map( stringify(), arguments, new String[ 0 ] )
            )
        );

        calls.append(
            NamedFieldFormatter.format( "${method}(${arguments})\n", map )
        );
    }

    private Function<Object, String> stringify()
    {
        return new Function<Object, String>()
        {
            @Override
            public String apply( Object input )
            {
                return input.toString();
            }
        };
    }

    public void assertCalls( String... expected )
    {
        if ( expected.length == 0 )
        {
            assertThat( calls.toString(), equalTo( "" ) );
        }
        else
        {
            assertThat(
                calls.toString(), equalTo( join( "\n", expected ) + "\n" ) );
        }
    }
}

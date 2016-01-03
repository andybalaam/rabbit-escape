package rabbitescape.engine.util;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Arrays;

import org.junit.Test;

public class TestUtil
{
    @Test
    public void Chain_3a()
    {
        String[] ab = new String[] {"a", "b"};
        String[] cd = new String[] {"c", "d"};
        String[] ef = new String[] {"e", "f"};
        
        Iterable<String> chained = Util.chain( Arrays.asList( ab ), 
                                               Arrays.asList( cd ), 
                                               Arrays.asList( ef ) );
        assertThat( Util.list( chained ).toArray( new String[] {} ), 
            equalTo( new String[] {"a", "b", "c", "d", "e", "f" } ) );
    }

    @Test
    public void Chain_3b()
    {
        String[] a = new String[] {"a"};
        String[] _bcd = new String[] {};
        String[] efg = new String[] {"e", "f", "g"};
        String[] _h = new String[] {};
        
        Iterable<String> chained = Util.chain( Arrays.asList( a ), 
                                               Arrays.asList( _bcd ), 
                                               Arrays.asList( efg ), 
                                               Arrays.asList( _h ) );
        assertThat( Util.list( chained ).toArray( new String[] {} ), 
            equalTo( new String[] {"a", "e", "f", "g" } ) );
    }
    
    @Test
    public void Concat_3()
    {
        String[] a = new String[] {"a"};
        String[] _bcd = new String[] {};
        String[] efg = new String[] {"e", "f", "g"};
        
        String[] concated = Util.concat( a, _bcd, efg );
        
        assertThat( concated, equalTo( new String[] {"a", "e", "f", "g" } ) );
    }
    
}

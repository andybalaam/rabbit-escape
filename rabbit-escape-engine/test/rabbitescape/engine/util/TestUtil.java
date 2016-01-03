package rabbitescape.engine.util;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Arrays;

import org.junit.Test;

public class TestUtil
{
    @Test
    public void Chain_3()
    {
        String[] ab = new String[] {"a", "b"};
        String[] cd = new String[] {"c", "d"};
        String[] ef = new String[] {"e", "f"};
        
        Iterable<String> chained = Util.chain( Arrays.asList(ab), 
                                               Arrays.asList(cd), 
                                               Arrays.asList(ef) );
        assertThat( Util.list( chained ).toArray( new String[] {}), 
            equalTo( new String[] {"a", "b", "c", "d", "e", "f" }) );
    }

}

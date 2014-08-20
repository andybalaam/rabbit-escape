package rabbitescape.engine;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

public class TestLoadWorldFile
{
    @Test
    public void Read_the_lines_in_a_resource()
    {
        assertThat(
            LoadWorldFile.readLinesFromResource( "test1/fortest.rel" ),
            equalTo( new String[] { "a", "b", "c" } )
        );
    }
}

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

    @Test
    public void Pretty_name_from_file_name()
    {
        assertThat(
            LoadWorldFile.levelName( "easy/level_01.rel" ),
            equalTo( "easy level 1" )
        );
    }
}

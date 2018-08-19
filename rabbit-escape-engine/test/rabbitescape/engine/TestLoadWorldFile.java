package rabbitescape.engine;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestLoadWorldFile
{
    @Test
    public void Read_the_lines_in_a_resource()
    {
        assertThat(
            LoadWorldFile.readLinesFromResource( "test1/fortest.rel" ),
            equalTo( new String[] { "a", "b", "c", ":name=For Test!" } )
        );
    }

    @Test
    public void Pretty_name_from_file_name()
    {
        assertThat(
            LoadWorldFile.levelName( "easy/01_Digging-practice.rel" ),
            equalTo( "easy 1 Digging-practice" )
        );
    }
}

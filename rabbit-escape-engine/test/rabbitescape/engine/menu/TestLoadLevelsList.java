package rabbitescape.engine.menu;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import java.util.Arrays;

public class TestLoadLevelsList
{
    @Test
    public void Test_levels_get_loaded_correctly()
    {
        LevelsList levels = LoadLevelsList.load( "test1", "test2" );

        assertThat(
            levels.inDir( "test1" ),
            equalTo(
                Arrays.asList(
                    new LevelsList.LevelInfo( "fortest", "For Test!" )
                )
            )
        );

        assertThat(
            levels.inDir( "test2" ),
            equalTo(
                Arrays.asList(
                    new LevelsList.LevelInfo( "lev1", "Test 9 no 1" ),
                    new LevelsList.LevelInfo( "lev2", "test 2" ),
                    new LevelsList.LevelInfo( "lev3", "Test 3 ..." )
                )
            )
        );
    }
}

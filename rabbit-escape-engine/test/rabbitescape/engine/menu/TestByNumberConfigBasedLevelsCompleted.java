package rabbitescape.engine.menu;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.*;

import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.TestConfig;

public class TestByNumberConfigBasedLevelsCompleted
{
    @Test
    public void Report_highest_level_from_config()
    {
        FakeConfig fakeConfig = new FakeConfig( "{\"bar\":2,\"foo\":3}" );

        ByNumberConfigBasedLevelsCompleted lc =
            new ByNumberConfigBasedLevelsCompleted( fakeConfig );

        int ans = lc.highestLevelCompleted( "foo" );

        assertThat( ans, equalTo( 3 ) );

        // We called set with the right config key and value
        assertThat(
            fakeConfig.log.get( 0 ),
            equalTo( "get levels.completed" )
        );
        assertThat( fakeConfig.log.size(), equalTo( 1 ) );
    }

    @Test
    public void Save_changes_to_config_new_dir()
    {
        FakeConfig fakeConfig = new FakeConfig( "{\"bar\":2,\"foo\":3}" );

        ByNumberConfigBasedLevelsCompleted lc =
            new ByNumberConfigBasedLevelsCompleted( fakeConfig );

        lc.setCompletedLevel( "baz", 1 );

        // We called set with the right config key and value
        assertThat(
            fakeConfig.log.get( 1 ),  // First was a get
            equalTo( "set levels.completed {\"bar\":2,\"baz\":1,\"foo\":3}" )
        );
        assertThat(
            fakeConfig.log.get( 2 ),
            equalTo( "save" )
        );
        assertThat( fakeConfig.log.size(), equalTo( 3 ) );
    }

    @Test
    public void Save_changes_to_config_existing_dir()
    {
        FakeConfig fakeConfig = new FakeConfig( "{\"bar\":2,\"foo\":3}" );

        ByNumberConfigBasedLevelsCompleted lc =
            new ByNumberConfigBasedLevelsCompleted( fakeConfig );

        lc.setCompletedLevel( "foo", 4 );

        // We called get with the right config key
        assertThat(
            fakeConfig.log.get( 1 ),  // First was a get
            equalTo( "set levels.completed {\"bar\":2,\"foo\":4}" )
        );
        assertThat(
            fakeConfig.log.get( 2 ),
            equalTo( "save" )
        );
        assertThat( fakeConfig.log.size(), equalTo( 3 ) );
    }

    @Test
    public void Characters_stripped_from_set_names()
    {
        assertThat( ByNumberConfigBasedLevelsCompleted.stripNumber_( "01_easy" ),
            equalTo( "easy" ) );
    }

    // ---

    private static class FakeConfig extends Config
    {
        private final String getAnswer;
        public final List<String> log;

        public FakeConfig( String getAnswer )
        {
            super( null, new TestConfig.EmptyConfigStorage() );

            this.getAnswer = getAnswer;
            this.log = new ArrayList<String>();
        }

        @Override
        public void set( String key, String value )
        {
            log.add( "set " + key + " " + value );
        }

        @Override
        public String get( String key )
        {
            log.add( "get " + key );
            return getAnswer;
        }

        @Override
        public void save()
        {
            log.add( "save" );
        }
    }
}

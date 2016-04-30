package rabbitescape.engine.menu;

import static rabbitescape.engine.menu.FakeLevelsList.*;
import static rabbitescape.engine.util.Util.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.TestConfig;

import static rabbitescape.engine.menu.ByNameConfigBasedLevelsCompleted.canonicalName;

public class TestByNameConfigBasedLevelsCompleted
{
    @Test
    public void Canonical_name_of_simple_string_is_the_string()
    {
        assertThat( "abcxyz", isEqualToItsCanonicalVersion() );
        assertThat( "c",      isEqualToItsCanonicalVersion() );
        assertThat( "zya",    isEqualToItsCanonicalVersion() );
        assertThat( "1z2a3",  isEqualToItsCanonicalVersion() );
    }

    @Test( expected = ByNameConfigBasedLevelsCompleted.EmptyLevelName.class )
    public void Empty_name_is_not_allowed()
    {
        canonicalName( "" );
    }

    @Test
    public void Spaces_become_underscores()
    {
        assertThat( canonicalName( "abc xyz" ), equalTo( "abc_xyz" ) );
        assertThat( canonicalName( " a " ), equalTo( "_a_" ) );
    }

    @Test
    public void Punctuation_becomes_underscores()
    {
        assertThat( canonicalName( "abc,xyz?" ), equalTo( "abc_xyz_" ) );
    }

    @Test
    public void Upper_case_becomes_lower_case()
    {
        assertThat( canonicalName( "AbcxYZ" ), equalTo( "abcxyz" ) );
    }

    @Test
    public void Unicode_becomes_underscore()
    {
        assertThat(
            canonicalName( "Pile of poo \uD83D\uDCA9 is coo" ),
            equalTo( "pile_of_poo___is_coo" )
        );

        assertThat(
            canonicalName( "Go to \u5317\u4eac\u5e02" ),
            equalTo( "go_to____" )
        );
    }

    @Test
    public void Report_highest_level_from_config_where_some_completed()
    {
        FakeConfig fakeConfig = new FakeConfig(
            "level_01_foo_1",
            "level_01_foo_2",
            "level_01_foo_3"
        );

        LevelsList levelsList = new LevelsList(
            levelSet( "01_foo", 10 ),
            levelSet( "02_bar", 10 )
        );

        ByNameConfigBasedLevelsCompleted lc =
            new ByNameConfigBasedLevelsCompleted( fakeConfig, levelsList );

        assertThat( lc.highestLevelCompleted( "01_foo" ), equalTo( 3 ) );
    }

    @Test
    public void Report_highest_level_from_config_where_none_completed()
    {
        FakeConfig fakeConfig = new FakeConfig(
            "level_01_foo_1",
            "level_01_foo_2",
            "level_01_foo_3"
        );

        LevelsList levelsList = new LevelsList(
            levelSet( "01_foo", 10 ),
            levelSet( "02_bar", 10 )
        );

        ByNameConfigBasedLevelsCompleted lc =
            new ByNameConfigBasedLevelsCompleted( fakeConfig, levelsList );

        assertThat( lc.highestLevelCompleted( "02_bar" ), equalTo( 0 ) );
    }

    @Test
    public void Report_highest_level_from_config_where_all_completed()
    {
        FakeConfig fakeConfig = new FakeConfig(
            "level_foo_1",
            "level_foo_2",
            "level_foo_3",
            "level_foo_4",
            "level_foo_5",
            "level_foo_6",
            "level_foo_7",
            "level_foo_8",
            "level_foo_9",
            "level_foo_10"
        );

        LevelsList levelsList = new LevelsList(
            levelSet( "foo", 10 ),
            levelSet( "bar", 10 )
        );

        ByNameConfigBasedLevelsCompleted lc =
            new ByNameConfigBasedLevelsCompleted( fakeConfig, levelsList );

        assertThat( lc.highestLevelCompleted( "foo" ), equalTo( 10 ) );
    }

    @Test
    public void Save_changes_to_config_new_dir()
    {
        FakeConfig fakeConfig = new FakeConfig(
            "level_foo_1",
            "level_foo_2",
            "level_foo_3"
        );

        LevelsList levelsList = new LevelsList(
            levelSet( "foo", 10 ),
            levelSet( "bar", 10 )
        );

        ByNameConfigBasedLevelsCompleted lc =
            new ByNameConfigBasedLevelsCompleted( fakeConfig, levelsList );

        lc.setCompletedLevel( "bar", 1 );

        // We called set with the right config key and value
        assertThat(
            fakeConfig.log.get( 1 ),  // 0 was a get
            equalTo(
                "set levels.completed [" +
                "\"level_bar_1\"," +
                "\"level_foo_1\"," +
                "\"level_foo_2\"," +
                "\"level_foo_3\"" +
                "]"
            )
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
        FakeConfig fakeConfig = new FakeConfig(
            "level_foo_1",
            "level_foo_2",
            "level_foo_3"
        );

        LevelsList levelsList = new LevelsList(
            levelSet( "foo", 10 ),
            levelSet( "bar", 10 )
        );

        ByNameConfigBasedLevelsCompleted lc =
            new ByNameConfigBasedLevelsCompleted( fakeConfig, levelsList );

        lc.setCompletedLevel( "foo", 4 );

        assertThat(
            fakeConfig.log.get( 1 ),  // 0 was a get
            equalTo(
                "set levels.completed [" +
                "\"level_foo_1\"," +
                "\"level_foo_2\"," +
                "\"level_foo_3\"," +
                "\"level_foo_4\"" +
                "]"
            )
        );
        assertThat(
            fakeConfig.log.get( 2 ),
            equalTo( "save" )
        );
        assertThat( fakeConfig.log.size(), equalTo( 3 ) );
    }

    @Test
    public void No_need_to_update_if_weve_already_completed_a_level()
    {
        FakeConfig fakeConfig = new FakeConfig(
            "level_foo_1",
            "level_foo_2",
            "level_foo_3"
        );

        LevelsList levelsList = new LevelsList(
            levelSet( "foo", 10 ),
            levelSet( "bar", 10 )
        );

        ByNameConfigBasedLevelsCompleted lc =
            new ByNameConfigBasedLevelsCompleted( fakeConfig, levelsList );

        // Two useless calls
        lc.setCompletedLevel( "foo", 3 );
        lc.setCompletedLevel( "foo", 1 );

        // Just gets - nothing saved
        assertThat(
            fakeConfig.log.get( 0 ),
            equalTo( "get levels.completed" )
        );
        assertThat(
            fakeConfig.log.get( 1 ),
            equalTo( "get levels.completed" )
        );
        assertThat( fakeConfig.log.size(), equalTo( 2 ) );
    }

    // ---

    private static class FakeConfig extends Config
    {
        private final String getAnswer;
        public final List<String> log;

        public FakeConfig( String... completedLevelNames )
        {
            super( null, new TestConfig.EmptyConfigStorage() );

            this.getAnswer = makeAnswer( completedLevelNames );
            this.log = new ArrayList<String>();
        }

        private String makeAnswer( String[] completedLevelNames )
        {
            return "["
                + join( ",", map( quoted(), completedLevelNames ) )
                + "]";
        }

        private static Function<String, String> quoted()
        {
            return new Function<String, String>()
            {
                @Override
                public String apply( String t )
                {
                    return "\"" + t + "\"";
                }
            };
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

    // ---

    private static Matcher<String> isEqualToItsCanonicalVersion()
    {
        return new BaseMatcher<String>()
        {
            String str;

            @Override
            public boolean matches( Object obj )
            {
                if ( !( obj instanceof String ) )
                {
                    return false;
                }

                str = (String)obj;

                return str.equals( canonicalName( str ) );
            }

            @Override
            public void describeTo( Description description )
            {
                description.appendText( canonicalName( str ) );
            }
        };
    }


}

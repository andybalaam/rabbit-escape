package rabbitescape.engine.config;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import rabbitescape.engine.util.Util;

public class TestConfig
{
    @Test
    public void When_no_underlying_values_defaults_are_returned()
    {
        Config cfg = new Config( simpleSchema(), new EmptyConfigStorage() );

        assertThat( cfg.get( "key1" ), equalTo( "defaultValue1" ) );
        assertThat( cfg.get( "key2" ), equalTo( "defaultValue2" ) );
    }

    @Test
    public void You_get_back_the_value_you_set()
    {
        Config cfg = new Config( simpleSchema(), new MemoryConfigStorage() );

        // Sanity
        assertThat( cfg.get( "key1" ), equalTo( "defaultValue1" ) );

        // This is what we are testing
        cfg.set( "key1", "setValue1" );

        // What you set stuck
        assertThat( cfg.get( "key1" ), equalTo( "setValue1" ) );
    }

    @Test( expected = Config.UnknownKey.class )
    public void Getting_unknown_config_key_is_an_error()
    {
        Config cfg = new Config( simpleSchema(), new EmptyConfigStorage() );

        cfg.get( "unknownkey" );
    }

    @Test( expected = Config.UnknownKey.class )
    public void Setting_unknown_config_key_is_an_error()
    {
        Config cfg = new Config( simpleSchema(), new EmptyConfigStorage() );

        cfg.set( "unknownkey", "val" );
    }

    @Test
    public void Calling_save_saves_to_underlying_storage()
    {
        SaveableConfigStorage storage = new SaveableConfigStorage();
        Config cfg = new Config( simpleSchema(), storage );

        assertThat( storage.savedConfig, is( nullValue() ) );

        cfg.save();

        assertThat( storage.savedConfig, sameInstance( cfg ) );
    }

    @Test
    public void Iterating_through_keys_lists_all()
    {
        Config cfg = new Config( simpleSchema(), new EmptyConfigStorage() );

        assertThat(
            Util.join( ", ", cfg.keys() ),
            equalTo( "config.version, key1, key2, key3" )
        );
    }

    @Test
    public void No_version_info_means_version_0()
    {
        Config cfg = new Config( simpleSchema(), new EmptyConfigStorage() );

        assertThat( cfg.version(), equalTo( 0 ) );
    }

    @Test
    public void Stored_version_info_gets_read()
    {
        MemoryConfigStorage storage = new MemoryConfigStorage();
        storage.set( Config.CFG_VERSION, "12" );
        Config cfg = new Config( simpleSchema(), storage );

        assertThat( cfg.version(), equalTo( 12 ) );
    }

    @Test
    public void When_no_existing_version_all_supplied_upgrades_are_run()
    {
        List<String> log = new ArrayList<String>();
        FakeConfigUpgrade to1 = new FakeConfigUpgrade( "1", log );
        FakeConfigUpgrade to2 = new FakeConfigUpgrade( "2", log );
        FakeConfigUpgrade to3 = new FakeConfigUpgrade( "3", log );

        new Config( simpleSchema(), new MemoryConfigStorage(), to1, to2, to3 );

        assertThat( Util.join( "", log ), equalTo( "123" ) );
    }

    @Test
    public void When_no_existing_version_and_no_upgrades_everything_is_ok()
    {
        new Config( simpleSchema(), new MemoryConfigStorage() );
    }

    @Test
    public void When_existing_version_set_all_subsequent_upgrades_are_run()
    {
        List<String> log = new ArrayList<String>();
        FakeConfigUpgrade to1 = new FakeConfigUpgrade( "1", log );
        FakeConfigUpgrade to2 = new FakeConfigUpgrade( "2", log );
        FakeConfigUpgrade to3 = new FakeConfigUpgrade( "3", log );
        FakeConfigUpgrade to4 = new FakeConfigUpgrade( "4", log );

        MemoryConfigStorage storage = new MemoryConfigStorage();
        storage.set( Config.CFG_VERSION, "2" );

        new Config( simpleSchema(), storage, to1, to2, to3, to4 );

        assertThat( Util.join( "", log ), equalTo( "34" ) );
    }

    @Test
    public void When_version_is_up_to_date_no_upgrades_are_run()
    {
        List<String> log = new ArrayList<String>();
        FakeConfigUpgrade to1 = new FakeConfigUpgrade( "1", log );
        FakeConfigUpgrade to2 = new FakeConfigUpgrade( "2", log );
        FakeConfigUpgrade to3 = new FakeConfigUpgrade( "3", log );

        MemoryConfigStorage storage = new MemoryConfigStorage();
        storage.set( Config.CFG_VERSION, "3" );

        new Config( simpleSchema(), storage, to1, to2, to3 );

        assertThat( Util.join( "", log ), equalTo( "" ) );
    }

    @Test
    public void Storage_is_saved_after_upgrade()
    {
        List<String> log = new ArrayList<String>();
        FakeConfigUpgrade to1 = new FakeConfigUpgrade( "1", log );
        FakeConfigUpgrade to2 = new FakeConfigUpgrade( "2", log );
        FakeConfigUpgrade to3 = new FakeConfigUpgrade( "3", log );

        MemoryConfigStorage storage = new MemoryConfigStorage();

        // This is what we are testing - upgrade to version 3
        new Config( simpleSchema(), storage, to1, to2, to3 );

        assertThat( storage.saves, equalTo( Arrays.asList( "1", "2", "3" ) ) );
    }

    @Test
    public void Storage_is_not_saved_when_no_upgrade()
    {
        List<String> log = new ArrayList<String>();
        FakeConfigUpgrade to1 = new FakeConfigUpgrade( "1", log );
        FakeConfigUpgrade to2 = new FakeConfigUpgrade( "2", log );
        FakeConfigUpgrade to3 = new FakeConfigUpgrade( "3", log );

        MemoryConfigStorage storage = new MemoryConfigStorage();
        storage.set( Config.CFG_VERSION, "3" );

        // This is what we are testing - already at 3 so ne need to upgrade
        new Config( simpleSchema(), storage, to1, to2, to3 );

        assertThat( storage.saves, equalTo( Collections.<String>emptyList() ) );
    }

    // --

    public static ConfigSchema simpleSchema()
    {
        ConfigSchema def = new ConfigSchema();
        def.set( "key1", "defaultValue1", "desc1" );
        def.set( "key2", "defaultValue2", "desc2" );
        def.set( "key3", "defaultValue3", "desc3" );
        return def;
    }

    public static class EmptyConfigStorage implements IConfigStorage
    {
        @Override
        public void set( String key, String value )
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public String get( String key )
        {
            return null;
        }

        @Override
        public void save( Config config )
        {
            throw new UnsupportedOperationException();
        }
    }

    private static class SaveableConfigStorage implements IConfigStorage
    {
        public Config savedConfig = null;

        @Override
        public void set( String key, String value )
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public String get( String key )
        {
            return null;
        }

        @Override
        public void save( Config config )
        {
            savedConfig = config;
        }
    }

    private static class FakeConfigUpgrade implements IConfigUpgrade
    {
        private final String newVersion;
        private final List<String> log;

        public FakeConfigUpgrade( String newVersion, List<String> log )
        {
            this.newVersion = newVersion;
            this.log = log;
        }

        @Override
        public void run( IConfigStorage storage )
        {
            storage.set( Config.CFG_VERSION, newVersion );
            log.add( newVersion );
        }
    }
}

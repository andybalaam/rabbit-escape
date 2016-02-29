package rabbitescape.engine.config;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

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
        Config cfg = new Config( simpleSchema(), null );

        assertThat(
            Util.join( ", ", cfg.keys() ),
            equalTo( "key1, key2, key3" )
        );
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

    private static class EmptyConfigStorage implements IConfigStorage
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
            throw new UnsupportedOperationException();
        }

        @Override
        public void save( Config config )
        {
            savedConfig = config;
        }
    }
}

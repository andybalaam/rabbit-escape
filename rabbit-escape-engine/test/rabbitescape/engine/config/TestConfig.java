package rabbitescape.engine.config;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import rabbitescape.engine.util.FakeFileSystem;

public class TestConfig
{
    @Test
    public void Loading_from_nonexistent_file_provides_default_values()
    {
        Config cfg = new Config( simpleDefinition(), null, null );

        assertThat( cfg.get( "key1" ), equalTo( "defaultValue1" ) );
        assertThat( cfg.get( "key2" ), equalTo( "defaultValue2" ) );
    }

    @Test
    public void You_get_back_the_value_you_set()
    {
        Config cfg = new Config( simpleDefinition(), null, null );

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
        Config cfg = new Config( simpleDefinition(), null, null );

        cfg.get( "unknownkey" );
    }

    @Test( expected = Config.UnknownKey.class )
    public void Setting_unknown_config_key_is_an_error()
    {
        Config cfg = new Config( simpleDefinition(), null, null );

        cfg.set( "unknownkey", "val" );
    }

    @Test
    public void Loading_from_existing_file_provides_loaded_values()
    {
        String fileName = "~/.rabbitescape/config/ui.properties";

        FakeFileSystem fs = new FakeFileSystem(
            fileName,
            new String[] {
                "key1=my value 1",
                "key3=my value 3"
            }
        );

        Config cfg = new Config( simpleDefinition(), fs, fileName );

        assertThat( cfg.get( "key1" ), equalTo( "my value 1" ) );
        assertThat( cfg.get( "key2" ), equalTo( "defaultValue2" ) );
        assertThat( cfg.get( "key3" ), equalTo( "my value 3" ) );
    }


    @Test
    public void Saving_saves_values_and_adds_comments() throws Exception
    {
        String fileName = "~/.rabbitescape/config/ui.properties";

        FakeFileSystem fs = new FakeFileSystem();

        Config cfg = new Config( simpleDefinition(), fs, fileName );
        cfg.set( "key2", "my value 2" );

        // Sanity
        assertThat( fs.exists( "~/.rabbitescape/config" ), is( false ) );
        assertThat( fs.exists( fileName ), is( false ) );

        cfg.save();

        assertThat( fs.exists( "~" ), is( true ) );
        assertThat( fs.exists( "~/.rabbitescape" ), is( true ) );
        assertThat( fs.exists( "~/.rabbitescape/config" ), is( true ) );

        assertThat(
            fs.readLines( fileName ),
            equalTo(
                new String[] {
                    "# desc1",
                    "#key1=defaultValue1",
                    "",
                    "# desc2",
                    "key2=my value 2",
                    "",
                    "# desc3",
                    "#key3=defaultValue3"
                }
            )
        );
    }

    // --

    public static Config.Definition simpleDefinition()
    {
        Config.Definition def = new Config.Definition();
        def.set( "key1", "defaultValue1", "desc1" );
        def.set( "key2", "defaultValue2", "desc2" );
        def.set( "key3", "defaultValue3", "desc3" );
        return def;
    }
}

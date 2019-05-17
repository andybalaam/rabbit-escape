package rabbitescape.engine.config;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import rabbitescape.engine.util.FakeFileSystem;

public class TestConfigFile
{
    @Test
    public void You_get_back_the_value_you_set()
    {
        ConfigFile cfg = new ConfigFile( null, null );

        // Sanity
        assertThat( cfg.get( "key1" ), is( nullValue() ) );

        // This is what we are testing
        cfg.set( "key1", "setValue1" );

        // What you set stuck
        assertThat( cfg.get( "key1" ), equalTo( "setValue1" ) );
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

        ConfigFile cfg = new ConfigFile( fs, fileName );

        assertThat( cfg.get( "key1" ), equalTo( "my value 1" ) );
        assertThat( cfg.get( "key2" ), is( nullValue() ) );
        assertThat( cfg.get( "key3" ), equalTo( "my value 3" ) );
    }


    @Test
    public void Saving_saves_values_and_defaults_and_adds_comments()
        throws Exception
    {
        String fileName = "~/.rabbitescape/config/ui.properties";

        FakeFileSystem fs = new FakeFileSystem();

        ConfigFile cfg = new ConfigFile( fs, fileName );
        cfg.set( "key2", "my value 2" );

        // Sanity
        assertThat( fs.exists( "~/.rabbitescape/config" ), is( false ) );
        assertThat( fs.exists( fileName ), is( false ) );

        cfg.save( new Config( TestConfig.simpleSchema(), cfg ) );

        assertThat( fs.exists( "~" ), is( true ) );
        assertThat( fs.exists( "~/.rabbitescape" ), is( true ) );
        assertThat( fs.exists( "~/.rabbitescape/config" ), is( true ) );

        assertThat(
            fs.readLines( fileName ),
            equalTo(
                new String[] {
                    "# The version of this config file.",
                    "#config.version=0",
                    "",
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
}

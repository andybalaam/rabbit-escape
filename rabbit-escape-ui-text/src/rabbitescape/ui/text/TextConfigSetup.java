package rabbitescape.ui.text;

import static rabbitescape.engine.config.ConfigKeys.*;

import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.ConfigFile;
import rabbitescape.engine.config.ConfigSchema;
import rabbitescape.engine.util.RealFileSystem;

public class TextConfigSetup
{
    private static final String CONFIG_PATH =
        "~/.rabbitescape/config/text.properties"
            .replace( "~", System.getProperty( "user.home" ) );

    public static Config createConfig()
    {
        ConfigSchema definition = new ConfigSchema();

        definition.set(
            CFG_LEVELS_COMPLETED,
            "{}",
            "Which level you have got to in each level set."
        );

        definition.set(
            CFG_DEBUG_PRINT_STATES,
            String.valueOf( false ),
            "Rabbit states are printed to System.out."
        );

        return new Config(
            definition, new ConfigFile( new RealFileSystem(), CONFIG_PATH ) );
    }
}

package rabbitescape.ui.text;

import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.ConfigFile;
import rabbitescape.engine.config.ConfigSchema;
import rabbitescape.engine.config.RealConfigUpgrades;
import rabbitescape.engine.config.StandardConfigSchema;
import rabbitescape.engine.util.RealFileSystem;

public class TextConfigSetup
{
    private static final String CONFIG_PATH =
        "~/.rabbitescape/config/text.properties"
            .replace( "~", System.getProperty( "user.home" ) );

    public static Config createConfig()
    {
        ConfigSchema definition = new ConfigSchema();
        StandardConfigSchema.setSchema( definition );

        return new Config(
            definition,
            new ConfigFile( new RealFileSystem(), CONFIG_PATH ),
            RealConfigUpgrades.realConfigUpgrades()
        );
    }
}

package rabbitescape.ui.android;

import android.content.SharedPreferences;

import static rabbitescape.engine.config.ConfigKeys.*;

import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.ConfigSchema;
import rabbitescape.engine.config.IConfigUpgrade;

public class AndroidConfigSetup
{
    public static final String CFG_MUTED = "muted";

    public static Config createConfig( SharedPreferences prefs )
    {
        return createConfig( prefs, realUpgrades() );
    }

    private static IConfigUpgrade[] realUpgrades()
    {
        return new IConfigUpgrade[]
        {
            new AndroidConfigUpgradeTo1()
        };
    }

    public static Config createConfig( SharedPreferences prefs, IConfigUpgrade... upgrades )
    {
        ConfigSchema definition = new ConfigSchema();

        definition.set(
            CFG_LEVELS_COMPLETED,
            "{}",
            ""
        );

        definition.set(
            CFG_DEBUG_PRINT_STATES,
            String.valueOf( false ),
            ""
        );

        definition.set( CFG_MUTED, String.valueOf( false ), "" );

        return new Config( definition, new AndroidConfigStorage( prefs ), upgrades );
    }
}

package rabbitescape.ui.android;

import android.content.SharedPreferences;

import static rabbitescape.engine.config.ConfigKeys.*;

import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.ConfigSchema;

public class AndroidConfigSetup
{
    public static Config createConfig( SharedPreferences prefs )
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

        return new Config( definition, new AndroidConfigStorage( prefs ) );
    }
}

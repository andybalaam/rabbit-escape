package rabbitescape.engine.config;

import static rabbitescape.engine.config.ConfigKeys.*;

public class StandardConfigSchema
{
    /**
     * Set config schema values that are standard across all platforms.
     */
    public static void setSchema( ConfigSchema definition )
    {
        definition.set(
            CFG_LEVELS_COMPLETED,
            "[]",
            "Which level you have got to in each level set."
        );

        definition.set(
            CFG_DEBUG_PRINT_STATES,
            String.valueOf( false ),
            "Rabbit states are printed to System.out."
        );

        definition.set(
            CFG_WATER_DYN_CONTENTS_PER_PARTICLE,
            String.valueOf( true ),
            "Particle count is auto-reduced for struggling hardware."
        );

        definition.set(
            CFG_WATER_CONTENTS_PER_PARTICLE,
            String.valueOf( 4 ),
            "Smaller values lead to more particles"
        );
    }

}

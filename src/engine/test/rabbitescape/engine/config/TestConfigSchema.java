package rabbitescape.engine.config;

import org.junit.Test;

public class TestConfigSchema
{
    @Test( expected = ConfigSchema.KeyNotAllowed.class )
    public void Config_version_is_not_allowed_as_a_key()
    {
        ConfigSchema schema = new ConfigSchema();
        schema.set( Config.CFG_VERSION, "", "" );
    }
}

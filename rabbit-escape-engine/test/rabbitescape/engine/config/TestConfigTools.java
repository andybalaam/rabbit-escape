package rabbitescape.engine.config;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import rabbitescape.engine.config.Config.Definition;

public class TestConfigTools
{
    @Test
    public void Can_get_and_set_ints()
    {
        Config cfg = new Config( TestConfig.simpleDefinition(), null, null );

        ConfigTools.setInt( cfg, "key1", 3 );

        assertThat( ConfigTools.getInt( cfg, "key1" ), is( 3 ) );
    }

    @Test
    public void Default_that_looks_like_an_int_can_be_treated_as_one()
    {
        Definition definition = new Config.Definition();
        definition.set( "num", "45", "" );
        Config cfg = new Config( definition, null, null );

        assertThat( ConfigTools.getInt( cfg, "num" ), is( 45 ) );
    }
}

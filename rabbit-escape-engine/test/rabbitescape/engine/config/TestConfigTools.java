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

    @Test
    public void Can_get_and_set_bools()
    {
        Config.Definition def = new Config.Definition();
        def.set( "key1", "true", "desc1" );
        def.set( "key2", "false", "desc2" );

        Config cfg = new Config( def, null, null );

        assertThat( ConfigTools.getBool( cfg, "key1" ), is( true ) );
        assertThat( ConfigTools.getBool( cfg, "key2" ), is( false ) );

        ConfigTools.setBool( cfg, "key1", false );

        assertThat( ConfigTools.getBool( cfg, "key1" ), is( false ) );
    }

}

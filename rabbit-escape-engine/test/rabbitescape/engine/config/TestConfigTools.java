package rabbitescape.engine.config;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.HashMap;
import java.util.Map;

import static rabbitescape.engine.util.Util.*;

import org.junit.Test;


public class TestConfigTools
{
    @Test
    public void Can_get_and_set_ints()
    {
        ConfigFile cfg = new ConfigFile( TestConfigFile.simpleDefinition(), null, null );

        ConfigTools.setInt( cfg, "key1", 3 );

        assertThat( ConfigTools.getInt( cfg, "key1" ), is( 3 ) );
    }

    @Test
    public void Default_that_looks_like_an_int_can_be_treated_as_one()
    {
        ConfigSchema definition = new ConfigSchema();
        definition.set( "num", "45", "" );
        ConfigFile cfg = new ConfigFile( definition, null, null );

        assertThat( ConfigTools.getInt( cfg, "num" ), is( 45 ) );
    }

    @Test
    public void Can_get_and_set_bools()
    {
        ConfigSchema def = new ConfigSchema();
        def.set( "key1", "true", "desc1" );
        def.set( "key2", "false", "desc2" );

        ConfigFile cfg = new ConfigFile( def, null, null );

        assertThat( ConfigTools.getBool( cfg, "key1" ), is( true ) );
        assertThat( ConfigTools.getBool( cfg, "key2" ), is( false ) );

        ConfigTools.setBool( cfg, "key1", false );

        assertThat( ConfigTools.getBool( cfg, "key1" ), is( false ) );
    }

    @Test
    public void Can_get_and_set_maps_of_string()
    {
        // Make a config with default map with 1 key
        ConfigSchema def = new ConfigSchema();
        def.set( "key1", "{\"a\":\"b\"}", "desc1" );
        ConfigFile cfg = new ConfigFile( def, null, null );

        // Get the map value out
        assertThat(
            ConfigTools.getMap( cfg, "key1", String.class ),
            equalTo( stringMap( "a", "b" ) )
        );

        // Set a different value
        Map<String, String> mp = stringMap(
            "aaa", "xyz",
            "bbb", "DFG"
        );
        ConfigTools.setMap( cfg, "key1", mp );

        // Check it comes out unchanged
        assertThat(
            ConfigTools.getMap( cfg, "key1", String.class ),
            equalTo( mp )
        );

        // Perhaps overkill: assert storage format
        assertThat(
            cfg.get( "key1" ),
            equalTo( "{\"aaa\":\"xyz\",\"bbb\":\"DFG\"}" )
        );
    }

    @Test
    public void Can_get_empty_map()
    {
        // Make a config with default map with 1 key
        ConfigSchema def = new ConfigSchema();
        def.set( "key1", "{}", "desc1" );
        ConfigFile cfg = new ConfigFile( def, null, null );

        // We get an empty map of the type we ask for
        assertThat(
            ConfigTools.getMap( cfg, "key1", String.class ),
            equalTo( stringMap() )
        );

     // We get an empty map of the type we ask for
        assertThat(
            ConfigTools.getMap( cfg, "key1", Integer.class ),
            equalTo( intMap() )
        );
    }

    @Test
    public void Can_get_and_set_maps_of_int()
    {
        // Make a config with default map with 1 key
        ConfigSchema def = new ConfigSchema();
        def.set( "key1", "{\"a\":3}", "desc1" );
        ConfigFile cfg = new ConfigFile( def, null, null );

        // Get the map value out
        assertThat(
            ConfigTools.getMap( cfg, "key1", Integer.class ),
            equalTo( intMap( "a", 3 ) )
        );

        // Set a different value
        Map<String, Integer> mp = intMap(
            "aaa", 45,
            "bbb", 56
        );
        ConfigTools.setMap( cfg, "key1", mp );

        // Check it comes out unchanged
        assertThat(
            ConfigTools.getMap( cfg, "key1", Integer.class ),
            equalTo( mp )
        );

        // Perhaps overkill: assert storage format
        assertThat(
            cfg.get( "key1" ),
            equalTo( "{\"aaa\":45,\"bbb\":56}" )
        );
    }

    // ---

    // TODO: merge with Util.newMap
    public static Map<String, String> stringMap( String... keysAndValues )
    {
        reAssert( keysAndValues.length % 2 == 0 );

        Map<String, String> ret = new HashMap<>();

        for ( int i = 0; i < keysAndValues.length; i += 2 )
        {
            ret.put( keysAndValues[i], keysAndValues[ i + 1 ] );
        }

        return ret;
    }

    // TODO: merge with Util.newMap?
    public static Map<String, Integer> intMap()
    {
        return new HashMap<String, Integer>();
    }

    // TODO: merge with Util.newMap?
    public static Map<String, Integer> intMap( String k1, int v1 )
    {
        Map<String, Integer> ret = new HashMap<>();
        ret.put( k1, v1 );
        return ret;
    }

    // TODO: merge with Util.newMap?
    public static Map<String, Integer> intMap(
        String k1, int v1, String k2, int v2 )
    {
        Map<String, Integer> ret = new HashMap<>();
        ret.put( k1, v1 );
        ret.put( k2, v2 );
        return ret;
    }
}

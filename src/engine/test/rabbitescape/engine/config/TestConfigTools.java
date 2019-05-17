package rabbitescape.engine.config;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static rabbitescape.engine.util.Util.*;

import org.junit.Test;


public class TestConfigTools
{
    @Test
    public void Can_get_and_set_ints()
    {
        Config cfg = new Config(
            TestConfig.simpleSchema(), new MemoryConfigStorage() );

        ConfigTools.setInt( cfg, "key1", 3 );

        assertThat( ConfigTools.getInt( cfg, "key1" ), is( 3 ) );
    }

    @Test
    public void Default_that_looks_like_an_int_can_be_treated_as_one()
    {
        ConfigSchema definition = new ConfigSchema();
        definition.set( "num", "45", "" );
        Config cfg = new Config( definition, new MemoryConfigStorage() );

        assertThat( ConfigTools.getInt( cfg, "num" ), is( 45 ) );
    }

    @Test
    public void Can_get_and_set_bools()
    {
        ConfigSchema def = new ConfigSchema();
        def.set( "key1", "true", "desc1" );
        def.set( "key2", "false", "desc2" );

        Config cfg = new Config( def, new MemoryConfigStorage() );

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
        Config cfg = new Config( def, new MemoryConfigStorage() );

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
        Config cfg = new Config( def, new MemoryConfigStorage() );

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
        Config cfg = new Config( def, new MemoryConfigStorage() );

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

    @Test
    public void Convert_a_set_to_a_string()
    {
        SortedSet<String> set = new TreeSet<String>();
        set.add( "a" );
        set.add( "bc" );
        set.add( "def" );

        assertThat(
            ConfigTools.setToString( set ),
            equalTo( "[\"a\",\"bc\",\"def\"]" )
        );
    }

    @Test
    public void Can_get_and_set_sets_of_string()
    {
        // Make a config with default map with 1 key
        ConfigSchema def = new ConfigSchema();
        def.set( "key1", "[\"a\",\"bb\",\"\"]", "desc1" );
        Config cfg = new Config( def, new MemoryConfigStorage() );

        // Get the map value out
        assertThat(
            ConfigTools.getSet( cfg, "key1", String.class ),
            equalTo( newSet( "a", "bb", "" ) )
        );

        // Set a different value
        Set<String> st = newSet( "aaa", "DFG", "xyz" );
        ConfigTools.setSet( cfg, "key1", st );

        // Check it comes out unchanged
        assertThat(
            ConfigTools.getSet( cfg, "key1", String.class ),
            equalTo( st )
        );

        // Perhaps overkill: assert storage format
        assertThat(
            cfg.get( "key1" ),
            equalTo( "[\"DFG\",\"aaa\",\"xyz\"]" )
        );
    }

    @Test
    public void Can_get_empty_set()
    {
        // Make a config with default map with 1 key
        ConfigSchema def = new ConfigSchema();
        def.set( "key1", "[]", "desc1" );
        Config cfg = new Config( def, new MemoryConfigStorage() );

        // We get an empty map of the type we ask for
        assertThat(
            ConfigTools.getSet( cfg, "key1", String.class ),
            equalTo( (Set<String>)new HashSet<String>() )
        );

        // We get an empty map of the type we ask for
        assertThat(
            ConfigTools.getSet( cfg, "key1", Integer.class ),
            equalTo( (Set<Integer>)new HashSet<Integer>() )
        );
    }

    @Test
    public void Can_get_and_set_sets_of_int()
    {
        // Make a config with default map with 1 key
        ConfigSchema def = new ConfigSchema();
        def.set( "key1", "[1,5,7]", "desc1" );
        Config cfg = new Config( def, new MemoryConfigStorage() );

        // Get the map value out
        assertThat(
            ConfigTools.getSet( cfg, "key1", Integer.class ),
            equalTo( newSet( 1, 5, 7 ) )
        );

        // Set a different value
        Set<Integer> st = newSet( 45, 56, 0 );
        ConfigTools.setSet( cfg, "key1", st );

        // Check it comes out unchanged
        assertThat(
            ConfigTools.getSet( cfg, "key1", Integer.class ),
            equalTo( st )
        );

        // Perhaps overkill: assert storage format
        assertThat(
            cfg.get( "key1" ),
            equalTo( "[0,45,56]" )
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

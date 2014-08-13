package rabbitescape.engine;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.IsEqual.*;
import static org.junit.Assert.fail;
import static rabbitescape.engine.util.Util.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

public class TestUtil
{
    @Test
    public void Assert_does_not_throw_when_true()
    {
        reAssert( true );
    }

    @Test( expected = AssertionError.class )
    public void Assert_throws_when_false()
    {
        reAssert( false );
    }

    @Test
    public void Join_returns_empty_for_no_items()
    {
        assertThat( join( "foo", new String[] {} ), equalTo( "" ) );
    }

    @Test
    public void Join_returns_item_for_1_item()
    {
        assertThat( join( "foo", new String[] { "xy" } ), equalTo( "xy" ) );
    }

    @Test
    public void Join_returns_concated_when_glue_is_empty()
    {
        assertThat( join( "", new String[] { "x", "y" } ), equalTo( "xy" ) );
    }

    @Test
    public void Join_sticks_items_together_with_glue()
    {
        assertThat(
            join( "::", new String[] { "x", "yz", "a" } ),
            equalTo( "x::yz::a" )
        );
    }

    @Test
    public void Empty_iterable_builds_into_empty_list()
    {
        Iterable<String> input = Arrays.asList( new String[] {} );

        assertThat(
            stringArray( list( input ) ),
            equalTo( new String[] {} )
        );
    }

    @Test
    public void Build_list_from_iterable_with_list()
    {
        Iterable<String> input = Arrays.asList( "a", "c", "b" );

        assertThat(
            stringArray( list( input ) ),
            equalTo( new String[] { "a", "c", "b" } )
        );
    }

    @Test
    public void Build_list_from_array_with_list()
    {
        String[] input = new String[] { "a", "c", "b" };

        assertThat(
            stringArray( list( input ) ),
            equalTo( new String[] { "a", "c", "b" } )
        );
    }

    @Test
    public void Get_the_first_item_with_getNth()
    {
        assertThat(
            getNth( Arrays.asList( "a", "b", "c" ), 0 ),
            equalTo( "a" )
        );
    }

    @Test
    public void Get_the_last_item_with_getNth()
    {
        assertThat(
            getNth( Arrays.asList( "a", "b", "c" ), 2 ),
            equalTo( "c" )
        );
    }

    @Test
    public void Get_any_item_with_getNth()
    {
        assertThat(
            getNth( Arrays.asList( "a", "b", "c" ), 1 ),
            equalTo( "b" )
        );
    }

    @Test( expected = ArrayIndexOutOfBoundsException.class )
    public void Negative_n_for_getNth_is_an_error()
    {
        getNth( Arrays.asList( "a", "b", "c" ), -1 );
    }

    @Test( expected = ArrayIndexOutOfBoundsException.class )
    public void Past_end_of_list_for_getNth_is_an_error()
    {
        getNth( Arrays.asList( "a", "b", "c" ), 3 );
    }

    @Test
    public void Iterate_through_string_with_asChars()
    {
        assertThat(
            characterArray( list( asChars( "acb" ) ) ),
            equalTo( new Character[] { 'a', 'c', 'b' } )
        );
    }

    @Test
    public void Iterate_through_empty_string_with_asChars()
    {
        assertThat(
            characterArray( list( asChars( "" ) ) ),
            equalTo( new Character[] {} )
        );
    }

    @Test
    public void Build_empty_string_from_chars()
    {
        assertThat(
            stringFromChars( list( new Character[] {} ) ),
            equalTo( "" )
        );
    }


    @Test
    public void Build_a_string_from_chars()
    {
        assertThat(
            stringFromChars( list( new Character[] { 'x', 'y', 'z' } ) ),
            equalTo( "xyz" )
        );
    }

    @Test
    public void Empty_range_does_not_enter_loop()
    {
        for( int i : range( 0 ) )
        {
            fail( "Should not get here " + i );
        }
    }

    @Test
    public void Range_provides_consecutive_integer_values()
    {
        List<Integer> result = new ArrayList<>();

        for( int i : range( 7 ) )
        {
            result.add( i );
        }

        assertThat(
            integerArray( result ),
            equalTo( new Integer[] { 0, 1, 2, 3, 4, 5, 6 } )
        );
    }

    @Test
    public void Chain_deals_with_initial_empty_list()
    {
        List<Integer> list1 = Arrays.asList();
        List<Integer> list2 = Arrays.asList( 6, 7, 8 );

        List<Integer> result = new ArrayList<>();

        for( int i : chain( list1, list2 ) )
        {
            result.add( i );
        }

        assertThat(
            integerArray( result ),
            equalTo( new Integer[] { 6, 7, 8 } )
        );
    }

    @Test
    public void Chain_deals_with_second_list_empty()
    {
        List<Integer> list1 = Arrays.asList( 5, 4, 3 );
        List<Integer> list2 = Arrays.asList();

        List<Integer> result = new ArrayList<>();

        for( int i : chain( list1, list2 ) )
        {
            result.add( i );
        }

        assertThat(
            integerArray( result ),
            equalTo( new Integer[] { 5, 4, 3 } )
        );
    }

    @Test
    public void Chain_deals_with_both_lists_empty()
    {
        List<Integer> list1 = Arrays.asList( new Integer[] {} );
        List<Integer> list2 = Arrays.asList( new Integer[] {} );

        List<Integer> result = new ArrayList<>();

        for( int i : chain( list1, list2 ) )
        {
            result.add( i );
        }

        assertThat(
            integerArray( result ),
            equalTo( new Integer[] {} )
        );
    }

    @Test
    public void Chain_concatenates_two_iterables()
    {
        List<Integer> list1 = Arrays.asList( 5, 4, 3 );
        List<Integer> list2 = Arrays.asList( 6, 7, 8 );

        List<Integer> result = new ArrayList<>();

        for( int i : chain( list1, list2 ) )
        {
            result.add( i );
        }

        assertThat(
            integerArray( result ),
            equalTo( new Integer[] { 5, 4, 3, 6, 7, 8 } )
        );
    }

    @Test
    public void Create_empty_map_with_newMap()
    {
        assertThat(
            newMap(),
            equalTo( (Map<String, Object>)new HashMap<String, Object>() )
        );
    }

    @Test
    public void Create_full_map_with_newMap()
    {
        Map<String, Object> expected = new TreeMap<>();
        expected.put( "x", "y" );
        expected.put( "z", "a" );

        assertThat(
            newMap( "x", "y", "z", "a" ),
            equalTo( expected )
        );
    }

    @Test( expected = AssertionError.class )
    public void Odd_number_of_args_to_newMap_is_an_error()
    {
        newMap( "x", "y", "z" );
    }

    Function<Integer, String> stringise()
    {
        return new Function<Integer, String>()
        {
            @Override
            public String apply( Integer t )
            {
                return t.toString();
            }
        };
    }

    @Test
    public void Apply_a_function_to_each_element_with_map()
    {
        assertThat(
            list( map( stringise(), list( new Integer[] { 3, -4, 5 } ) ) ),
            equalTo( list( new String[] { "3", "-4", "5" } ) )
        );
    }

    @Test
    public void Sorted_empty_list_is_empty()
    {
        assertThat(
            list( sorted( Arrays.asList() ) ),
            equalTo( Arrays.asList() )
        );
    }

    @Test
    public void Sorted_version_of_a_list()
    {
        assertThat(
            list( sorted( Arrays.asList( 5, 3, 4 ) ) ),
            equalTo( Arrays.asList( 3, 4, 5 ) )
        );
    }
}

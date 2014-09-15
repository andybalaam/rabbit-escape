package rabbitescape.engine;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.fail;

import static rabbitescape.engine.util.Util.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    public void Split_an_empty_string_gives_single_item_list()
    {
        assertThat( split( "", "x" ), equalTo( new String[] { "" } ) );
    }

    @Test
    public void Split_returns_just_the_string_if_no_delim_found()
    {
        assertThat( split( "yy", "x" ), equalTo( new String[] { "yy" } ) );
    }

    @Test
    public void Split_breaks_on_a_single_char_delim()
    {
        assertThat(
            split( "abc\ndef", "\n" ),
            equalTo( new String[] { "abc", "def" } )
        );
    }

    @Test
    public void Split_breaks_on_a_multiple_char_delim()
    {
        assertThat(
            split( "ab|c|||d|ef", "|||" ),
            equalTo( new String[] { "ab|c", "d|ef" } )
        );
    }

    @Test
    public void Split_breaks_at_beginning()
    {
        assertThat(
            split( "||a||b", "||" ),
            equalTo( new String[] { "", "a", "b" } )
        );
    }

    @Test
    public void Split_breaks_at_end()
    {
        assertThat(
            split( "a||b||", "||" ),
            equalTo( new String[] { "a", "b", "" } )
        );
    }

    @Test
    public void Split_breaks_consecutive_delimiters_into_empty_string_lists()
    {
        assertThat(
            split( "a|||b|", "|" ),
            equalTo( new String[] { "a", "", "", "b", "" } )
        );
    }

    @Test
    public void Split_breaks_consecutive_multi_delimiters_to_empty_strings()
    {
        assertThat(
            split( "a||||||||||||b||||", "||||" ),
            equalTo( new String[] { "a", "", "", "b", "" } )
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
    public void Build_arrays_from_lists()
    {
        assertThat(
            stringArray( Arrays.asList( new String[] { "d", "e" } ) ),
            equalTo( new String[] { "d", "e" } )
        );

        assertThat(
            characterArray( Arrays.asList( new Character[] { 'd', 'e' } ) ),
            equalTo( new Character[] { 'd', 'e' } )
        );

        assertThat(
            integerArray( Arrays.asList( new Integer[] { 4, 5 } ) ),
            equalTo( new Integer[] { 4, 5 } )
        );
    }

    @Test
    public void Build_arrays_from_iterables_lists()
    {
        assertThat(
            stringArray( (Iterable<String>)Arrays.asList( new String[] { "d", "e" } ) ),
            equalTo( new String[] { "d", "e" } )
        );

        assertThat(
            characterArray( (Iterable<Character>)Arrays.asList( new Character[] { 'd', 'e' } ) ),
            equalTo( new Character[] { 'd', 'e' } )
        );

        assertThat(
            integerArray( (Iterable<Integer>)Arrays.asList( new Integer[] { 4, 5 } ) ),
            equalTo( new Integer[] { 4, 5 } )
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
            stringFromChars( Arrays.asList( 'x', 'y', 'z' ) ),
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
        List<Integer> list1 = list( new Integer[] {} );
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
        List<Integer> list2 = list( new Integer[] {} );

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
        List<Integer> list1 = list( new Integer[] {} );
        List<Integer> list2 = list( new Integer[] {} );

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
    public void Filter_deals_with_empty_list()
    {
        assertThat(
            list( filter( endsWith( "x" ), new ArrayList<String>() ) ),
            equalTo( (List<String>)new ArrayList<String>() )
        );
    }

    @Test
    public void Filter_removes_nonmatching_items_even_at_beginning()
    {
        List<String> inp = Arrays.asList( "ax", "bx", "ay", "bx", "x", "by" );

        assertThat(
            list( filter( endsWith( "y" ), inp ) ),
            equalTo( Arrays.asList( "ay", "by" ) )
        );
    }

    @Test
    public void Filter_removes_nonmatching_items_even_at_end()
    {
        List<String> inp = Arrays.asList( "ay", "bx", "x", "by", "ax", "bx" );

        assertThat(
            list( filter( endsWith( "y" ), inp ) ),
            equalTo( Arrays.asList( "ay", "by" ) )
        );
    }

    @Test
    public void Striplast_0_chars()
    {
        assertThat(
            stripLast( 0 ).apply( "987654321" ),
            equalTo( "987654321" )
        );
    }

    @Test
    public void Striplast_4_chars()
    {
        assertThat(
            stripLast( 4 ).apply( "987654321" ),
            equalTo( "98765" )
        );
    }

    @Test
    public void Striplast_more_than_there_are_gives_empty_string()
    {
        assertThat(
            stripLast( 20 ).apply( "987654321" ),
            equalTo( "" )
        );
    }

    @Test( expected = StringIndexOutOfBoundsException.class )
    public void Striplast_negative_is_an_error()
    {
        stripLast( -2 ).apply( "987654321" );
    }

    @Test
    public void Endswith_empty_string_is_true()
    {
        assertThat(
            endsWith( "" ).apply( "987654321" ),
            equalTo( true )
        );
    }

    @Test
    public void Endswith_suffix_is_true()
    {
        assertThat(
            endsWith( "21" ).apply( "987654321" ),
            equalTo( true )
        );
    }

    @Test
    public void Endswith_nonsuffix_is_false()
    {
        assertThat(
            endsWith( "AA" ).apply( "987654321" ),
            equalTo( false )
        );
    }

    @Test
    public void Endswith_longer_suffix_than_string_is_false()
    {
        assertThat(
            endsWith( "A987654321" ).apply( "987654321" ),
            equalTo( false )
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
            list( map( stringise(), Arrays.asList( 3, -4, 5 ) ) ),
            equalTo( Arrays.asList( "3", "-4", "5" ) )
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

    @Test
    public void Read_the_lines_in_a_stream() throws Exception
    {
        byte[] contents = "foo\nbar\nbaz\n".getBytes( "UTF8" );

        assertThat(
            list( streamLines( new ByteArrayInputStream( contents ) ) ),
            equalTo( Arrays.asList( "foo", "bar", "baz" ) )
        );
    }

    @Test( expected = ReadingResourceFailed.class )
    public void Reading_a_throwing_stream_is_an_error()
    {
        InputStream throwing = new InputStream()
        {
            @Override
            public int read() throws IOException
            {
                throw new IOException();
            }
        };

        streamLines( throwing ).iterator().hasNext();
    }

    @Test
    public void Read_the_lines_in_a_resource()
    {
        assertThat(
            list( resourceLines( "/rabbitescape/engine/testresource.rel" ) ),
            equalTo( Arrays.asList( "x", "y", "z" ) )
        );
    }

    @Test( expected = MissingResource.class )
    public void Reading_a_nonexistent_resource_is_an_error()
    {
        resourceLines( "/rabbitescape/engine/NONEXISTENT.rel" );
    }
}

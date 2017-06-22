package rabbitescape.engine.util;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.fail;

import static rabbitescape.engine.util.Util.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Test;

import rabbitescape.engine.Direction;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.Thing;
import rabbitescape.engine.Token;
import rabbitescape.engine.util.Util;

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
    public void Join_sticks_nonstring_items_together_with_glue()
    {
        assertThat(
            join( "::", list( new Integer[] { 3, 4, 5 } ) ),
            equalTo( "3::4::5" )
        );
    }

    @Test
    public void Join_array_version_works()
    {
        assertThat(
            join( "<>", new Boolean[] { true, true, false } ),
            equalTo( "true<>true<>false" )
        );
    }

    @Test
    public void Split_an_empty_string_gives_single_item_list()
    {
        assertThat( split( "", "x" ), equalTo( new String[] { "" } ) );
    }

    @Test
    public void Splitn_an_empty_string_gives_single_item_list()
    {
        assertThat( split( "", "x", 2 ), equalTo( new String[] { "" } ) );
    }

    @Test
    public void Split_returns_just_the_string_if_no_delim_found()
    {
        assertThat( split( "yy", "x" ), equalTo( new String[] { "yy" } ) );
    }

    @Test
    public void Splitn_returns_just_the_string_if_no_delim_found()
    {
        assertThat( split( "yy", "x", 1 ), equalTo( new String[] { "yy" } ) );
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
    public void Splitn_breaks_on_a_single_char_delim()
    {
        assertThat(
            split( "abc\ndef", "\n", 1 ),
            equalTo( new String[] { "abc", "def" } )
        );
    }

    @Test
    public void Split0_refuses_to_break()
    {
        assertThat(
            split( "abc\ndef", "\n", 0 ),
            equalTo( new String[] { "abc\ndef" } )
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
    public void Splitn_breaks_on_a_multiple_char_delim()
    {
        assertThat(
            split( "ab|c|||d|ef", "|||", 3 ),
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
    public void Splitn_breaks_at_beginning()
    {
        assertThat(
            split( "||a||b", "||", 2 ),
            equalTo( new String[] { "", "a", "b" } )
        );
    }

    @Test
    public void Split1_breaks_only_once()
    {
        assertThat(
            split( "||a||b", "||", 1 ),
            equalTo( new String[] { "", "a||b" } )
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
    public void Splitn_breaks_at_end()
    {
        assertThat(
            split( "a||b||", "||", 2 ),
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
    public void Splitn_breaks_consecutive_delimiters_into_empty_string_lists()
    {
        assertThat(
            split( "a|||b|", "|", 4 ),
            equalTo( new String[] { "a", "", "", "b", "" } )
        );
    }

    @Test
    public void Split3_breaks_only_three_times()
    {
        assertThat(
            split( "a|||b|c", "|", 3 ),
            equalTo( new String[] { "a", "", "", "b|c" } )
        );
    }

    @Test
    public void Split3_breaks_only_three_times_even_at_end()
    {
        assertThat(
            split( "a|||b|", "|", 3 ),
            equalTo( new String[] { "a", "", "", "b|" } )
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
    public void Splitminus1_acts_like_split_with_no_n()
    {
        assertThat(
            split( "a||||||||||||b||||", "||||", -1 ),
            equalTo( new String[] { "a", "", "", "b", "" } )
        );
    }

    @Test
    public void Splitn_breaks_consecutive_multi_delimiters_to_empty_strings()
    {
        assertThat(
            split( "a||||||||||||b||||", "||||", 7 ),
            equalTo( new String[] { "a", "", "", "b", "" } )
        );
    }

    @Test
    public void Splitn_stops_breaking_consecutive_delimiters()
    {
        assertThat(
            split( "a||||||||||||b||||", "||", 2 ),
            equalTo( new String[] { "a", "", "||||||||b||||" } )
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
    public void Empty_iterator_builds_into_empty_list()
    {
        Iterable<String> input = Arrays.asList( new String[] {} );

        assertThat(
            stringArray( list( input.iterator() ) ),
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
    public void Build_list_from_iterator_with_list()
    {
        Iterable<String> input = Arrays.asList( "a", "c", "b" );

        assertThat(
            stringArray( list( input.iterator() ) ),
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

    @Test
    public void Create_empty_set_with_newSet()
    {
        assertThat(
            newSet(),
            equalTo( (Set<Object>)new HashSet<Object>() )
        );
    }

    @Test
    public void Create_full_set_with_newSet()
    {
        Set<String> expected = new TreeSet<>();
        expected.add( "x" );
        expected.add( "z" );

        assertThat(
            newSet( "x", "z" ),
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
    public void Apply_a_function_to_each_array_element_with_map()
    {
        assertThat(
            map( stringise(), new Integer[]{ 3, -4, 5 }, new String[0] ),
            equalTo( new String[] { "3", "-4", "5" } )
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

    @Test
    public void Concat_empty_arrays_gives_empty()
    {
        assertThat(
            concat( new Integer[0], new Integer[0] ),
            equalTo( new Integer[0] )
        );
    }

    @Test
    public void Concat_empty_onto_something_gives_something()
    {
        assertThat(
            concat( new Double[0], new Double[] { 3.0, 4.0 } ),
            equalTo( new Double[] { 3.0, 4.0 } )
        );

        assertThat(
            concat( new String[] { "1", "2" }, new String[0] ),
            equalTo( new String[] { "1", "2" } )
        );
    }

    @Test
    public void Concat_two_arrays_gives_their_contents_joined()
    {
        assertThat(
            concat( new String[] { "4" }, new String[] { "3", "2", "1" } ),
            equalTo( new String[] { "4", "3", "2", "1" } )
        );
    }

    @Test
    public void Equal_objects_are_equal()
    {
        assertThat(
            equalsOrBothNull( "foo", "foo" ),
            equalTo( true )
        );
    }

    @Test
    public void Unequal_objects_are_unequal()
    {
        assertThat(
            equalsOrBothNull( "foo", "bar" ),
            equalTo( false )
        );
    }

    @Test
    public void Null_does_not_equal_an_object()
    {
        assertThat(
            equalsOrBothNull( "foo", null ),
            equalTo( false )
        );

        assertThat(
            equalsOrBothNull( null, "bar" ),
            equalTo( false )
        );
    }

    @Test
    public void Two_nulls_are_equal()
    {
        assertThat(
            equalsOrBothNull( null, null ),
            equalTo( true )
        );
    }

    @Test
    public void Empty_array_converts_to_empty_string_list()
    {
        assertThat(
            toStringList( new String[] {} ),
            equalTo( (List<String>)new ArrayList<String>() )
        );
    }

    private static enum Foo { A, bc, d };

    @Test
    public void Can_convert_enum_to_string_list()
    {
        assertThat(
            toStringList( Foo.values() ),
            equalTo( list( new String[] { "A", "bc", "d" } ) )
        );
    }

    @Test
    public void Identical_IdxObjs_are_equal()
    {
        IdxObj<String> p1 = IdxObj.make( 3, "x" );
        IdxObj<String> p2 = IdxObj.make( 3, "x" );

        assertThat( p1, equalTo( p2 ) );
        assertThat( p1.hashCode(), equalTo( p2.hashCode() ) );
    }

    @Test
    public void Different_IdxObjs_are_unequal()
    {
        IdxObj<String> p1 = IdxObj.make( 3, "x" );
        IdxObj<String> p2 = IdxObj.make( 5, "x" );
        IdxObj<String> p3 = IdxObj.make( 3, "y" );

        assertThat( p1, not( equalTo( p2 ) ) );
        assertThat( p1, not( equalTo( p3 ) ) );

        // Doesn't strictly have to be true, but would be nice
        assertThat( p1.hashCode(), not( equalTo( p2.hashCode() ) ) );
        assertThat( p1.hashCode(), not( equalTo( p3.hashCode() ) ) );
    }

    @Test
    public void Enumerating1_an_empty_list_gives_empty_list()
    {
        assertThat(
            list( enumerate1( new ArrayList<String>() ) ),
            equalTo(
                (List<IdxObj<String>>)new ArrayList<IdxObj<String>>() )
        );
    }

    @Test
    public void Enumerating1_a_list_gives_each_item_with_numbers()
    {
        assertThat(
            list( enumerate1( list( new String[] { "x", "y", "z" } ) ) ),
            equalTo(
                Arrays.asList(
                      IdxObj.make( 1, "x" )
                    , IdxObj.make( 2, "y" )
                    , IdxObj.make( 3, "z" )
                )
            )
        );
    }

    @Test
    public void Enumerating1_an_array_gives_each_item_with_numbers()
    {
        assertThat(
            list( enumerate1( new String[] { "x", "y", "z" } ) ),
            equalTo(
                Arrays.asList(
                      IdxObj.make( 1, "x" )
                    , IdxObj.make( 2, "y" )
                    , IdxObj.make( 3, "z" )
                )
            )
        );
    }


    @Test
    public void Enumerating_an_empty_list_gives_empty_list()
    {
        assertThat(
            list( enumerate( new ArrayList<String>() ) ),
            equalTo(
                (List<IdxObj<String>>)new ArrayList<IdxObj<String>>() )
        );
    }

    @Test
    public void Enumerating_a_list_gives_each_item_with_numbers()
    {
        assertThat(
            list( enumerate( list( new String[] { "x", "y", "z" } ) ) ),
            equalTo(
                Arrays.asList(
                      IdxObj.make( 0, "x" )
                    , IdxObj.make( 1, "y" )
                    , IdxObj.make( 2, "z" )
                )
            )
        );
    }

    @Test
    public void Enumerating_an_array_gives_each_item_with_numbers()
    {
        assertThat(
            list( enumerate( new String[] { "x", "y", "z" } ) ),
            equalTo(
                Arrays.asList(
                      IdxObj.make( 0, "x" )
                    , IdxObj.make( 1, "y" )
                    , IdxObj.make( 2, "z" )
                )
            )
        );
    }

    @Test
    public void Test_text_line_wrapping()
    {
        String s;
        String[] l;
        // Note that the "...Orion." line is 47 chars, when the the
        // trailing space is stripped.
        //            1         2         3         4      |
        //   1234567890123456789012345678901234567890123456789
        s = "I've seen things you people wouldn't believe. " +
            "Attack ships on fire off the shoulder of Orion. "+
            "I watched C-beams glitter in the dark near the " +
            "Tannhauser Gate. All those moments will be lost " +
            "in time, like tears...in...rain. Time to die";
        l = new String[] {
            "I've seen things you people wouldn't believe.",
            "Attack ships on fire off the shoulder of Orion.",
            "I watched C-beams glitter in the dark near the",
            "Tannhauser Gate. All those moments will be lost",
            "in time, like tears...in...rain. Time to die"
        };
          assertThat( Util.wrap( s, 47 ), equalTo(l));

        s = "";
        l = new String[] { "" };
        assertThat( Util.wrap( s, 5 ), equalTo(l));

        //            1         2         3         4      |
        //   1234567890123456789012345678901234567890123456789
        s = "It's " +
            "supercalifragilisticexpialidocious " +
            "Even " +
            "though the " +
            "sound of " +
            "it is " +
            "something " +
            "quite " +
            "atrocious.";
        l = new String[] {
            "It's",
            "supercalifragilisticexpialidocious",
            "Even",
            "though the",
            "sound of",
            "it is",
            "something",
            "quite",
            "atrocious."
        };
        // When a word is longer than the requested line, it does not split it.
        assertThat( Util.wrap( s, 10 ), equalTo(l));
    }

    @Test
    public void Chaining_3_nonempty_lists()
    {
        String[] ab = new String[] {"a", "b"};
        String[] cd = new String[] {"c", "d"};
        String[] ef = new String[] {"e", "f"};

        Iterable<String> chained = Util.chain( Arrays.asList( ab ),
                                               Arrays.asList( cd ),
                                               Arrays.asList( ef ) );
        assertThat( Util.list( chained ).toArray( new String[] {} ),
            equalTo( new String[] {"a", "b", "c", "d", "e", "f" } ) );
    }

    @Test
    public void Chaining_with_some_empty_lists()
    {
        String[] a = new String[] {"a"};
        String[] _bcd = new String[] {};
        String[] efg = new String[] {"e", "f", "g"};
        String[] _h = new String[] {};

        Iterable<String> chained = Util.chain( Arrays.asList( a ),
                                               Arrays.asList( _bcd ),
                                               Arrays.asList( efg ),
                                               Arrays.asList( _h ) );
        assertThat( Util.list( chained ).toArray( new String[] {} ),
            equalTo( new String[] {"a", "e", "f", "g" } ) );
    }

    @Test
    public void Chaining_a_single_empty_list()
    {

        String[] empty = new String[] {};

        Iterable<String> chained = Util.chain( Arrays.asList( empty ) );
        assertThat( Util.list( chained ).toArray( new String[] {} ),
            equalTo( empty ) );
    }

    @Test
    public void Chaining_different_classes_yields_common_superclass()
    {
        Token[] tokens = new Token[]
        {
            new Token( 0, 0, Token.Type.bash ),
            new Token( 1, 1, Token.Type.bridge )
        };

        Rabbit[] rabbits = new Rabbit[]
            { new Rabbit( 3, 3, Direction.LEFT, Rabbit.Type.RABBIT ) };

        Iterable<Thing> chained = Util.chain( Arrays.asList( tokens ), Arrays.asList( rabbits ) );

        String s = "";

        for ( Thing t: chained ) {
            s = s + t.x;
        }

        assertThat( s, equalTo( "013" ) );
    }

    @Test
    public void Concatenating_2_arrays_returns_all_elements_in_order()
    {
        String[] a = new String[] {"a"};
        String[] efg = new String[] {"e", "f", "g"};

        assertThat(
            Util.concat( a, efg ),
            equalTo( new String[] {"a", "e", "f", "g" } )
        );
    }

    @Test
    public void Concatenating_3_arrays_returns_all_elements_in_order()
    {
        String[] a = new String[] {"a"};
        String[] _bcd = new String[] {};
        String[] efg = new String[] {"e", "f", "g"};

        assertThat(
            Util.concat( a, _bcd, efg ),
            equalTo( new String[] {"a", "e", "f", "g" } )
        );
    }

    @Test
    public void Concatenating_4_arrays_returns_all_elements_in_order()
    {
        String[] a = new String[] {"a"};
        String[] _bcd = new String[] {};
        String[] efg = new String[] {"e", "f", "g"};
        String[] hij = new String[] {"h", "i", "j"};

        assertThat(
            Util.concat( a, _bcd, efg, hij ),
            equalTo( new String[] {"a", "e", "f", "g", "h", "i", "j" } )
        );
    }

    @Test
    public void Can_get_property_names_from_properties()
    {
        Properties props = new Properties();
        props.setProperty( "a", "av" );
        props.setProperty( "bb", "bbv" );

        assertThat(
            list(sorted(stringPropertyNames(props))),
            equalTo( Arrays.asList( "a", "bb" ) )
        );
    }
}

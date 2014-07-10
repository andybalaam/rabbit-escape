package rabbitescape;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.IsEqual.*;
import static rabbitescape.util.Util.*;

import java.util.Arrays;

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
            list( input ).toArray( new String[] {} ),
            equalTo( new String[] {} )
        );
    }

    @Test
    public void Build_list_from_iterable_with_list()
    {
        Iterable<String> input = Arrays.asList(
            new String[] { "a", "c", "b" } );

        assertThat(
            list( input ).toArray( new String[] {} ),
            equalTo( new String[] { "a", "c", "b" } )
        );
    }

    @Test
    public void Get_the_first_item_with_getNth()
    {
        assertThat(
            getNth( Arrays.asList( new String[] { "a", "b", "c" } ), 0 ),
            equalTo( "a" )
        );
    }

    @Test
    public void Get_the_last_item_with_getNth()
    {
        assertThat(
            getNth( Arrays.asList( new String[] { "a", "b", "c" } ), 2 ),
            equalTo( "c" )
        );
    }

    @Test
    public void Get_any_item_with_getNth()
    {
        assertThat(
            getNth( Arrays.asList( new String[] { "a", "b", "c" } ), 1 ),
            equalTo( "b" )
        );
    }

    @Test( expected = ArrayIndexOutOfBoundsException.class )
    public void Negative_n_for_getNth_is_an_error()
    {
        getNth( Arrays.asList( new String[] { "a", "b", "c" } ), -1 );
    }

    @Test( expected = ArrayIndexOutOfBoundsException.class )
    public void Past_end_of_list_for_getNth_is_an_error()
    {
        getNth( Arrays.asList( new String[] { "a", "b", "c" } ), 3 );
    }

    @Test
    public void Iterate_through_string_with_asChars()
    {
        assertThat(
            list( asChars( "acb" ) ).toArray( new Character[] {} ),
            equalTo( new Character[] { 'a', 'c', 'b' } )
        );
    }

    @Test
    public void Iterate_through_empty_string_with_asChars()
    {
        assertThat(
            list( asChars( "" ) ).toArray( new Character[] {} ),
            equalTo( new Character[] {} )
        );
    }
}

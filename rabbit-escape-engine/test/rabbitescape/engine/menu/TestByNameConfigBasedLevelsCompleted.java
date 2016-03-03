package rabbitescape.engine.menu;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

import static rabbitescape.engine.menu.ByNameConfigBasedLevelsCompleted.canonicalName;

public class TestByNameConfigBasedLevelsCompleted
{
    @Test
    public void Canonical_name_of_simple_string_is_the_string()
    {
        assertThat( "abcxyz", isEqualToItsCanonicalVersion() );
        assertThat( "c",      isEqualToItsCanonicalVersion() );
        assertThat( "zya",    isEqualToItsCanonicalVersion() );
    }

    @Test( expected = ByNameConfigBasedLevelsCompleted.EmptyLevelName.class )
    public void Empty_name_is_not_allowed()
    {
        canonicalName( "" );
    }

    @Test
    public void Spaces_become_underscores()
    {
        assertThat( canonicalName( "abc xyz" ), equalTo( "abc_xyz" ) );
        assertThat( canonicalName( " a " ), equalTo( "_a_" ) );
    }

    @Test
    public void Punctuation_becomes_underscores()
    {
        assertThat( canonicalName( "abc,xyz?" ), equalTo( "abc_xyz_" ) );
    }

    @Test
    public void Upper_case_becomes_lower_case()
    {
        assertThat( canonicalName( "AbcxYZ" ), equalTo( "abcxyz" ) );
    }

    @Test
    public void Unicode_becomes_underscore()
    {
        assertThat(
            canonicalName( "Pile of poo \uD83D\uDCA9 is coo" ),
            equalTo( "pile_of_poo___is_coo" )
        );

        assertThat(
            canonicalName( "Go to \u5317\u4eac\u5e02" ),
            equalTo( "go_to____" )
        );
    }

    // ---

    private static Matcher<String> isEqualToItsCanonicalVersion()
    {
        return new BaseMatcher<String>()
        {
            String str;

            @Override
            public boolean matches( Object obj )
            {
                if ( !( obj instanceof String ) )
                {
                    return false;
                }

                str = (String)obj;

                return str.equals( canonicalName( str ) );
            }

            @Override
            public void describeTo( Description description )
            {
                description.appendText( canonicalName( str ) );
            }
        };
    }
}

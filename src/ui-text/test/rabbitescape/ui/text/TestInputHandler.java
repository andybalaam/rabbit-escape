package rabbitescape.ui.text;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

public class TestInputHandler
{
    @Test
    public void Expand_abbreviations()
    {
        String abbrev, expected, expanded;

        abbrev = "";
        expected = "1" ;
        expanded = InputHandler.expandAbbreviations( abbrev );
        assertThat( expanded, equalTo( expected ) );

        abbrev = "i;b;d;k;1,2;c;p";
        expected = "bridge;bash;dig;block;(1,2);climb;explode" ;
        expanded = InputHandler.expandAbbreviations( abbrev );
        assertThat( expanded, equalTo( expected ) );

        abbrev = "in;bridge;di;ok;(1,2)";
        expected = "in;bridge;di;ok;(1,2)" ;
        expanded = InputHandler.expandAbbreviations( abbrev );
        assertThat( expanded, equalTo( expected ) );
    }

}

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
        
    }
    
}

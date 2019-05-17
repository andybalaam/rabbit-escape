package rabbitescape.engine.util;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

public class TestRegexUtil
{

    @Test
    public void Regex_remove_preserve_group()
    {
        String before, regex, after;

        before = "Level: \\\"Tetris\\\"";
        regex  = "\\\\(\")";
        after  = "Level: \"Tetris\"";
        assertThat(
            Util.regexRemovePreserveGroup( before, regex ),
            equalTo( after )
        );

        before = "\\\"Thing in escaped quotes\\\" \"Thing in quotes\"";
        regex  = "\\\\(\")";
        after  = "\"Thing in escaped quotes\" \"Thing in quotes\"";
        assertThat(
            Util.regexRemovePreserveGroup( before, regex ),
            equalTo( after )
        );
    }

    @Test
    public void Regex_replace()
    {
        String before, regex, replacement, after;

        before = "The brown fox.";
        regex  = "brown";
        replacement = "red";
        after  = "The red fox.";
        assertThat(
            Util.regexReplace( before, regex, replacement ),
            equalTo( after )
        );
    }

}

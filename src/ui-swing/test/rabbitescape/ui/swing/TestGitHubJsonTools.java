package rabbitescape.ui.swing;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

public class TestGitHubJsonTools
{

    @Test
    public void Parse_labels()
    {
        String json =
            "{"
                +
                "\"number\":117,"
                +
                "\"title\":\"Bug: window loses width\","
                +
                "\"labels\":["
                +
                "{"
                +
                "\"url\":\"https://api.github.com/repos/andybalaam/rabbit-escape/labels/bug\","
                +
                "\"name\":\"bug\","
                +
                "\"color\":\"fc2929\""
                +
                "}"
                +
                "{"
                +
                "\"url\":\"https://api.github.com/repos/andybalaam/rabbit-escape/labels/bug\","
                +
                "\"name\":\"level\"," +
                "\"color\":\"fc2929\"" +
                "}" +
                "]," +
                "\"state\":\"open\"," +
                "\"locked\":false" +
                "}";
        String[] labels = GitHubJsonTools.getStringValuesFromArrayOfObjects(
            json, "labels.name" );
        assertThat(
            labels,
            equalTo( new String[] { "bug", "level" } ) );
    }

}

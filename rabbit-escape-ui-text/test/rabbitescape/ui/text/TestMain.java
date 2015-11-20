package rabbitescape.ui.text;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Locale;

import org.junit.Test;

import rabbitescape.engine.IgnoreLevelWinListener;
import rabbitescape.engine.util.FakeFileSystem;
import rabbitescape.engine.util.FileSystem;
import rabbitescape.engine.util.NothingExistsFileSystem;

public class TestMain
{
    @Test
    public void File_reading_errors_are_reported()
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FileSystem fs = new NothingExistsFileSystem();

        TextSingleGameEntryPoint main = new TextSingleGameEntryPoint(
            fs, new PrintStream( out ), Locale.ENGLISH );

        int status = main.launchGame(
            new String[] { "file1" }, new IgnoreLevelWinListener() );

        assertThat( status, not( equalTo( 0 ) ) );

        assertThat(
            out.toString(),
            equalTo(
                "File 'file1' does not exist.\n"
                + "Unable to load world file 'file1'.\n"
            )
        );
    }

    @Test
    public void Bad_level_syntax_errors_are_reported()
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String[] badLevel = new String[] { "##", "#" };
        FileSystem fs = new FakeFileSystem( "file1", badLevel );

        TextSingleGameEntryPoint main = new TextSingleGameEntryPoint(
            fs, new PrintStream( out ), Locale.ENGLISH );

        int status = main.launchGame(
            new String[] { "file1" }, new IgnoreLevelWinListener() );

        assertThat( status, not( equalTo( 0 ) ) );

        assertThat(
            out.toString(),
            equalTo(
                "Line number 2 (#) has the wrong length in text world lines:\n"
                + "##\n"
                + "#\n"
                + "Unable to load world file 'file1'.\n"
            )
        );
    }

    @Test
    public void Bad_level_character_syntax_errors_are_reported()
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String[] badLevel = new String[] { "##", "#z" };
        FileSystem fs = new FakeFileSystem( "file1", badLevel );

        TextSingleGameEntryPoint main = new TextSingleGameEntryPoint(
            fs, new PrintStream( out ), Locale.ENGLISH );

        int status = main.launchGame(
            new String[] { "file1" }, new IgnoreLevelWinListener() );

        assertThat( status, not( equalTo( 0 ) ) );

        assertThat(
            out.toString(),
            equalTo(
                "Line number 2 contains an unknown character 'z' "
                + "in text world lines:\n"
                + "##\n"
                + "#z\n"
                + "Unable to load world file 'file1'.\n"
            )
        );
    }
}

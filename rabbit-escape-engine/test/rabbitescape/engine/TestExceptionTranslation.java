package rabbitescape.engine;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.IOException;
import java.util.Locale;

import org.junit.Test;

import rabbitescape.engine.LoadWorldFile.Failed;
import rabbitescape.engine.LoadWorldFile.MissingFile;
import rabbitescape.engine.LoadWorldFile.ReadingFailed;
import rabbitescape.engine.err.RabbitEscapeException;
import rabbitescape.engine.textworld.UnknownCharacter;
import rabbitescape.engine.textworld.WrongLineLength;

public class TestExceptionTranslation
{
    @Test
    public void Translate_exception_into_English()
    {
        assertThat(
            new WrongLineLength(
                new String[] { "x", "y", "zz", "a" }, 2
            ).translate( Locale.ENGLISH ),
            equalTo(
                "Line number 3 (zz) has the wrong length"
                + " in text world lines:\nx\ny\nzz\na"
            )
        );
    }

    @Test
    public void Translate_exception_plus_cause_into_English()
    {
        MissingFile cause = new LoadWorldFile.MissingFile( "myFile" );
        Failed e = new LoadWorldFile.Failed( "myFile", cause );

        assertThat(
            e.translate( Locale.ENGLISH ),
            equalTo(
                "File 'myFile' does not exist.\n"
                + "Unable to load world file 'myFile'."
            )
        );
    }

    @Test
    public void Translate_exception_plus_nonrabbit_cause_into_English()
    {
        ReadingFailed cause = new LoadWorldFile.ReadingFailed(
            "myFile", new IOException( "foo" ) );

        Failed e = new LoadWorldFile.Failed( "myFile", cause );

        assertThat(
            e.translate( Locale.ENGLISH ),
            equalTo(
                "Reading file 'myFile' failed with IOException: 'foo'.\n"
                + "Unable to load world file 'myFile'."
            )
        );
    }

    @Test
    public void Translate_exception_containing_backslash()
    {
        RabbitEscapeException e = new UnknownCharacter(
            new String[] { "#\\" }, 0, 1 );

        assertThat(
            e.translate( Locale.ENGLISH ),
            equalTo(
                "Line number 1 contains an unknown character "
                 + "'\\' in text world lines:\n#\\"
            )
        );
    }

    public static class Undescribed extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;
        public final String fld1 = "val1";
        public final String fld2 = "val2";
    }

    @Test
    public void Translate_undescribed_exception()
    {
        assertThat(
            new Undescribed().translate( Locale.ENGLISH ),
            equalTo(
                "TestExceptionTranslation.Undescribed "
                + "{cause=null, fld1=val1, fld2=val2}"
            )
        );
    }
}

package rabbitescape;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.IsEqual.*;

import java.util.Locale;

import org.junit.Test;

public class TestExceptionTranslation
{
    @Test
    public void Translate_exception_into_English()
    {
        assertThat(
            new TextWorldManip.WrongLineLength(
                new String[] { "x", "y", "zz", "a" }, 2
            ).translate( Locale.ENGLISH ),
            equalTo(
                "Line number 3 (zz) has the wrong length"
                + " in text world lines:\nx\ny\nzz\na\n"
            )
        );
    }
}

package rabbitescape.ui.text;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Locale;

public class Terminal
{
    public final InputStream in;
    public final PrintStream out;
    public final Locale locale;

    public Terminal( InputStream in, PrintStream out, Locale locale )
    {
        this.in = in;
        this.out = out;
        this.locale = locale;
    }
}

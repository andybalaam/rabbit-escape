package rabbitescape.ui.text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Locale;

public class Terminal
{
    public final BufferedReader in;
    public final PrintStream out;
    public final Locale locale;

    public Terminal( InputStream in, PrintStream out, Locale locale )
    {
        this.in = new BufferedReader( new InputStreamReader( in ) );
        this.out = out;
        this.locale = locale;
    }
}

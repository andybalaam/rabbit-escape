package rabbitescape.ui.text;

import static rabbitescape.engine.util.Util.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class FakeTerminal
{
    private final ByteArrayInputStream in;
    public final ByteArrayOutputStream out;
    public final Terminal t;

    public FakeTerminal( String... inputLines )
    {
        this.in = makeIn( inputLines );
        this.out = new ByteArrayOutputStream();

        this.t = new Terminal(
            this.in, new PrintStream( this.out ), Locale.UK );
    }

    private ByteArrayInputStream makeIn( String... inputLines )
    {
        try
        {
            return new ByteArrayInputStream(
                join( "\n", inputLines ).getBytes( "UTF-8" ) );
        }
        catch ( UnsupportedEncodingException e )
        {
            // Should never happen
            throw new RuntimeException( e );
        }
    }

}

package rabbitescape.ui.text;

import rabbitescape.engine.i18n.Translation;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Locale;

public class TextMenuMain
{
    private final TextMenu textMenu;

    public TextMenuMain( InputStream in, PrintStream out )
    {
        this.textMenu = new TextMenu(
            new BufferedReader( new InputStreamReader( in ) ), out );
    }

    public static void main( String[] args )
    {
        Locale locale = Locale.getDefault();
        Translation.init( locale );

        TextMenuMain m = new TextMenuMain( System.in, System.out );

        m.run( args );
    }

    private void run( String[] args )
    {
        textMenu.run();
    }
}

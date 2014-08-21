package rabbitescape.ui.swing;

import java.io.PrintStream;
import java.util.Locale;

import javax.swing.SwingUtilities;

import rabbitescape.engine.i18n.Translation;
import rabbitescape.engine.util.RealFileSystem;

public class SwingMain
{
    private final SwingMenu swingMenu;

    public SwingMain(
        RealFileSystem fs,
        PrintStream out,
        Locale locale
    )
    {
        this.swingMenu = new SwingMenu( fs, out, locale );
    }

    public static void main( String[] args )
    {
        Locale locale = Locale.getDefault();
        Translation.init( locale );

        SwingMain m = new SwingMain(
            new RealFileSystem(),
            System.out,
            locale
        );

        m.run( args );
    }

    private void run( String[] args )
    {
        SwingUtilities.invokeLater( new Runnable()
        {
            @Override
            public void run()
            {
                swingMenu.run();
            }
        } );
    }
}

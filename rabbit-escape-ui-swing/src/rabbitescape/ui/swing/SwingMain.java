package rabbitescape.ui.swing;

import java.io.PrintStream;
import java.util.Locale;

import javax.swing.SwingUtilities;

import rabbitescape.engine.i18n.Translation;
import rabbitescape.engine.util.RealFileSystem;
import rabbitescape.render.BitmapCache;

public class SwingMain
{
    public SwingMain(
        RealFileSystem fs,
        PrintStream out,
        Locale locale
    )
    {
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
        final BitmapCache<SwingBitmap> bitmapCache = new BitmapCache<>(
            new SwingBitmapLoader(), 500 );

        SwingUtilities.invokeLater( new Runnable()
        {
            @Override
            public void run()
            {
                new MenuJFrame( SwingConfigSetup.createConfig(), bitmapCache );
            }
        } );
    }
}

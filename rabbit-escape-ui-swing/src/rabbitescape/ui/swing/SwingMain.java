package rabbitescape.ui.swing;

import java.io.PrintStream;
import java.util.Locale;

import javax.swing.SwingUtilities;

import rabbitescape.engine.config.Config;
import rabbitescape.engine.i18n.Translation;
import rabbitescape.engine.util.RealFileSystem;
import rabbitescape.render.BitmapCache;

public class SwingMain
{
    private final RealFileSystem fs;
    private final PrintStream out;
    private final Locale locale;
    private final BitmapCache<SwingBitmap> bitmapCache;
    private final Config uiConfig;

    public SwingMain(
        RealFileSystem fs,
        PrintStream out,
        Locale locale,
        BitmapCache<SwingBitmap> bitmapCache,
        Config uiConfig
    )
    {
        this.fs = fs;
        this.out = out;
        this.locale = locale;
        this.bitmapCache = bitmapCache;
        this.uiConfig = uiConfig;
    }

    public static void main( String[] args )
    {
        Locale locale = Locale.getDefault();
        Translation.init( locale );

        SwingMain m = new SwingMain(
            new RealFileSystem(),
            System.out,
            locale,
            new BitmapCache<>( new SwingBitmapLoader(), 500 ),
            SwingConfigSetup.createConfig()
        );

        m.run( args );
    }

    public void run( String[] args )
    {
        SwingUtilities.invokeLater( new Runnable()
        {
            @Override
            public void run()
            {
                MainJFrame frame = new MainJFrame();
                new MenuUi(  fs, out, locale, bitmapCache, uiConfig, frame );
            }
        } );
    }
}

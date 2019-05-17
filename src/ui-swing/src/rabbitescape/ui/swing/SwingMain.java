package rabbitescape.ui.swing;

import static rabbitescape.ui.swing.SwingConfigSetup.*;

import java.io.PrintStream;
import java.util.Locale;

import javax.swing.SwingUtilities;

import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.ConfigTools;
import rabbitescape.engine.i18n.Translation;
import rabbitescape.engine.util.RealFileSystem;
import rabbitescape.render.BitmapCache;
import rabbitescape.render.androidlike.Sound;

public class SwingMain
{
    private final RealFileSystem fs;
    private final PrintStream out;
    private final Locale locale;
    private final BitmapCache<SwingBitmap> bitmapCache;
    private final Config uiConfig;
    private final Sound sound;

    public SwingMain(
        RealFileSystem fs,
        PrintStream out,
        Locale locale,
        BitmapCache<SwingBitmap> bitmapCache,
        Config uiConfig,
        Sound sound
    )
    {
        this.fs = fs;
        this.out = out;
        this.locale = locale;
        this.bitmapCache = bitmapCache;
        this.uiConfig = uiConfig;
        this.sound = sound;
    }

    public static void main( String[] args )
    {
        if ( args.length > 0 )
        {
            SwingSingleGameEntryPoint.entryPoint( args );
            return;
        }

        Locale locale = Locale.getDefault();
        Translation.init( locale );
        Config config = SwingConfigSetup.createConfig();

        Sound sound = SwingSound.create(
            ConfigTools.getBool( config, CFG_MUTED ) );

        SwingMain m = new SwingMain(
            new RealFileSystem(),
            System.out,
            locale,
            new BitmapCache<>(
                new SwingBitmapLoader(), new SwingBitmapScaler(), cacheSize() ),
            config,
            sound
        );

        m.run( args );
    }

    public static long cacheSize()
    {
        // About half of the available memory we will give to images
        return Runtime.getRuntime().maxMemory() / 2;
    }

    public void run( String[] args )
    {
        SwingUtilities.invokeLater( new Runnable()
        {
            @Override
            public void run()
            {
                MainJFrame frame = new MainJFrame( uiConfig, sound );
                new MenuUi(
                    fs, out, locale, bitmapCache, uiConfig, frame, sound );
            }
        } );
    }
}

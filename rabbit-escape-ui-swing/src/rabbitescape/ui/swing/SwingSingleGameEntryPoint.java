package rabbitescape.ui.swing;

import static rabbitescape.ui.swing.SwingConfigSetup.*;

import java.io.PrintStream;
import java.util.Locale;

import javax.swing.SwingUtilities;

import rabbitescape.engine.LevelWinListener;
import rabbitescape.engine.World;
import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.ConfigTools;
import rabbitescape.engine.util.FileSystem;
import rabbitescape.engine.util.RealFileSystem;
import rabbitescape.render.BitmapCache;
import rabbitescape.render.GameLaunch;
import rabbitescape.render.SingleGameEntryPoint;

public class SwingSingleGameEntryPoint extends SingleGameEntryPoint
{
    private final BitmapCache<SwingBitmap> bitmapCache;
    private final Config uiConfig;
    private final MainJFrame frame;
    private final SwingSound sound;
    private final MenuUi menuUi;

    public SwingSingleGameEntryPoint(
        FileSystem fs,
        PrintStream out,
        Locale locale,
        BitmapCache<SwingBitmap> bitmapCache,
        Config uiConfig,
        MainJFrame frame,
        SwingSound sound,
        MenuUi menuUi
    )
    {
        super( fs, out, locale );
        this.bitmapCache = bitmapCache;
        this.uiConfig = uiConfig;
        this.frame = frame;
        this.sound = sound;
        this.menuUi = menuUi;
    }

    public static void entryPoint( String[] args )
    {
        Config cfg = SwingConfigSetup.createConfig();

        SwingSound sound = new SwingSound(
            ConfigTools.getBool( cfg, CFG_MUTED ) );

        SingleGameEntryPoint m = new SwingSingleGameEntryPoint(
            new RealFileSystem(),
            System.out,
            Locale.getDefault(),
            new BitmapCache<>(
                new SwingBitmapLoader(), new SwingBitmapScaler(), 500 ),
            cfg,
            new MainJFrame( cfg, sound ),
            sound,
            null
        );

        m.run( args );
    }

    @Override
    public GameLaunch createGameLaunch( World world, LevelWinListener winListener )
    {
        SwingGameInit init = new SwingGameInit(
            bitmapCache, uiConfig, frame, menuUi );

        SwingUtilities.invokeLater( init );

        return new SwingGameLaunch( init, world, winListener, sound );
    }
}

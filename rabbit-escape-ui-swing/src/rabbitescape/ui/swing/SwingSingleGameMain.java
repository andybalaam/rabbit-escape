package rabbitescape.ui.swing;

import java.io.PrintStream;
import java.util.Locale;

import javax.swing.SwingUtilities;

import rabbitescape.engine.LevelWinListener;
import rabbitescape.engine.World;
import rabbitescape.engine.config.Config;
import rabbitescape.engine.util.FileSystem;
import rabbitescape.engine.util.RealFileSystem;
import rabbitescape.render.BitmapCache;
import rabbitescape.render.GameLoop;
import rabbitescape.render.Main;

public class SwingSingleGameMain extends Main
{
    private final BitmapCache<SwingBitmap> bitmapCache;
    private final Config uiConfig;

    public SwingSingleGameMain(
        FileSystem fs,
        PrintStream out,
        Locale locale,
        BitmapCache<SwingBitmap> bitmapCache,
        Config uiConfig
    )
    {
        super( fs, out, locale );
        this.bitmapCache = bitmapCache;
        this.uiConfig = uiConfig;
    }

    public static void main( String[] args )
    {
        Main m = new SwingSingleGameMain(
            new RealFileSystem(),
            System.out,
            Locale.getDefault(),
            new BitmapCache<>( new SwingBitmapLoader(), 500 ),
            SwingConfigSetup.createConfig()
        );

        m.run( args );
    }

    @Override
    public GameLoop createGameLoop( World world, LevelWinListener winListener )
    {
        SwingGameInit init = new SwingGameInit( bitmapCache, uiConfig );

        SwingUtilities.invokeLater( init );

        return new SwingGameLoop( init, world, winListener );
    }
}

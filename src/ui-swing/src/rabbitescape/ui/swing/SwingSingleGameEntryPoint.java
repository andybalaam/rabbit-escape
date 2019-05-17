package rabbitescape.ui.swing;

import static rabbitescape.ui.swing.SwingConfigSetup.*;

import java.io.PrintStream;
import java.util.Locale;

import javax.swing.SwingUtilities;

import rabbitescape.engine.LevelWinListener;
import rabbitescape.engine.World;
import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.ConfigTools;
import rabbitescape.engine.menu.LevelMenuItem;
import rabbitescape.engine.menu.LevelsMenu;
import rabbitescape.engine.util.CommandLineOption;
import rabbitescape.engine.util.CommandLineOptionSet;
import rabbitescape.engine.util.FileSystem;
import rabbitescape.engine.util.RealFileSystem;
import rabbitescape.render.BitmapCache;
import rabbitescape.render.GameLaunch;
import rabbitescape.render.SingleGameEntryPoint;
import rabbitescape.render.androidlike.Sound;

public class SwingSingleGameEntryPoint extends SingleGameEntryPoint
{
    private final BitmapCache<SwingBitmap> bitmapCache;
    private final Config uiConfig;
    private final MainJFrame frame;
    private final Sound sound;
    private final MenuUi menuUi;
    private final String solution;
    private final boolean frameDumping;
    private final LevelMenuItem menuItem;
    private final LevelsMenu menu;

    public SwingSingleGameEntryPoint(
        FileSystem fs,
        PrintStream out,
        Locale locale,
        BitmapCache<SwingBitmap> bitmapCache,
        Config uiConfig,
        MainJFrame frame,
        Sound sound,
        MenuUi menuUi,
        String solution,
        boolean frameDumping,
        LevelMenuItem menuItem,
        LevelsMenu menu
    )
    {
        super( fs, out, locale );
        this.bitmapCache = bitmapCache;
        this.uiConfig = uiConfig;
        this.frame = frame;
        this.sound = sound;
        this.menuUi = menuUi;
        this.solution = solution;
        this.frameDumping = frameDumping;
        this.menuItem = menuItem;
        this.menu = menu;
    }

    public static void entryPoint( String[] args )
    {
        if ( 1 == args.length && args[0].endsWith( ".rel" ) )
        { // Single arg must level file
            go( 
                args, 
                SwingGameLaunch.NOT_DEMO_MODE, 
                false 
            );
            System.exit( 0 );
        }

        CommandLineOption level =
            new CommandLineOption( "--level",        true );
        CommandLineOption solution =
            new CommandLineOption( "--solution",     true );
        CommandLineOption anim =
            new CommandLineOption( "--animation",    false );
        CommandLineOption dump =
            new CommandLineOption( "--dump",         false );

        CommandLineOptionSet.parse( args,
                                    level, solution, anim, dump);
        try
        {
            if ( anim.isPresent() )
            {
                AnimationTester.main( new String[] {} );
                System.exit( 0 );
            }
            if ( solution.isPresent() )
            {
                go( new String[] {level.getValue()},
                    solution.getValue(), dump.isPresent() );
                System.exit( 0 );
            }
            if ( level.isPresent() )
            {
                go( 
                    new String[] {level.getValue()},
                    SwingGameLaunch.NOT_DEMO_MODE, 
                    false 
                );
                System.exit( 0 );
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
            System.exit( 1 );
        }

    }

    private static void go( 
        String[] fileName, 
        String solution,
        boolean frameDumping 
    )
    {

        Config cfg = SwingConfigSetup.createConfig();

        Sound sound = SwingSound.create(
            ConfigTools.getBool( cfg, CFG_MUTED ) );

        SingleGameEntryPoint m = new SwingSingleGameEntryPoint(
            new RealFileSystem(),
            System.out,
            Locale.getDefault(),
            new BitmapCache<>(
                new SwingBitmapLoader(),
                new SwingBitmapScaler(),
                SwingMain.cacheSize()
            ),
            cfg,
            new MainJFrame( cfg, sound ),
            sound,
            null,
            solution,
            frameDumping,
            null,
            null
        );

        m.run( fileName );
    }

    @Override
    public GameLaunch createGameLaunch(
        World world, LevelWinListener winListener )
    {
        SwingGameInit init = new SwingGameInit(
            bitmapCache, uiConfig, frame, menuUi );

        SwingUtilities.invokeLater( init );

        return new SwingGameLaunch( init, world, winListener, sound, uiConfig,
                                    out, solution, frameDumping, menuItem, menu );
    }
}

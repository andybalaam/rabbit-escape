package rabbitescape.ui.swing;

import rabbitescape.engine.config.Config;
import rabbitescape.engine.util.RealFileSystem;
import rabbitescape.render.BitmapCache;

public class SwingGameInit implements Runnable
{
    private static final String CONFIG_PATH =
        "~/.rabbitescape/config/ui.properties"
            .replace( "~", System.getProperty( "user.home" ) );

    public static final String CFG_GAME_WINDOW_LEFT   = "game.window.left";
    public static final String CFG_GAME_WINDOW_TOP    = "game.window.top";
    public static final String CFG_GAME_WINDOW_WIDTH  = "game.window.width";
    public static final String CFG_GAME_WINDOW_HEIGHT = "game.window.height";
    public static final String CFG_MUTED = "muted";

    public class WaitForUi
    {
        private synchronized void notifyUiReady()
        {
            assert whenUiReady != null;
            notify();
        }

        public synchronized WhenUiReady waitForUi()
        {
            while ( whenUiReady == null )
            {
                //noinspection EmptyCatchBlock
                try
                {
                    synchronized( this )
                    {
                        wait();
                    }
                }
                catch ( InterruptedException e )
                {
                }
            }

            return whenUiReady;
        }
    }

    /**
     * Stuff we have made by the time we notify that the UI is ready
     */
    public static class WhenUiReady
    {
        public final GameJFrame jframe;
        public final BitmapCache<SwingBitmap> bitmapCache;

        public WhenUiReady(
            GameJFrame jframe, BitmapCache<SwingBitmap> bitmapCache )
        {
            this.jframe = jframe;
            this.bitmapCache = bitmapCache;
        }
    }

    public final WaitForUi waitForUi = new WaitForUi();

    private WhenUiReady whenUiReady = null;

    @Override
    public void run()
    {
        BitmapCache<SwingBitmap> bitmapCache = new BitmapCache<>(
            new SwingBitmapLoader(), 500 );

        //noinspection UnnecessaryLocalVariable
        GameJFrame jframe = new GameJFrame( createConfig(), bitmapCache );

        // Populate the cache with images in a worker thread?

        this.whenUiReady = new WhenUiReady( jframe, bitmapCache );

        waitForUi.notifyUiReady();
    }

    private Config createConfig()
    {
        Config.Definition definition = new Config.Definition();

        definition.set(
            CFG_GAME_WINDOW_LEFT,
            String.valueOf( Integer.MIN_VALUE ),
            "The x position of the game window on the screen"
        );

        definition.set(
            CFG_GAME_WINDOW_TOP,
            String.valueOf( Integer.MIN_VALUE ),
            "The y position of the game window on the screen"
        );

        definition.set(
            CFG_GAME_WINDOW_WIDTH,
            String.valueOf( Integer.MIN_VALUE ),
            "The width of the game window on the screen"
        );

        definition.set(
            CFG_GAME_WINDOW_HEIGHT,
            String.valueOf( Integer.MIN_VALUE ),
            "The height of the game window on the screen"
        );

        definition.set(
            CFG_MUTED,
            String.valueOf( false ),
            "Disable all sound"
        );

        return new Config( definition, new RealFileSystem(), CONFIG_PATH );
    }
}

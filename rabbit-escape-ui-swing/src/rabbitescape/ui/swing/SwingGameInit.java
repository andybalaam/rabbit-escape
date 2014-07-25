package rabbitescape.ui.swing;

import rabbitescape.engine.config.Config;
import rabbitescape.engine.util.RealFileSystem;

public class SwingGameInit implements Runnable
{
    public static final String CONFIG_PATH =
        "~/.rabbitescape/config/ui.properties"
            .replace( "~", System.getProperty( "user.home" ) );

    public static final String CFG_GAME_WINDOW_LEFT   = "game.window.left";
    public static final String CFG_GAME_WINDOW_TOP    = "game.window.top";
    public static final String CFG_GAME_WINDOW_WIDTH  = "game.window.width";
    public static final String CFG_GAME_WINDOW_HEIGHT = "game.window.height";

    public class WaitForUi
    {
        private synchronized void notifyUiReady()
        {
            assert jframe != null;
            notify();
        }

        public synchronized GameJFrame waitForUi()
        {
            while ( jframe == null )
            {
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

            return jframe;
        }
    }

    public final WaitForUi waitForUi = new WaitForUi();

    private GameJFrame jframe = null;

    @Override
    public void run()
    {
        GameJFrame jframe = new GameJFrame( createConfig() );
        // load all images in a worker thread

        // When ready, put frame or canvas or something into a variable.

        this.jframe = jframe;

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

        return new Config( definition, new RealFileSystem(), CONFIG_PATH );
    }
}

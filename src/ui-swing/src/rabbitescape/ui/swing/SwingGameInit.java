package rabbitescape.ui.swing;

import rabbitescape.engine.config.Config;
import rabbitescape.render.BitmapCache;

public class SwingGameInit implements Runnable
{
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
        public final GameUi jframe;
        public final BitmapCache<SwingBitmap> bitmapCache;

        public WhenUiReady(
            GameUi jframe, BitmapCache<SwingBitmap> bitmapCache )
        {
            this.jframe = jframe;
            this.bitmapCache = bitmapCache;
        }
    }

    public final WaitForUi waitForUi = new WaitForUi();
    private WhenUiReady whenUiReady = null;

    private final BitmapCache<SwingBitmap> bitmapCache;
    private final Config uiConfig;
    public final MainJFrame frame;
    private final MenuUi menuUi;

    public SwingGameInit(
        BitmapCache<SwingBitmap> bitmapCache,
        Config uiConfig,
        MainJFrame frame,
        MenuUi menuUi
    )
    {
        this.bitmapCache = bitmapCache;
        this.uiConfig = uiConfig;
        this.frame = frame;
        this.menuUi = menuUi;
    }

    @Override
    public void run()
    {
        //noinspection UnnecessaryLocalVariable
        GameUi jframe = new GameUi( uiConfig, bitmapCache, frame, menuUi );

        // Populate the cache with images in a worker thread?

        this.whenUiReady = new WhenUiReady( jframe, bitmapCache );

        waitForUi.notifyUiReady();
    }
}

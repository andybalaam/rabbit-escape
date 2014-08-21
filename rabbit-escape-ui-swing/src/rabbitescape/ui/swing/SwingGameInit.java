package rabbitescape.ui.swing;

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
        GameJFrame jframe = new GameJFrame(
            SwingConfigSetup.createConfig(), bitmapCache );

        // Populate the cache with images in a worker thread?

        this.whenUiReady = new WhenUiReady( jframe, bitmapCache );

        waitForUi.notifyUiReady();
    }
}

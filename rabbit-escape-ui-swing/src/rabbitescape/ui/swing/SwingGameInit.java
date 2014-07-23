package rabbitescape.ui.swing;

public class SwingGameInit implements Runnable
{
    public class WaitForCanvas
    {
        private synchronized void notifyCanvasReady()
        {
            assert canvas != null;
            notify();
        }

        public synchronized GameJFrame waitForCanvas()
        {
            while ( canvas == null )
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

            return canvas;
        }
    }

    public final WaitForCanvas waitForCanvas = new WaitForCanvas();

    private GameJFrame canvas = null;

    @Override
    public void run()
    {
        GameJFrame frame = new GameJFrame();
        // load all images in a worker thread

        // When ready, put frame or canvas or something into a variable.

        canvas = frame;

        waitForCanvas.notifyCanvasReady();
    }
}

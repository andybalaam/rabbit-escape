package rabbitescape.ui.swing;

import rabbitescape.engine.World;
import rabbitescape.render.GameLoop;

public class SwingGameLoop implements GameLoop
{
    private final SwingGameInit init;
    private boolean running;

    public SwingGameLoop( SwingGameInit init, World world )
    {
        this.init = init;
        this.running = true;
    }

    @Override
    public void run()
    {
        GameJFrame canvas = init.waitForCanvas.waitForCanvas();
        canvas.setGameLoop( this );

        while( running )
        {
            try
            {
                Thread.sleep( 1000 );
            }
            catch ( InterruptedException e )
            {
            }
        }
    }

    public void stop()
    {
        running = false;
    }
}

package rabbitescape.ui.swing;

import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

public abstract class BufferedDraw
{
    private final BufferStrategy strategy;

    public BufferedDraw( BufferStrategy strategy )
    {
        this.strategy = strategy;
    }

    abstract void draw( Graphics2D g );

    void run()
    {
        do
        {
            do
            {
                Graphics2D g;
                try
                {
                    g = (Graphics2D)strategy.getDrawGraphics();
                }
                catch ( IllegalStateException e )
                {
                    // Display has gone away - nothing to do
                    return;
                }
                catch ( NullPointerException e)
                {
                    // Can get this from within getDrawGraphics()
                    // While resizing when not paused.
                    // Ignoring it seems harmless.
                    return;
                }

                try
                {
                    draw( g );
                }
                finally
                {
                    g.dispose();
                }

            } while( strategy.contentsRestored() );

            try
            {
                strategy.show();
            }
            catch ( IllegalStateException e )
            {
                // Display has gone away - nothing to do
                return;
            }

        } while( strategy.contentsLost() );
    }
}

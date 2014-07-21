package rabbitescape.ui.swing;

import java.io.IOException;

import rabbitescape.render.FailedToLoadImage;

public class SwingBitmapLoader
{
    public SwingBitmap load( String name )
    {
        try
        {
            return new SwingBitmap(
                name,
                javax.imageio.ImageIO.read( getClass().getResource( name ) )
            );
        }
        catch ( IOException e )
        {
            throw new FailedToLoadImage( name, e );
        }
    }
}

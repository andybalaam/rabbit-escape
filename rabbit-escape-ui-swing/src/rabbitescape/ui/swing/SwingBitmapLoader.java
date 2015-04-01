package rabbitescape.ui.swing;

import java.io.IOException;

import rabbitescape.render.BitmapLoader;
import rabbitescape.render.FailedToLoadImage;

public class SwingBitmapLoader implements BitmapLoader<SwingBitmap>
{
    @Override
    public SwingBitmap load( String name )
    {
        String resourcePath =
            "/rabbitescape/ui/swing/images32/" + name + ".png";

        try
        {
            return new SwingBitmap(
                name,
                javax.imageio.ImageIO.read(
                    getClass().getResource( resourcePath )
                )
            );
        }
        catch ( IOException | IllegalArgumentException e )
        {
            throw new FailedToLoadImage( resourcePath, e );
        }
    }
}

package rabbitescape.ui.swing;

import static org.hamcrest.core.IsEqual.*;
import static org.hamcrest.MatcherAssert.*;
import static rabbitescape.ui.swing.Tools.*;

import org.junit.Test;

import rabbitescape.render.Renderer;
import rabbitescape.render.Sprite;

public class TestRendering
{
    @Test
    public void Draw_sprites_on_grid_lines_unscaled()
    {
        SwingBitmapLoader bitmapLoader = new SwingBitmapLoader();

        SwingBitmap x = bitmapLoader.load( "/rabbitescape/ui/swing/x.png" );

        Sprite x1 = new Sprite( x, 0, 0 );
        Sprite x2 = new Sprite( x, 1, 0 );

        SwingCanvas output = new SwingCanvas(
            new SwingBitmap( "output", 64, 32 ) );

        Renderer renderer = new Renderer( output );
        renderer.render( new Sprite[] { x1, x2 }, new SwingPaint() );

        SwingBitmap expected = bitmapLoader.load(
            "/rabbitescape/ui/swing/xx.png" );

        assertThat( output.bitmap, equalTo( expected ) );
    }
}

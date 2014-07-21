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

        Sprite[] sprites = new Sprite[] {
            new Sprite( x, 0, 0 ),
            new Sprite( x, 1, 0 ),
            new Sprite( x, 0, 1 ),
            new Sprite( x, 1, 1 ),
            new Sprite( x, 0, 2 ),
            new Sprite( x, 1, 2 ),
        };

        SwingCanvas output = new SwingCanvas(
            new SwingBitmap( "output", 64, 96 ) );

        Renderer renderer = new Renderer( output );
        renderer.render( sprites, new SwingPaint() );

        SwingBitmap expected = bitmapLoader.load(
            "/rabbitescape/ui/swing/sixx.png" );

        assertThat( output.bitmap, equalTo( expected ) );
    }
}

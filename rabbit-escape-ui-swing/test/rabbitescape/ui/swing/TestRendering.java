package rabbitescape.ui.swing;

import static org.hamcrest.core.IsEqual.*;
import static org.hamcrest.MatcherAssert.*;
import static rabbitescape.ui.swing.Tools.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

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
            new Sprite( x, 1, 2 ),
        };

        SwingCanvas output = blankCanvas( 64, 96 );

        Renderer renderer = new Renderer( output, 0, 0 );
        renderer.render( sprites, new SwingPaint() );

        SwingBitmap expected = bitmapLoader.load(
            "/rabbitescape/ui/swing/sixx.png" );

        assertThat( output.bitmap, equalTo( expected ) );
    }

    @Test
    public void Renderer_can_be_offset()
    {
        SwingBitmapLoader bitmapLoader = new SwingBitmapLoader();

        SwingBitmap x = bitmapLoader.load( "/rabbitescape/ui/swing/x.png" );

        Sprite[] sprites = new Sprite[] {
            new Sprite( x, 0, 0 )
        };

        SwingCanvas output = blankCanvas( 35, 34 );

        Renderer renderer = new Renderer( output, 3, 2 );
        renderer.render( sprites, new SwingPaint() );

        SwingBitmap expected = bitmapLoader.load(
            "/rabbitescape/ui/swing/x32.png" );

        assertThat( output.bitmap, equalTo( expected ) );
    }

    /**
     * @return a SwingCanvas that has its background set to the magic "none"
     *         colour that that I chose to be RGB 64, 177, 170.
     */
    private SwingCanvas blankCanvas( int width, int height )
    {
        SwingBitmap outBitmap = new SwingBitmap( "output", width, height );
        BufferedImage image = outBitmap.image;
        Graphics2D gfx = image.createGraphics();

        gfx.setColor( new Color( 64, 177, 170 ) );
        gfx.fillRect(
            0, 0, outBitmap.image.getWidth(), outBitmap.image.getHeight() );

        return new SwingCanvas( outBitmap );
    }
}

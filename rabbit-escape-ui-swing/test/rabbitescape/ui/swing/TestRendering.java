package rabbitescape.ui.swing;

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
        SwingBitmapScaler scaler = new SwingBitmapScaler();

        SwingBitmap x = bitmapLoader.load( "/rabbitescape/ui/swing/x.png" );

        Sprite[] sprites = new Sprite[] {
            new Sprite( x, scaler, 0, 0, 32, 0, 0 ),
            new Sprite( x, scaler, 1, 0, 32, 0, 0 ),
            new Sprite( x, scaler, 0, 1, 32, 0, 0 ),
            new Sprite( x, scaler, 1, 1, 32, 0, 0 ),
            new Sprite( x, scaler, 1, 2, 32, 0, 0 ),
        };

        SwingBitmapCanvas output = blankCanvas( 64, 96 );

        Renderer renderer = new Renderer( 0, 0, 32 );
        renderer.render( output, sprites, new SwingPaint() );

        SwingBitmap expected = bitmapLoader.load(
            "/rabbitescape/ui/swing/sixx.png" );

        assertThat( output.bitmap, equalTo( expected ) );
    }

    @Test
    public void Renderer_can_be_offset()
    {
        SwingBitmapLoader bitmapLoader = new SwingBitmapLoader();
        SwingBitmapScaler scaler = new SwingBitmapScaler();

        SwingBitmap x = bitmapLoader.load( "/rabbitescape/ui/swing/x.png" );

        Sprite[] sprites = new Sprite[] {
            new Sprite( x, scaler, 0, 0, 32, 0, 0 )
        };

        SwingBitmapCanvas output = blankCanvas( 35, 34 );

        Renderer renderer = new Renderer( 3, 2, 32 );
        renderer.render( output, sprites, new SwingPaint() );

        SwingBitmap expected = bitmapLoader.load(
            "/rabbitescape/ui/swing/x32.png" );

        assertThat( output.bitmap, equalTo( expected ) );
    }

    @Test
    public void Renderer_tile_size_can_be_non_32()
    {
        SwingBitmapLoader bitmapLoader = new SwingBitmapLoader();
        SwingBitmapScaler scaler = new SwingBitmapScaler();

        SwingBitmap x = bitmapLoader.load( "/rabbitescape/ui/swing/x.png" );

        Sprite[] sprites = new Sprite[] {
            new Sprite( x, scaler, 1, 1, 32, 0, 0 ) // Note: original tile size
                                                    // is larger
        };

        SwingBitmapCanvas output = blankCanvas( 35, 34 );

        Renderer renderer = new Renderer( 3, 2, 16 ); // But the
                                                              // renderer gets
                                                              // to override.
        renderer.render( output, sprites, new SwingPaint() );

        SwingBitmap expected = bitmapLoader.load(
            "/rabbitescape/ui/swing/x16-32.png" );

        assertThat( output.bitmap, equalTo( expected ) );
    }

    @Test
    public void Sprites_can_be_offset_individually()
    {
        SwingBitmapLoader bitmapLoader = new SwingBitmapLoader();
        SwingBitmapScaler scaler = new SwingBitmapScaler();

        SwingBitmap x = bitmapLoader.load( "/rabbitescape/ui/swing/x.png" );

        Sprite[] sprites = new Sprite[] {
            new Sprite( x, scaler, 0, 0, 32, 3, 2 )  // Sprite offset
        };

        SwingBitmapCanvas output = blankCanvas( 35, 34 );

        Renderer renderer = new Renderer( 0, 0, 32 ); // Renderer not
        renderer.render( output, sprites, new SwingPaint() );

        SwingBitmap expected = bitmapLoader.load(
            "/rabbitescape/ui/swing/x32.png" );

        assertThat( output.bitmap, equalTo( expected ) );
    }

    @Test
    public void Sprites_offset_is_scaled_relative_to_tile_size()
    {
        SwingBitmapLoader bitmapLoader = new SwingBitmapLoader();
        SwingBitmapScaler scaler = new SwingBitmapScaler();

        SwingBitmap x = bitmapLoader.load( "/rabbitescape/ui/swing/x.png" );

        Sprite[] sprites = new Sprite[] {
            new Sprite( x, scaler, 1, 1, 32, 6, 4 )
        };

        SwingBitmapCanvas output = blankCanvas( 35, 34 );

        Renderer renderer = new Renderer( 0, 0, 16 );
        renderer.render( output, sprites, new SwingPaint() );

        SwingBitmap expected = bitmapLoader.load(
            "/rabbitescape/ui/swing/x16-32.png" );

        assertThat( output.bitmap, equalTo( expected ) );
    }

    // --

    /**
     * @return a SwingImageCanvas that has its background set to the magic
     *         "none" colour that that I chose to be RGB 64, 177, 170.
     */
    private SwingBitmapCanvas blankCanvas( int width, int height )
    {
        SwingBitmap outBitmap = new SwingBitmap( "output", width, height );
        BufferedImage image = outBitmap.image;
        Graphics2D gfx = image.createGraphics();

        gfx.setColor( new Color( 64, 177, 170 ) );
        gfx.fillRect(
            0, 0, outBitmap.image.getWidth(), outBitmap.image.getHeight() );

        return new SwingBitmapCanvas( outBitmap );
    }
}

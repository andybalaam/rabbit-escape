package rabbitescape.ui.swing;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static rabbitescape.ui.swing.Tools.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import rabbitescape.render.Renderer;
import rabbitescape.render.ScaledBitmap;
import rabbitescape.render.Sprite;

public class TestSwingRendering
{
    @Test
    public void Draw_sprites_on_grid_lines_unscaled()
    {
        SwingBitmapLoader bitmapLoader = new SwingBitmapLoader();
        SwingBitmapScaler scaler = new SwingBitmapScaler();

        List<Sprite<SwingBitmap>> sprites =
            new ArrayList<Sprite<SwingBitmap>>();

        sprites.add( sprite( scaler, bitmapLoader, 0, 0, 0, 0 ) );
        sprites.add( sprite( scaler, bitmapLoader, 1, 0, 0, 0 ) );
        sprites.add( sprite( scaler, bitmapLoader, 0, 1, 0, 0 ) );
        sprites.add( sprite( scaler, bitmapLoader, 1, 1, 0, 0 ) );
        sprites.add( sprite( scaler, bitmapLoader, 1, 2, 0, 0 ) );

        SwingBitmapCanvas output = blankCanvas( 64, 96 );

        Renderer<SwingBitmap> renderer = new Renderer<SwingBitmap>( 0, 0, 32 );
        renderer.render( output, sprites, new SwingPaint() );

        SwingBitmap expected = bitmapLoader.load( "sixx", 32 );

        assertThat( output.bitmap, equalTo( expected ) );
    }

    @Test
    public void Renderer_can_be_offset()
    {
        SwingBitmapLoader bitmapLoader = new SwingBitmapLoader();
        SwingBitmapScaler scaler = new SwingBitmapScaler();

        List<Sprite<SwingBitmap>> sprites =
            new ArrayList<Sprite<SwingBitmap>>();

        sprites.add( sprite( scaler, bitmapLoader, 0, 0, 0, 0 ) );

        SwingBitmapCanvas output = blankCanvas( 35, 34 );

        Renderer<SwingBitmap> renderer = new Renderer<SwingBitmap>( 3, 2, 32 );
        renderer.render( output, sprites, new SwingPaint() );

        SwingBitmap expected = bitmapLoader.load( "x32", 32 );

        assertThat( output.bitmap, equalTo( expected ) );
    }

    @Test
    public void Renderer_tile_size_can_be_non_32()
    {
        SwingBitmapLoader bitmapLoader = new SwingBitmapLoader();
            // The tile size  of this loader is 32, which is larger ...
        SwingBitmapScaler scaler = new SwingBitmapScaler();

        List<Sprite<SwingBitmap>> sprites =
            new ArrayList<Sprite<SwingBitmap>>();

        sprites.add( sprite( scaler, bitmapLoader, 1, 1, 0, 0 ) );

        SwingBitmapCanvas output = blankCanvas( 35, 34 );

        Renderer<SwingBitmap> renderer = new Renderer<SwingBitmap>( 3, 2, 16 );
            // ... but the renderer gets to say what size it wants (16).
        renderer.render( output, sprites, new SwingPaint() );

        SwingBitmap expected = bitmapLoader.load( "x16-32", 32 );

        assertThat( output.bitmap, equalTo( expected ) );
    }

    @Test
    public void Sprites_can_be_offset_individually()
    {
        SwingBitmapLoader bitmapLoader = new SwingBitmapLoader();
        SwingBitmapScaler scaler = new SwingBitmapScaler();

        List<Sprite<SwingBitmap>> sprites =
            new ArrayList<Sprite<SwingBitmap>>();

        sprites.add( sprite( scaler, bitmapLoader, 0, 0, 3, 2 ) );
        // the Sprite is offset

        SwingBitmapCanvas output = blankCanvas( 35, 34 );

        Renderer<SwingBitmap> renderer = new Renderer<SwingBitmap>( 0, 0, 32 );
        // the Renderer is not

        renderer.render( output, sprites, new SwingPaint() );

        SwingBitmap expected = bitmapLoader.load( "x32", 32 );

        assertThat( output.bitmap, equalTo( expected ) );
    }

    @Test
    public void Sprites_offset_is_scaled_relative_to_tile_size()
    {
        SwingBitmapLoader bitmapLoader = new SwingBitmapLoader();
        SwingBitmapScaler scaler = new SwingBitmapScaler();

        List<Sprite<SwingBitmap>> sprites =
            new ArrayList<Sprite<SwingBitmap>>();

        sprites.add( sprite( scaler, bitmapLoader, 1, 1, 6, 4 ) );

        SwingBitmapCanvas output = blankCanvas( 35, 34 );

        Renderer<SwingBitmap> renderer = new Renderer<SwingBitmap>( 0, 0, 16 );
        renderer.render( output, sprites, new SwingPaint() );

        assertThat( sprites.get( 0 ).offsetX( 16 ), equalTo( 3 ) );
        assertThat( sprites.get( 0 ).offsetY( 16 ), equalTo( 2 ) );

        SwingBitmap expected = bitmapLoader.load( "x16-32", 32 );

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

    private Sprite<SwingBitmap> sprite(
        SwingBitmapScaler scaler,
        SwingBitmapLoader bitmapLoader,
        int tileX,
        int tileY,
        int offsetX,
        int offsetY
    )
    {
        return new Sprite<SwingBitmap>(
            new ScaledBitmap<SwingBitmap>( scaler, bitmapLoader, "x" ),
            tileX,
            tileY,
            offsetX,
            offsetY
        );
    }
}

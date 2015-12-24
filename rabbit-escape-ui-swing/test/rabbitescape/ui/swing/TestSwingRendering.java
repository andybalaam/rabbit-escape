package rabbitescape.ui.swing;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static rabbitescape.ui.swing.Tools.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rabbitescape.render.BitmapCache;
import rabbitescape.render.Renderer;
import rabbitescape.render.Sprite;

public class TestSwingRendering
{
    private SwingBitmapLoader loader;
    private BitmapCache<SwingBitmap> cache;

    @Before
    public void setUp()
    {
        loader = new SwingBitmapLoader();
        cache = new BitmapCache<SwingBitmap>(
            loader,
            new SwingBitmapScaler(),
            Runtime.getRuntime().maxMemory() / 8
        );
    }

    @After
    public void tearDown()
    {
        cache.recycle();
    }

    @Test
    public void Draw_sprites_on_grid_lines_unscaled()
    {
        List<Sprite> sprites =
            new ArrayList<Sprite>();

        sprites.add( sprite( 0, 0, 0, 0 ) );
        sprites.add( sprite( 1, 0, 0, 0 ) );
        sprites.add( sprite( 0, 1, 0, 0 ) );
        sprites.add( sprite( 1, 1, 0, 0 ) );
        sprites.add( sprite( 1, 2, 0, 0 ) );

        SwingBitmapCanvas output = blankCanvas( 64, 96 );

        Renderer<SwingBitmap, SwingPaint> renderer =
            new Renderer<SwingBitmap, SwingPaint>( 0, 0, 32, cache );

        renderer.render( output, sprites, new SwingPaint( null ) );

        SwingBitmap expected = loader.load( "sixx", 32 );

        assertThat( output.bitmap, equalTo( expected ) );
    }

    @Test
    public void Renderer_can_be_offset()
    {
        List<Sprite> sprites =
            new ArrayList<Sprite>();

        sprites.add( sprite( 0, 0, 0, 0 ) );

        SwingBitmapCanvas output = blankCanvas( 35, 34 );

        Renderer<SwingBitmap, SwingPaint> renderer =
            new Renderer<SwingBitmap, SwingPaint>( 3, 2, 32, cache );

        renderer.render( output, sprites, new SwingPaint( null ) );

        SwingBitmap expected = loader.load( "x32", 32 );

        assertThat( output.bitmap, equalTo( expected ) );
    }

    @Test
    public void Renderer_tile_size_can_be_non_32()
    {
        List<Sprite> sprites =
            new ArrayList<Sprite>();

        sprites.add( sprite( 1, 1, 0, 0 ) );

        SwingBitmapCanvas output = blankCanvas( 35, 34 );

        Renderer<SwingBitmap, SwingPaint> renderer =
            new Renderer<SwingBitmap, SwingPaint>( 3, 2, 16, cache );
            // ... but the renderer gets to say what size it wants (16).

        renderer.render( output, sprites, new SwingPaint( null ) );

        SwingBitmap expected = loader.load( "x16-32", 32 );

        assertThat( output.bitmap, equalTo( expected ) );
    }

    @Test
    public void Sprites_can_be_offset_individually()
    {
        List<Sprite> sprites =
            new ArrayList<Sprite>();

        sprites.add( sprite( 0, 0, 3, 2 ) );
        // the Sprite is offset

        SwingBitmapCanvas output = blankCanvas( 35, 34 );

        Renderer<SwingBitmap, SwingPaint> renderer =
            new Renderer<SwingBitmap, SwingPaint>( 0, 0, 32, cache );
        // the Renderer is not

        renderer.render( output, sprites, new SwingPaint( null ) );

        SwingBitmap expected = loader.load( "x32", 32 );

        assertThat( output.bitmap, equalTo( expected ) );
    }

    @Test
    public void Sprites_offset_is_scaled_relative_to_tile_size()
    {
        List<Sprite> sprites =
            new ArrayList<Sprite>();

        sprites.add( sprite( 1, 1, 6, 4 ) );

        SwingBitmapCanvas output = blankCanvas( 35, 34 );

        Renderer<SwingBitmap, SwingPaint> renderer =
            new Renderer<SwingBitmap, SwingPaint>( 0, 0, 16, cache );

        renderer.render( output, sprites, new SwingPaint( null ) );

        assertThat( sprites.get( 0 ).offsetX( 16 ), equalTo( 3 ) );
        assertThat( sprites.get( 0 ).offsetY( 16 ), equalTo( 2 ) );

        SwingBitmap expected = loader.load( "x16-32", 32 );

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

    private Sprite sprite(
        int tileX,
        int tileY,
        int offsetX,
        int offsetY
    )
    {
        return new Sprite(
            "x",
            null,
            tileX,
            tileY,
            offsetX,
            offsetY
        );
    }
}

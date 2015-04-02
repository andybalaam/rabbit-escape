package rabbitescape.render;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.ArrayList;
import java.util.List;

import rabbitescape.render.androidlike.Bitmap;
import rabbitescape.render.androidlike.Canvas;
import rabbitescape.render.androidlike.Paint;

import org.junit.*;

public class TestRendering
{
    @Test
    public void Bitmaps_are_not_rescaled_every_render()
    {
        TrackingBitmapScaler scaler = new TrackingBitmapScaler();

        FakeBitmapLoader loader = new FakeBitmapLoader();

        ScaledBitmap<FakeBitmap> bitmap = new ScaledBitmap<FakeBitmap>(
            scaler, loader, "x" );

        List<Sprite<FakeBitmap>> sprites1 = new ArrayList<Sprite<FakeBitmap>>();
        sprites1.add( new Sprite<FakeBitmap>( bitmap, 1, 1, 6, 4 ) );

        List<Sprite<FakeBitmap>> sprites2 = new ArrayList<Sprite<FakeBitmap>>();
        sprites2.add( new Sprite<FakeBitmap>( bitmap, 1, 1, 6, 4 ) );

        FakeCanvas output = new FakeCanvas();

        Renderer<FakeBitmap> renderer = new Renderer<FakeBitmap>( 0, 0, 16 );

        // Sanity: no calls to scale yet
        assertThat( scaler.scaleCalls.size(), equalTo( 0 ) );

        renderer.render( output, sprites1, null );

        // Sanity: we had to scale the bitmap
        assertThat( scaler.scaleCalls.size(), equalTo( 1 ) );
        assertThat( scaler.scaleCalls.get( 0 ), equalTo( 0.5 ) );

        // This is what we are testing: render new sprite, same bitmap
        renderer.render( output, sprites2, null );

        // No new scale call was made
        assertThat( scaler.scaleCalls.size(), equalTo( 1 ) );
    }

    // ---

    private static class FakeBitmap implements Bitmap
    {
        @Override
        public String name()
        {
            return null;
        }

        @Override
        public void recycle()
        {
        }
    }

    private static class FakeBitmapLoader implements BitmapLoader<FakeBitmap>
    {
        @Override
        public FakeBitmap load( String fileName, int tileSize )
        {
            return new FakeBitmap();
        }

        @Override
        public int sizeFor( int tileSize )
        {
            return 32;
        }
    }

    private static class TrackingBitmapScaler
    implements BitmapScaler<FakeBitmap>
    {
        public List<Double> scaleCalls = new ArrayList<Double>();

        @Override
        public FakeBitmap scale( FakeBitmap originalBitmap, double scale )
        {
            scaleCalls.add( scale );
            return null;
        }
    }

    public static class FakeCanvas implements Canvas
    {
        @Override
        public void drawBitmap(
            Bitmap bitmap,
            float left,
            float top,
            Paint paint
        )
        {
        }
    }
}

package rabbitescape.render;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.Test;

import rabbitescape.render.androidlike.Bitmap;

public class TestScaledBitmap
{
    @Test
    public void Scaled_reports_total_size()
    {
        MyBitmapLoader loader = new MyBitmapLoader();

        ScaledBitmap<MyBitmap> scaled = new ScaledBitmap<>(
            new MyBitmapScaler(), loader, "" );

        scaled.bitmap( 5 );

        // The unscaled is 10, +5 for the scaled
        assertThat( scaled.size(), is( 15L ) );
    }

    @Test
    public void Unscaled_does_not_double_count()
    {
        MyBitmapLoader loader = new MyBitmapLoader();

        ScaledBitmap<MyBitmap> scaled = new ScaledBitmap<>(
            new MyBitmapScaler(), loader, "" );

        scaled.bitmap( 10 );

        // The unscaled and scaled are identical so only count 10 once
        assertThat( scaled.size(), is( 10L ) );
    }

    // ---

    private static class MyBitmapLoader implements BitmapLoader<MyBitmap>
    {
        @Override
        public MyBitmap load( String fileName, int tileSize )
        {
            return new MyBitmap( tileSize );
        }

        @Override
        public int sizeFor( int tileSize )
        {
            return 10;
        }
    }

    private static class MyBitmapScaler implements BitmapScaler<MyBitmap>
    {
        @Override
        public MyBitmap scale( MyBitmap originalBitmap, double scale )
        {
            return new MyBitmap( (long)( originalBitmap.byteCount * scale ) );
        }
    }

    private static class MyBitmap implements Bitmap
    {
        private final long byteCount;

        public MyBitmap( long byteCount )
        {
            this.byteCount = byteCount;
        }

        @Override
        public String name()
        {
            return null;
        }

        @Override
        public int width()
        {
            return 0;
        }

        @Override
        public int height()
        {
            return 0;
        }

        @Override
        public long getByteCount()
        {
            return byteCount;
        }

        @Override
        public void recycle()
        {
        }
    }
}

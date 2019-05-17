package rabbitescape.render;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static rabbitescape.engine.Tools.*;

import java.util.ArrayList;
import java.util.List;

import rabbitescape.render.androidlike.Bitmap;

import org.junit.Test;

public class TestBitmapCache
{
    @Test
    public void Cache_and_scale_calls_load_if_not_loaded_before()
    {
        TrackingBitmapLoader loader = new TrackingBitmapLoader();
        BitmapCache<FakeBitmap> cache = new BitmapCache<>( loader, null, 5 );

        // This is what we are testing - get from the cache and load a bitmap
        cache.get( "a/b/foo.png", 32 );

        // Load was called once
        assertThat(
            loader.loadCalls,
            equalToList( new String[] { "a/b/foo.png" } )
        );
    }

    @Test
    public void Getting_a_bitmap_from_the_cache_returns_the_same_object()
    {
        TrackingBitmapLoader loader = new TrackingBitmapLoader();
        BitmapCache<FakeBitmap> cache = new BitmapCache<>( loader, null, 5 );

        FakeBitmap gotBitmap1 = cache.get( "a/b/foo01.png", 32 );
        FakeBitmap gotBitmap2 = cache.get( "a/b/foo02.png", 32 );

        // Sanity
        assertThat( gotBitmap1, not( sameInstance( gotBitmap2 ) ) );

        // This is what we are testing - get same instance when call again
        assertThat(
            gotBitmap1,
            sameInstance( cache.get( "a/b/foo01.png", 32 ) )
        );

        assertThat(
            gotBitmap1,
            sameInstance( cache.get( "a/b/foo01.png", 32 ) )
        );

        assertThat(
            gotBitmap2,
            sameInstance( cache.get( "a/b/foo02.png", 32 ) )
        );

        assertThat(
            gotBitmap2,
            sameInstance( cache.get( "a/b/foo02.png", 32 ) )
        );
    }

    @Test
    public void Load_only_called_once_even_if_get_called_multiple_times()
    {
        TrackingBitmapLoader loader = new TrackingBitmapLoader();
        TrackingBitmapScaler scaler = new TrackingBitmapScaler();
        BitmapCache<FakeBitmap> cache = new BitmapCache<>( loader, scaler, 5 );

        // This is what we are testing - call get 3 times
        FakeBitmap bitmap = cache.get( "a/b/foo.png", 30 );
        cache.get( "a/b/foo.png", 30 );
        cache.get( "a/b/foo.png", 30 );

        // Load was still only called once
        assertThat(
            loader.loadCalls,
            equalToList( new String[] { "a/b/foo.png" } )
        );

        // Scale was only called once
        assertThat( scaler.scaleCalls.size(), equalTo( 1 ) );

        // The bitmap was not recycled
        assertThat( bitmap.recycled, is( false ) );
    }

    @Test
    public void Scale_is_not_called_when_we_ask_for_the_loaded_size()
    {
        TrackingBitmapLoader loader = new TrackingBitmapLoader();
        TrackingBitmapScaler scaler = new TrackingBitmapScaler();
        BitmapCache<FakeBitmap> cache = new BitmapCache<>( loader, scaler, 5 );

        // This is what we are testing - get it and scale 3 times to
        // the same size as the loader uses.
        cache.get( "a/b/foo.png", 32 );
        cache.get( "a/b/foo.png", 32 );
        cache.get( "a/b/foo.png", 32 );

        // Scale was only called once
        assertThat( scaler.scaleCalls.size(), equalTo( 0 ) );
    }

    @Test
    public void Scale_is_only_called_the_first_time_we_ask_for_a_size()
    {
        TrackingBitmapLoader loader = new TrackingBitmapLoader();
        TrackingBitmapScaler scaler = new TrackingBitmapScaler();
        BitmapCache<FakeBitmap> cache = new BitmapCache<>( loader, scaler, 5 );

        // This is what we are testing - get it and scale 3 times to
        // the same size, but not the default loader size
        cache.get( "a/b/foo.png", 19 );

        // Scale was called
        assertThat( scaler.scaleCalls.size(), equalTo( 1 ) );

        cache.get( "a/b/foo.png", 19 );
        cache.get( "a/b/foo.png", 19 );

        // Scale was not called again
        assertThat( scaler.scaleCalls.size(), equalTo( 1 ) );
    }

    @Test
    public void Recently_accessed_items_are_not_purged_before_older()
    {
        TrackingBitmapLoader loader = new TrackingBitmapLoader();
        BitmapCache<FakeBitmap> cache = new BitmapCache<>( loader, null, 5 );

        // Get enough to fill the cache
        FakeBitmap b1 = cache.get( "a/b/foo01.png", 32 );
        FakeBitmap b2 = cache.get( "a/b/foo02.png", 32 );
        FakeBitmap b3 = cache.get( "a/b/foo03.png", 32 );
        FakeBitmap b4 = cache.get( "a/b/foo04.png", 32 );
        FakeBitmap b5 = cache.get( "a/b/foo05.png", 32 );

        // This is what we are testing: re-access foo02 and it should avoid
        // being purged
        cache.get( "a/b/foo02.png", 32 );

        // Sanity
        assertThat( loader.loadCalls.size(), equalTo( 5 ) );

        // Get 3 new things - causing 3 things to be purged
        cache.get( "a/b/foo06.png", 32 );
        cache.get( "a/b/foo07.png", 32 );
        cache.get( "a/b/foo08.png", 32 );

        assertThat( b1.recycled, is( true ) );
        assertThat( b2.recycled, is( false ) );
        assertThat( b3.recycled, is( true ) );
        assertThat( b4.recycled, is( true ) );
        assertThat( b5.recycled, is( false ) );

        // Sanity
        assertThat( loader.loadCalls.size(), equalTo( 8 ) );

        // Get foo02 - should not need to re-load it
        cache.get( "a/b/foo02.png", 32 );

        // Load was not called again
        assertThat( loader.loadCalls.size(), equalTo( 8 ) );

        // Get foo01 - for this we should need to re-load it
        cache.get( "a/b/foo01.png", 32 );

        // Load was called
        assertThat( loader.loadCalls.size(), equalTo( 9 ) );
    }

    @Test
    public void Bitmaps_are_purged_in_order_when_cache_is_full()
    {
        TrackingBitmapLoader loader = new TrackingBitmapLoader();
        BitmapCache<FakeBitmap> cache = new BitmapCache<>( loader, null, 5 );

        // Get enough to fill the cache
        FakeBitmap b1 = cache.get( "a/b/foo01.png", 32 );
        FakeBitmap b2 = cache.get( "a/b/foo02.png", 32 );
        FakeBitmap b3 = cache.get( "a/b/foo03.png", 32 );
        FakeBitmap b4 = cache.get( "a/b/foo04.png", 32 );
        FakeBitmap b5 = cache.get( "a/b/foo05.png", 32 );

        // Not recycled yet
        assertThat( b1.recycled, is( false ) );

        // This is what we are testing: get another and the first bitmap
        // should get purged
        cache.get( "a/b/foo06.png", 32 );

        // Sanity
        assertThat( loader.loadCalls.size(), equalTo( 6 ) );
        assertThat( loader.loadCalls.get( 5 ), equalTo( "a/b/foo06.png" ) );

        // The first was recycled, others weren't
        assertThat( b1.recycled, is( true ) );
        assertThat( b2.recycled, is( false ) );
        assertThat( b3.recycled, is( false ) );
        assertThat( b4.recycled, is( false ) );
        assertThat( b5.recycled, is( false ) );

        // Try and get the first - it should have to be reloaded
        cache.get( "a/b/foo01.png", 32 );

        // Load was called again for foo01
        assertThat( loader.loadCalls.size(), equalTo( 7 ) );
        assertThat( loader.loadCalls.get( 6 ), equalTo( "a/b/foo01.png" ) );
    }

    // --

    private static class FakeBitmap implements Bitmap
    {
        public boolean recycled = false;

        @Override
        public String name()
        {
            return "fake";
        }

        @Override
        public int width()
        {
            return 32;
        }

        @Override
        public int height()
        {
            return 32;
        }

        @Override
        public void recycle()
        {
            assertThat( recycled, is( false ) );

            recycled = true;
        }

        @Override
        public long getByteCount()
        {
            return 1;
        }
    }

    private static class TrackingBitmapLoader
        implements BitmapLoader<FakeBitmap>
    {
        public List<String> loadCalls = new ArrayList<String>();

        @Override
        public FakeBitmap load( String fileName, int tileSize )
        {
            loadCalls.add( fileName );
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
        public List<String> scaleCalls = new ArrayList<String>();

        @Override
        public FakeBitmap scale( FakeBitmap originalBitmap, double scale )
        {
            scaleCalls.add( String.valueOf( scale ) );
            return new FakeBitmap();
        }
    }
}

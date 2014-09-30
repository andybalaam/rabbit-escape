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
    public void Cache_calls_load_if_not_loaded_before()
    {
        TrackingBitmapLoader loader = new TrackingBitmapLoader();
        BitmapCache<FakeBitmap> cache = new BitmapCache<>( loader, 5 );

        // This is what we are testing
        cache.get( "a/b/foo.png" );

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
        BitmapCache<FakeBitmap> cache = new BitmapCache<>( loader, 5 );

        FakeBitmap gotBitmap1 = cache.get( "a/b/foo01.png" );
        FakeBitmap gotBitmap2 = cache.get( "a/b/foo02.png" );

        // Sanity
        assertThat( gotBitmap1, not( sameInstance( gotBitmap2 ) ) );

        // This is what we are testing - get same instance when call again
        assertThat( gotBitmap1, sameInstance( cache.get( "a/b/foo01.png" ) ) );
        assertThat( gotBitmap1, sameInstance( cache.get( "a/b/foo01.png" ) ) );

        assertThat( gotBitmap2, sameInstance( cache.get( "a/b/foo02.png" ) ) );
        assertThat( gotBitmap2, sameInstance( cache.get( "a/b/foo02.png" ) ) );
    }

    @Test
    public void Load_only_called_once_even_if_get_called_multiple_times()
    {
        TrackingBitmapLoader loader = new TrackingBitmapLoader();
        BitmapCache<FakeBitmap> cache = new BitmapCache<>( loader, 5 );

        // This is what we are testing - call get 3 times
        FakeBitmap b1 = cache.get( "a/b/foo.png" );
        cache.get( "a/b/foo.png" );
        cache.get( "a/b/foo.png" );

        // Load was still only called once
        assertThat(
            loader.loadCalls,
            equalToList( new String[] { "a/b/foo.png" } )
        );

        // The bitmap was not recycled
        assertThat( b1.recycled, is( false ) );
    }

    @Test
    public void Recently_accessed_items_are_not_purged_before_older()
    {
        TrackingBitmapLoader loader = new TrackingBitmapLoader();
        BitmapCache<FakeBitmap> cache = new BitmapCache<>( loader, 5 );

        // Get enough to fill the cache
        FakeBitmap b1 = cache.get( "a/b/foo01.png" );
        FakeBitmap b2 = cache.get( "a/b/foo02.png" );
        FakeBitmap b3 = cache.get( "a/b/foo03.png" );
        FakeBitmap b4 = cache.get( "a/b/foo04.png" );
        FakeBitmap b5 = cache.get( "a/b/foo05.png" );

        // This is what we are testing: re-access foo02 and it should avoid
        // being purged
        cache.get( "a/b/foo02.png" );

        // Sanity
        assertThat( loader.loadCalls.size(), equalTo( 5 ) );

        // Get 3 new things - causing 3 things to be purged
        cache.get( "a/b/foo06.png" );
        cache.get( "a/b/foo07.png" );
        cache.get( "a/b/foo08.png" );

        assertThat( b1.recycled, is( true ) );
        assertThat( b2.recycled, is( false ) );
        assertThat( b3.recycled, is( true ) );
        assertThat( b4.recycled, is( true ) );
        assertThat( b5.recycled, is( false ) );

        // Sanity
        assertThat( loader.loadCalls.size(), equalTo( 8 ) );

        // Get foo02 - should not need to re-load it
        cache.get( "a/b/foo02.png" );

        // Load was not called again
        assertThat( loader.loadCalls.size(), equalTo( 8 ) );

        // Get foo01 - for this we should need to re-load it
        cache.get( "a/b/foo01.png" );

        // Load was called
        assertThat( loader.loadCalls.size(), equalTo( 9 ) );
    }

    @Test
    public void Bitmaps_are_purged_in_order_when_cache_is_full()
    {
        TrackingBitmapLoader loader = new TrackingBitmapLoader();
        BitmapCache<FakeBitmap> cache = new BitmapCache<>( loader, 5 );

        // Get enough to fill the cache
        FakeBitmap b1 = cache.get( "a/b/foo01.png" );
        FakeBitmap b2 = cache.get( "a/b/foo02.png" );
        FakeBitmap b3 = cache.get( "a/b/foo03.png" );
        FakeBitmap b4 = cache.get( "a/b/foo04.png" );
        FakeBitmap b5 = cache.get( "a/b/foo05.png" );

        // Not recycled yet
        assertThat( b1.recycled, is( false ) );

        // This is what we are testing: get another and the first bitmap
        // should get purged
        cache.get( "a/b/foo06.png" );

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
        cache.get( "a/b/foo01.png" );

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
        public void recycle()
        {
            assertThat( recycled, is( false ) );

            recycled = true;
        }
    }

    private static class TrackingBitmapLoader
        implements BitmapLoader<FakeBitmap>
    {
        public List<String> loadCalls = new ArrayList<String>();

        @Override
        public FakeBitmap load( String fileName )
        {
            loadCalls.add( fileName );
            return new FakeBitmap();
        }
    }
}

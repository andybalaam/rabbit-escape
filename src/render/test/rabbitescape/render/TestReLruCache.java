package rabbitescape.render;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.Test;
import rabbitescape.render.androidlike.Bitmap;

public class TestReLruCache
{
    @Test
    public void Can_get_elements_that_were_added()
    {
        ReLruCache<Obj> cache = new ReLruCache<Obj>( 10L );

        Obj obj1 = new Obj( 1 );
        Obj obj2 = new Obj( 1 );

        cache.put( "obj1", obj1 );
        cache.put( "obj2", obj2 );

        // This is what we are testing: can get things out
        assertThat( cache.get( "obj1" ), sameInstance( obj1 ) );
        assertThat( cache.get( "obj2" ), sameInstance( obj2 ) );
    }

    @Test
    public void When_space_is_still_free_size_is_the_sum_of_added_elements()
    {
        ReLruCache<Obj> cache = new ReLruCache<Obj>( 100 );

        cache.put( "obj1", new Obj( 14 ) );
        cache.put( "obj2", new Obj( 1 ) );

        // This is what we are testing: size is the total
        assertThat( cache.currentSize(), is( 15L ) );

        // Another object adds to the size again
        cache.put( "obj3", new Obj( 3 ) );
        assertThat( cache.currentSize(), is( 18L ) );
    }

    @Test
    public void When_space_is_still_free_replacing_an_object_replaces_its_size()
    {
        ReLruCache<Obj> cache = new ReLruCache<Obj>( 100 );

        cache.put( "obj1", new Obj( 14 ) );
        cache.put( "obj2", new Obj( 1 ) );

        // Sanity
        assertThat( cache.currentSize(), is( 15L ) );

        // obj2 replaced - the size it takes changes from 1 to 3
        cache.put( "obj2", new Obj( 3 ) );
        assertThat( cache.currentSize(), is( 17L ) );

        // obj1 replaced - the size it takes changes from 14 to 7
        cache.put( "obj1", new Obj( 7 ) );
        assertThat( cache.currentSize(), is( 10L ) );
    }

    @Test
    public void When_space_overflows_last_used_object_is_removed()
    {
        ReLruCache<Obj> cache = new ReLruCache<Obj>( 20 );

        Obj obj1 = new Obj( 12 );
        Obj obj2 = new Obj( 10 );

        cache.put( "obj1", obj1 );
        cache.put( "obj2", obj2 ); // Here we go over max size

        // Sanity: obj2 is in cache
        assertThat( cache.get( "obj2" ), sameInstance( obj2 ) );
        assertThat( obj2.recycled, is( false ) );

        // This is what we are testing: obj1 got removed
        assertThat( cache.get( "obj1" ), nullValue() );
        assertThat( obj1.recycled, is( true ) );

        assertThat( cache.currentSize(), is( 10L ) );
    }

    @Test
    public void When_space_overflows_last_used_object_is_removed_even_if_it_was_added_later()
    {
        ReLruCache<Obj> cache = new ReLruCache<Obj>( 13 );

        Obj obj1 = new Obj( 5 );
        Obj obj2 = new Obj( 5 );
        Obj obj3 = new Obj( 5 );

        cache.put( "obj1", obj1 );
        cache.put( "obj2", obj2 );
        cache.get( "obj1" ); // Moves obj1 to front

        // This is what we are testing: go over size
        cache.put( "obj3", obj3 );

        // obj1 is in the cache because it is more recently used
        assertThat( cache.get( "obj1" ), sameInstance( obj1 ) );
        assertThat( obj1.recycled, is( false ) );

        // This is what we are testing: obj2 got removed
        assertThat( cache.get( "obj2" ), nullValue() );
        assertThat( obj2.recycled, is( true ) );

        assertThat( cache.currentSize(), is( 10L ) );
    }

    @Test
    public void Adding_an_object_makes_it_recently_used()
    {
        ReLruCache<Obj> cache = new ReLruCache<Obj>( 13 );

        Obj obj1a = new Obj( 5 );
        Obj obj1b = new Obj( 5 );
        Obj obj2 = new Obj( 5 );
        Obj obj3 = new Obj( 5 );

        cache.put( "obj1", obj1a );
        cache.put( "obj2", obj2 );
        cache.put( "obj1", obj1b ); // obj1 to front, even though different

        // This is what we are testing: go over size
        cache.put( "obj3", obj3 );

        // obj1 is in the cache because it is more recently used
        assertThat( cache.get( "obj1" ), sameInstance( obj1b ) );
        assertThat( obj1b.recycled, is( false ) );

        // This is what we are testing: obj2 got removed
        assertThat( cache.get( "obj2" ), nullValue() );
        assertThat( obj2.recycled, is( true ) );

        assertThat( cache.currentSize(), is( 10L ) );
    }

    @Test
    public void Adding_something_huge_stores_it()
    {
        ReLruCache<Obj> cache = new ReLruCache<Obj>( 13 );

        Obj obj1 = new Obj( 5 );
        Obj obj2 = new Obj( 5 );
        Obj obj3 = new Obj( 15 );

        cache.put( "obj1", obj1 );
        cache.put( "obj2", obj2 );

        // This is what we are testing: object overfills whole cache
        cache.put( "obj3", obj3 );

        // obj3 was stored, even though it's huge
        assertThat( cache.get( "obj3" ), sameInstance( obj3 ) );
        assertThat( obj3.recycled, is( false ) );

        // obj1 and obj2 got removed
        assertThat( cache.get( "obj1" ), nullValue() );
        assertThat( cache.get( "obj2" ), nullValue() );
        assertThat( obj1.recycled, is( true ) );
        assertThat( obj2.recycled, is( true ) );

        assertThat( cache.currentSize(), is( 15L ) );
    }

    // ---

    private static class Obj implements Bitmap
    {
        private final int sz;
        public boolean recycled = false;

        public Obj( int size )
        {
            sz = size;
        }

        @Override
        public String name()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public int width()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public int height()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getByteCount()
        {
            return sz;
        }

        @Override
        public void recycle()
        {
            if ( recycled )
            {
                throw new AssertionError( "Object recycled twice!" );
            }

            recycled = true;
        }
    }
}

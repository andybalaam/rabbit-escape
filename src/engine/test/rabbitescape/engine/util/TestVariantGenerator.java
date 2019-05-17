package rabbitescape.engine.util;

import static rabbitescape.engine.Tools.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.Assert.fail;

import org.junit.Test;

public class TestVariantGenerator
{
    @Test
    public void Generates_integer_variant()
    {
        VariantGenerator gen = new VariantGenerator( 0 );
        int ans = gen.next( 4 );

        assertThat( ans, greaterThan( -1 ) );
    }

    @Test
    public void Answers_are_between_zero_and_max()
    {
        VariantGenerator gen = new VariantGenerator( 0 );

        for ( int i = 0; i < 200; ++i )
        {
            int ans = gen.next( 6 );
            assertThat( ans, greaterThan( -1 ) );
            assertThat( ans, lessThan( 6 ) );
        }
    }

    @Test
    public void Answers_cover_all_in_range()
    {
        // Array to store what answers got hit
        boolean[] hits = new boolean[10];
        for ( int i = 0; i < hits.length; ++i )
        {
            hits[i] = false;
        }

        VariantGenerator gen = new VariantGenerator( 0 );

        // Generate lots and capture which answers were hit
        for ( int i = 0; i < 20000; ++i )
        {
            int ans = gen.next( hits.length );
            hits[ans] = true;
        }

        // All were hit
        for ( int i = 0; i < hits.length; ++i )
        {
            assertThat( hits[i], is( true ) );
        }
    }

    @Test
    public void Seed_varies_answers()
    {
        VariantGenerator gen0 = new VariantGenerator( 0 );
        VariantGenerator gen1 = new VariantGenerator( 1 );

        for ( int i = 0; i < 200; ++i )
        {
            int ans0 = gen0.next( 5 );
            int ans1 = gen1.next( 5 );
            if ( ans0 != ans1 )
            {
                return;
            }
        }

        fail( "All 200 answers were the same, for different seeds!" );
    }
}

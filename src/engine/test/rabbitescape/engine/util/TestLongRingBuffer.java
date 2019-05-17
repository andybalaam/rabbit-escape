package rabbitescape.engine.util;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

public class TestLongRingBuffer
{

    @Test
    public void Buffer_is_a_ring_and_calculates_mean()
    {
        LongRingBuffer rb = new LongRingBuffer( 3 );
        rb.write(1);
        rb.write(2);
        assertThat( rb.full(), equalTo( false ) );
        rb.write(3);
        assertThat( rb.full(), equalTo( true ) );
        assertThat( rb.readOldest(), equalTo( 1l ) );
        assertThat( rb.mean(), equalTo( 2l ) );
    }

}

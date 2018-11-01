package rabbitescape.engine.util;

/**
 * Holds a fixed number of integers in an array.
 * Automatically overwriting older numbers when the
 * capacity in reached.
 */
public class LongRingBuffer
{
    private final long[] buf;
    private int next;
    private boolean filled;

    /**
     * Creates a LongRingBuffer that can store size long integers.
     */
    public LongRingBuffer( int size )
    {
        buf = new long[size];
        next = 0;
        filled = false;
    }

    /**
     * Stores a long integer in the buffer.
     */
    public void write( long n )
    {
        buf[next++] = n;
        if ( next >= buf.length )
        {
            next = 0;
            filled = true;
        }
    }

    public long readOldest()
    {
        return buf[next];
    }

    public boolean full()
    {
        return filled;
    }

    /**
     * Calculates the mean average of the numbers in the buffer.
     * The returned value will be junk if the buffer is not full().
     */
    public long mean()
    {
        long total = 0;
        for ( long i: buf )
        {
            total += i;
        }
        return total / buf.length;
    }
}

package rabbitescape.engine.util;

import java.util.Collection;

public class MathUtil
{
    public static int sum( Collection<Integer> values )
    {
        int total = 0;
        for ( int value : values )
        {
            total += value;
        }
        return total;
    }

    public static int min( int a, int b )
    {
        return ( a < b ? a : b );
    }

    public static int max( int a, int b )
    {
        return ( a > b ? a : b );
    }

    /**
     * Return the value of n if it is between minN and maxN, otherwise return
     * whichever limit is nearer. The behaviour is undefined if minN is
     * greater than maxN.
     */
    public static int constrain( int n, int minN, int maxN )
    {
        if ( n < minN )
        {
            return minN;
        }
        else if ( n > maxN )
        {
            return maxN;
        }
        return n;
    }
}

package rabbitescape.engine.util;

import java.util.ArrayList;
import java.util.List;

public class DupeStringCounter
{
    public final String[] deDuped;
    public final int[] freq;

    public <T> DupeStringCounter( Iterable<T> items )
    {
        ArrayList<String> deDupedList = new ArrayList<String>();
        ArrayList<Integer> freqList = new ArrayList<Integer>();
        for ( T item : items )
        {
            String s = item.toString();
            int i = contains( s, deDupedList );
            if ( -1 == i )
            {
                deDupedList.add( s );
                freqList.add( 1 );
            }
            else
            {
                freqList.set( i, new Integer( freqList.get( i ) + 1 ) );
            }
        }
        deDuped = deDupedList.toArray( new String[deDupedList.size()] );
        freq = new int[freqList.size()];
        for ( int i = 0; i < freq.length ; i++ )
        {
            freq[i] = freqList.get( i );
        }
    }

    public String join( String glue )
    {
        StringBuilder ret = new StringBuilder();

        boolean first = true;
        for ( int i = 0; i < deDuped.length ; i++ )
        {
            if ( first )
            {
                first = false;
            }
            else
            {
                ret.append( glue );
            }

            if ( 1 < freq[i] )
            {
                ret.append( freq[i] );
                ret.append( ' ' );
            }

            ret.append( deDuped[i] );
        }

        return ret.toString();
    }

    /**
     * @return The index of the first occurrence or -1 if it is not present.
     */
    private static int contains( String string, List<String> strings)
    {
        for ( int i = 0; i < strings.size(); i++ )
        {
            if ( strings.get( i ).equals( string ) )
            {
                return i;
            }
        }
        return -1;
    }

}

package rabbitescape.render;

import java.util.Vector;

import rabbitescape.engine.util.Util;

public class Polygon
{
    public Vector<Integer> x, y;

    public Polygon()
    {
        x = new Vector<Integer>();
        y = new Vector<Integer>();
    }

    public boolean isEmpty()
    {
        return x.size() == 0;
    }

    /**
     * return int[] scaled by factor, f, and offset by o.
     */
    public int[] getX( double f, int o )
    {
        Util.reAssert( x.size() == y.size() );
        return unboxArray( x, f, o );
    }

    public int[] getY( double f, int o )
    {
        return unboxArray( y, f, o );
    }

    private int[] unboxArray( Vector<Integer> v, double f, int offset )
    {
        int[] a = new int[v.size()];
        for ( int i = 0; i < v.size() ; i++ )
        {
            a[i] = offset + (int)( f * (double)v.get( i ) );
        }
        return a;
    }
}

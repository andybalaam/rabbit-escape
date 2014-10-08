package rabbitescape.engine.textworld;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import rabbitescape.engine.World;

public class Chars
{
    private static class Point implements Comparable<Point>
    {
        public final int x;
        public final int y;

        public Point( int x, int y )
        {
            this.x = x;
            this.y = y;
        }

        /**
         * Lexical comparison: y then x.
         */
        @Override
        public int compareTo( Point other )
        {
            if ( y < other.y )
            {
                return -1;
            }
            else if( y > other.y )
            {
                return 1;
            }
            else
            {
                if ( x < other.x )
                {
                    return -1;
                }
                else if( x > other.x )
                {
                    return 1;
                }
                else
                {
                    return 0;
                }
            }
        }
    }

    private final boolean starsMode;
    private final char[][] impl;
    private final Map<Point, String> stars;

    public Chars( World world, boolean starsMode )
    {
        this.starsMode = starsMode;
        this.impl = new char[world.size.height][world.size.width];
        this.stars = new TreeMap<Point, String>();

        for( int i = 0; i < world.size.height; ++i )
        {
            Arrays.fill( impl[i], ' ' );
        }
    }

    public void set( int x, int y, char ch )
    {
        char currentCh = impl[y][x];

        if ( currentCh == ' ' || !starsMode )
        {
            impl[y][x] = ch;
        }
        else
        {
            Point p = new Point( x, y );

            String starString = stars.get( p );
            if ( starString == null )
            {
                starString = "";
            }
            if ( currentCh != '*' )
            {
                starString += currentCh;
                impl[y][x] = '*';
            }
            starString += ch;

            stars.put( p, starString );
        }
    }

    public int numRows()
    {
        return impl.length;
    }

    public int numCols()
    {
        return impl[0].length;
    }

    public char[] line( int lineNum )
    {
        return impl[ lineNum ];
    }

    public List<String> starLines()
    {
        List<String> ret = new ArrayList<String>();

        for ( Map.Entry<Point, String> e : stars.entrySet() )
        {
            ret.add( e.getValue() );
        }

        return ret;
    }
}

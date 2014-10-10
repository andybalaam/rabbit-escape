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
        set( x, y, ch, null );
    }

    public void set( int x, int y, char ch, Map<String, String> state )
    {
        String thisState = encodeState( state );

        char currentCh = impl[y][x];

        if ( !starsMode || ( thisState == "" && ( currentCh == ' ' ) ) )
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
            impl[y][x] = '*';
            if ( currentCh != '*' && currentCh != ' ' )
            {
                starString += currentCh;
            }
            starString += ch + thisState;

            stars.put( p, starString );
        }
    }

    private String encodeState( Map<String, String> state )
    {
        if ( state == null || state.size() == 0 )
        {
            return "";
        }
        else
        {
            StringBuilder ret = new StringBuilder();

            ret.append( '{' );
            boolean begin = true;
            for ( Map.Entry<String, String> e : new TreeMap<String, String>( state ).entrySet() )
            {
                if ( !begin )
                {
                    ret.append( ',' );
                }
                else
                {
                    begin = false;
                }
                ret.append( e.getKey() );
                ret.append( ':' );
                ret.append( e.getValue() );
            }
            ret.append( '}' );

            return ret.toString();
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

package rabbitescape.engine.util;

import java.io.PrintStream;
import java.util.ArrayList;

/**
 * Formats a table of debug output.
 */
public class CellDebugPrint
{
    private final ArrayList<ArrayList<ArrayList<String>>> strings;

    private static int maxLength = 0, maxX = 0, maxY = 0, maxI = 0;
    private static boolean off = false;
    
    public CellDebugPrint()
    {
        strings = new ArrayList<ArrayList<ArrayList<String>>>();;
    }

    public void addString( int x, int y, int i, String s )
    {
        x++; y++; // Offset so -1 can be shown
        if ( x < 0 || y < 0 || i < 0 )
        {
            return;
        }
        while ( strings.size() <= x )
        {
            strings.add( new  ArrayList<ArrayList<String>>() );
        }
        while ( strings.get( x ).size() <= y )
        {
            strings.get( x ).add( new ArrayList<String>() );
        }
        while ( strings.get( x ).get(  y ).size() <= i )
        {
            strings.get( x ).get( y ).add( "");
        }
        strings.get( x ).get( y ).set( i, s );

        maxX = maxX > x ? maxX : x;
        maxY = maxY > y ? maxY : y;
        maxI = maxI > i ? maxI : i;
        maxLength = maxLength > s.length() ? maxLength : s.length();
    }

    public void addString( LookupItem2D l, int i, String s )
    {
        Position p = l.getPosition();
        addString( p.x, p.y, i, s);
    }

    public void addString( LookupItem2D l, int i, String f, Object... args )
    {
        Position p = l.getPosition();
        addString( p.x, p.y, i, String.format( f, args ));
    }

    public void print()
    {
        if ( off )
        {
            return;
        }
        print( System.out );
    }

    public void print( PrintStream s)
    {
        for ( int y = 0; y <= maxY; y++)
        {
            for ( int i = 0; i <= maxI; i++ )
            {
                for ( int x = 0; x <= maxX; x++ )
                {
                    s.print( stringAt( x, y, i ) + "|" );
                }
            s.println();
            }
        s.println( "-" );
        }
    }

    private String stringAt( int x, int y, int i )
    {
        if ( strings.size() <= x )
        {
            return formatString( "" );
        }
        if ( strings.get( x ).size() <= y )
        {
            return formatString( "" );
        }
        if ( strings.get( x ).get( y ).size() <= i )
        {
            return formatString( "" );
        }
        return formatString( strings.get( x ).get( y ).get( i ) );
    }

    private static String formatString( String s )
    {
        if ( maxLength == 0 )
        {
            return "";
        }
        return String.format( "%" + maxLength + "s", s );
    }
}

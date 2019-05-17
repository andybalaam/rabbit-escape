package rabbitescape.engine.textworld;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import rabbitescape.engine.World;
import rabbitescape.engine.util.Position;
import rabbitescape.engine.util.WaterUtil;

public class Chars
{

    private final boolean starsMode;
    private final char[][] impl;
    private final Map<Position, String> stars;
    private final int worldWidth, worldHeight;
    private final Map<Position, String> waterAmounts;

    public Chars( World world, boolean starsMode )
    {
        this.starsMode = starsMode;
        this.worldWidth = world.size.width;
        this.worldHeight = world.size.height;
        this.impl = new char[worldHeight][worldWidth];
        this.stars = new TreeMap<Position, String>();
        this.waterAmounts = new TreeMap<Position, String>();

        for( int i = 0; i < world.size.height; ++i )
        {
            Arrays.fill( impl[i], ' ' );
        }
    }

    public void set( int x, int y, char ch )
    {
        if ( // Rabbits may try to build bridges out of bounds
                x <  0
             || y <  0
             || x >= worldWidth
             || y >= worldHeight
           )
        {
            return;
        }
        set( x, y, ch, null );
    }

    public void set( int x, int y, char ch, Map<String, String> state )
    {
        set( x, y, ch, state, 0 );
    }

    public void set( 
        int x, 
        int y, 
        char ch, 
        Map<String, String> state,
        int waterAmount 
    )
    {
        String thisState = encodeState( state );

        char currentCh = impl[y][x];

        if ( !starsMode || ( thisState == "" && ( currentCh == ' ' ) ) )
        {
            impl[y][x] = ch;
        }
        else
        {
            Position p = new Position( x, y );

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

        if ( waterAmount > 0
            && ( ( ch == 'n' && waterAmount != WaterUtil.HALF_CAPACITY )
                || ( ch == 'N' && waterAmount != WaterUtil.MAX_CAPACITY ) ) )
        {
            Position p = new Position( x, y );
            waterAmounts.put( p, x + "," + y + "," + waterAmount );
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
            for ( Map.Entry<String, String> e : new TreeMap<String,
                  String>( state ).entrySet() )
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

        for ( Map.Entry<Position, String> e : stars.entrySet() )
        {
            ret.add( e.getValue() );
        }

        return ret;
    }

    public List<String> waterAmountLines()
    {
        List<String> ret = new ArrayList<String>();

        for ( String amountString : waterAmounts.values() )
        {
            ret.add( amountString );
        }

        return ret;
    }
}

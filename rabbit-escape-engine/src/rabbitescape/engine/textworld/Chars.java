package rabbitescape.engine.textworld;

import java.util.Arrays;

import rabbitescape.engine.World;

public class Chars
{
    final char[][] impl;

    public Chars( World world )
    {
        impl = new char[world.size.height][world.size.width];

        for( int i = 0; i < world.size.height; ++i )
        {
            Arrays.fill( impl[i], ' ' );
        }
    }

    public void set( int x, int y, char ch )
    {
        impl[y][x] = ch;
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
}

package rabbitescape;

import static rabbitescape.Direction.*;

public class Rabbit extends Thing
{
    Direction dir;

    public Rabbit( int x, int y, Direction dir )
    {
        super( x, y );
        this.dir = dir;
    }

    @Override
    public void step( World world )
    {
        if ( !fall( world ) )
        {
            walk( world );
        }
    }

    /**
     * Fall if possible.
     * @return true if we are falling
     */
    private boolean fall( World world )
    {
        int below = y + 1;

        if ( blockAt( world, x, below ) )
        {
            return false;
        }

        int furtherBelow = y + 2;

        if ( blockAt( world, x, furtherBelow ) )
        {
            // Only fall 1 step
            y = below;
        }
        else
        {
            y = furtherBelow;
        }

        return true;
    }

    private void walk( World world )
    {
        int destination = ( dir == RIGHT ) ? x + 1 : x - 1;

        if ( blockAt( world, destination, y ) )
        {
            dir = opposite( dir );
        }
        else
        {
            x = destination;
        }
    }

    private static boolean blockAt( World world, int x, int y )
    {
        for ( Block block : world.blocks )
        {
            if ( block.x == x && block.y == y )
            {
                return true;
            }
        }
        return false;
    }
}

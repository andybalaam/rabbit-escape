package rabbitescape.engine.textworld;

import static rabbitescape.engine.Direction.*;

import java.util.List;

import rabbitescape.engine.Entrance;
import rabbitescape.engine.Exit;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.Thing;

public class ThingRenderer
{
    public static void render( char[][] chars, List<Thing> things )
    {
        for ( Thing thing : things )
        {
            if ( thing.alive )
            {
                chars[ thing.y ][ thing.x ] = charForThing( thing );
            }
        }
    }

    private static char charForThing( Thing thing )
    {
        if ( thing instanceof Rabbit )
        {
            return ( (Rabbit)thing ).dir == RIGHT ? 'r' : 'j';
        }
        else if ( thing instanceof Entrance )
        {
            return 'Q';
        }
        else if ( thing instanceof Exit )
        {
            return 'O';
        }
        else
        {
            throw new AssertionError(
                "Unknown Thing type: " + thing.getClass() );
        }
    }
}

package rabbitescape.engine.textworld;

import java.util.List;

import rabbitescape.engine.Entrance;
import rabbitescape.engine.Exit;
import rabbitescape.engine.Thing;
import rabbitescape.engine.Token;

public class ThingRenderer
{
    public static void render( char[][] chars, List<Thing> things )
    {
        for ( Thing thing : things )
        {
            chars[ thing.y ][ thing.x ] = charForThing( thing );
        }
    }

    private static char charForThing( Thing thing )
    {
        if ( thing instanceof Entrance )
        {
            return 'Q';
        }
        else if ( thing instanceof Exit )
        {
            return 'O';
        }
        else if ( thing instanceof Token )
        {
            return charForToken( (Token)thing );
        }
        else
        {
            throw new AssertionError(
                "Unknown Thing type: " + thing.getClass() );
        }
    }

    private static char charForToken( Token thing )
    {
        return 'b';
    }
}

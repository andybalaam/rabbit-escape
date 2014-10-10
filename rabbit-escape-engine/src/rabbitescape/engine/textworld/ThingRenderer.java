package rabbitescape.engine.textworld;

import java.util.List;

import rabbitescape.engine.Entrance;
import rabbitescape.engine.Exit;
import rabbitescape.engine.Thing;
import rabbitescape.engine.Token;
import rabbitescape.engine.Token.Type;
import rabbitescape.engine.err.RabbitEscapeException;

public class ThingRenderer
{
    public static class UnknownTokenType extends RabbitEscapeException
    {
        public final Type type;

        public UnknownTokenType( Type type )
        {
            this.type = type;
        }

        private static final long serialVersionUID = 1L;
    }

    public static void render( Chars chars, List<Thing> things )
    {
        for ( Thing thing : things )
        {
            chars.set(
                thing.x, thing.y, charForThing( thing ), thing.saveState() );
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
        switch ( thing.type )
        {
            case bash:   return 'b';
            case dig:    return 'd';
            case bridge: return 'i';
            default: throw new UnknownTokenType( thing.type );
        }
    }
}

package rabbitescape.engine;

import static rabbitescape.engine.ChangeDescription.State.*;
import rabbitescape.engine.ChangeDescription.State;
import rabbitescape.engine.err.RabbitEscapeException;

public class Token extends Thing
{
    public static class UnknownType extends RabbitEscapeException
    {
        public final Type type;

        public UnknownType( Type type )
        {
            this.type = type;
        }

        private static final long serialVersionUID = 1L;
    }

    public static enum Type
    {
        bash,
        dig,
    }

    public final Type type;

    public Token( int x, int y, Type type )
    {
        super( x, y, state( type, false ) );
        this.type = type;
    }

    private static State state( Type type, boolean moving )
    {
        switch( type )
        {
            case bash: return moving ? TOKEN_BASH_STILL : TOKEN_BASH_FALLING;
            case dig:  return moving ? TOKEN_DIG_STILL  : TOKEN_DIG_FALLING;
            default: throw new UnknownType( type );
        }
    }

    @Override
    public void calcNewState( World world )
    {
        state = state( type, world.flatBlockAt( x, y + 1 ) );
    }

    @Override
    public void step( World world )
    {
        if ( state == TOKEN_BASH_FALLING || state == TOKEN_DIG_FALLING )
        {
            ++y;
        }
    }
}

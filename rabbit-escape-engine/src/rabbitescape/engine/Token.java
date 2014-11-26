package rabbitescape.engine;

import static rabbitescape.engine.ChangeDescription.State.*;

import java.util.HashMap;
import java.util.Map;

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
        bridge,
        block,
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
            case bash:   return moving ?
                TOKEN_BASH_FALLING : TOKEN_BASH_STILL;

            case dig:    return moving ?
                TOKEN_DIG_FALLING : TOKEN_DIG_STILL;

            case bridge: return moving ?
                TOKEN_BRIDGE_FALLING : TOKEN_BRIDGE_STILL;

            case block: return moving ?
                TOKEN_BLOCK_FALLING : TOKEN_BLOCK_STILL;

            default: throw new UnknownType( type );
        }
    }

    @Override
    public void calcNewState( World world )
    {
        boolean still = (
               world.flatBlockAt( x, y + 1 )
            || ( world.getBlockAt( x, y ) != null )
        );

        state = state( type, !still );
    }

    @Override
    public void step( World world )
    {
        if (
               state == TOKEN_BASH_FALLING
            || state == TOKEN_DIG_FALLING
            || state == TOKEN_BRIDGE_FALLING
            || state == TOKEN_BLOCK_FALLING
        )
        {
            ++y;
        }
    }

    @Override
    public Map<String, String> saveState()
    {
        return new HashMap<String, String>();
    }

    @Override
    public void restoreFromState( Map<String, String> state )
    {
    }
}

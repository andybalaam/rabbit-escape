package rabbitescape.engine;

import static rabbitescape.engine.ChangeDescription.State.*;

import java.util.HashMap;
import java.util.Map;

import rabbitescape.engine.ChangeDescription.State;
import rabbitescape.engine.err.RabbitEscapeException;
import rabbitescape.engine.util.Position;

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
        climb,
        explode,
        brolly
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

            case climb: return moving ?
                TOKEN_CLIMB_FALLING : TOKEN_CLIMB_STILL;

            case explode: return moving ?
                TOKEN_EXPLODE_FALLING : TOKEN_EXPLODE_STILL;

            case brolly: return moving ?
                TOKEN_BROLLY_FALLING : TOKEN_BROLLY_STILL;
            
            default: throw new UnknownType( type );
        }
    }

    @Override
    public void calcNewState( World world )
    {
        boolean still = (
               BehaviourTools.s_isFlat( world.getBlockAt( x, y + 1 ) )
            || ( world.getBlockAt( x, y ) != null )
            || someoneIsBridgingAt( world, x, y )
        );

        state = state( type, !still );
    }

    private static boolean someoneIsBridgingAt( World world, int x, int y )
    {
        for( Rabbit rabbit: world.rabbits )
        {
            if ( rabbitIsBridgingAt( rabbit, x, y ) )
            {
                return true;
            }
        }
        return false;
    }

    private static boolean rabbitIsBridgingAt( Rabbit rabbit, int x, int y )
    {
        Position bridging = RabbitStates.whereBridging(
            new StateAndPosition( rabbit.state, rabbit.x, rabbit.y ) );

        if ( bridging == null )
        {
            return false;
        }
        else
        {
            return ( bridging.x == x && bridging.y == y );
        }
    }

    @Override
    public void step( World world )
    {
        if (
               state == TOKEN_BASH_FALLING
            || state == TOKEN_DIG_FALLING
            || state == TOKEN_BRIDGE_FALLING
            || state == TOKEN_BLOCK_FALLING
            || state == TOKEN_CLIMB_FALLING
            || state == TOKEN_EXPLODE_FALLING
            || state == TOKEN_BROLLY_FALLING
        )
        {
            ++y;

            if ( y >= world.size.height )
            {
                world.changes.removeToken( this );
            }
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

    public static String name( Type ability )
    {
        String n = ability.name();
        return n.substring( 0, 1 ).toUpperCase() + n.substring( 1 );
    }
    
    @Override
    public String toString()
    {
        return type.toString();
    }
}

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
        climb,
        explode,
        brolly,
        breakblock //gyh
    }

    public final Type type;

    public Token( int x, int y, Type type )
    {
        super( x, y, switchType( type, false, false, true ) );
        this.type = type;
    }

    public Token( int x, int y, Type type, World world )
    {
        this( x, y, type );
        boolean onSlope = BehaviourTools.isSlope( world.getBlockAt( x, y ) );
        // Can't use calcNewState here since we have just been created, so
        // can't be moving (until a time step passes).
        state = switchType( type, false, false, onSlope );
    }

    private static State switchType(  //gyh주석 : Thing-Token과 Behaviour-bash,dig...을 이어주는 메소드
        Type type, 
        boolean moving,
        boolean slopeBelow, 
        boolean onSlope 
    )
    {
        switch( type )
        {
            case bash:   return chooseState( 
                moving, 
                slopeBelow, 
                onSlope,
                TOKEN_BASH_FALLING, 
                TOKEN_BASH_STILL,
                TOKEN_BASH_FALL_TO_SLOPE, 
                TOKEN_BASH_ON_SLOPE
                );

            case dig:    return chooseState( 
                moving, 
                slopeBelow, 
                onSlope,
                TOKEN_DIG_FALLING, 
                TOKEN_DIG_STILL,
                TOKEN_DIG_FALL_TO_SLOPE, 
                TOKEN_DIG_ON_SLOPE
                );

            case bridge: return chooseState( 
                moving, 
                slopeBelow, 
                onSlope,
                TOKEN_BRIDGE_FALLING, 
                TOKEN_BRIDGE_STILL,
                TOKEN_BRIDGE_FALL_TO_SLOPE, 
                TOKEN_BRIDGE_ON_SLOPE
                );

            case block: return chooseState( 
                moving, 
                slopeBelow, 
                onSlope,
                TOKEN_BLOCK_FALLING, 
                TOKEN_BLOCK_STILL,
                TOKEN_BLOCK_FALL_TO_SLOPE, 
                TOKEN_BLOCK_ON_SLOPE
                );

            case climb: return chooseState( 
                moving, 
                slopeBelow, 
                onSlope,
                TOKEN_CLIMB_FALLING, 
                TOKEN_CLIMB_STILL,
                TOKEN_CLIMB_FALL_TO_SLOPE, 
                TOKEN_CLIMB_ON_SLOPE
                );

            case explode: return chooseState( 
                moving, 
                slopeBelow, 
                onSlope,
                TOKEN_EXPLODE_FALLING, 
                TOKEN_EXPLODE_STILL,
                TOKEN_EXPLODE_FALL_TO_SLOPE, 
                TOKEN_EXPLODE_ON_SLOPE)
                ;

            case brolly: return chooseState( 
                moving, 
                slopeBelow, 
                onSlope,
                TOKEN_BROLLY_FALLING, 
                TOKEN_BROLLY_STILL,
                TOKEN_BROLLY_FALL_TO_SLOPE, 
                TOKEN_BROLLY_ON_SLOPE
                );
            case breakblock: return chooseState( //gyh
                 moving,
                 slopeBelow,
                 onSlope,
                 TOKEN_BREAKBLOCK_FALLING,
                 TOKEN_BREAKBLOCK_STILL,
                 TOKEN_BREAKBLOCK_FALL_TO_SLOPE,
                 TOKEN_BREAKBLOCK_ON_SLOPE);

            default: throw new UnknownType( type );
        }
    }

    private static State chooseState( 
        boolean moving, 
        boolean slopeBelow,
        boolean onSlope, 
        State falling,
        State onFlat, 
        State fallingToSlope,
        State onSlopeState
    )
    {
        if ( onSlope )
        {
            return onSlopeState;
        }
        if ( !moving )
        {
            return onFlat;
        }
        if ( slopeBelow )
        {
            return fallingToSlope;
        }
        return falling;
    }

    @Override
    public void calcNewState( World world )
    {
        Block onBlock = world.getBlockAt( x, y );
        Block belowBlock = world.getBlockAt( x, y + 1 );
        boolean still = (
               BehaviourTools.s_isFlat( belowBlock )
            || ( onBlock != null )
            || BridgeTools.someoneIsBridgingAt( world, x, y )
        );

        state = switchType( type, !still,
            BehaviourTools.isSlope( belowBlock ),
            BehaviourTools.isSlope( onBlock ) );
    }

    @Override
    public void step( World world ) //gyh 주석 : step단위가 지날때마다 토큰의 위치 조정
    {
        switch ( state )
        {
        case TOKEN_BASH_FALLING:
        case TOKEN_BASH_FALL_TO_SLOPE:
        case TOKEN_DIG_FALLING:
        case TOKEN_DIG_FALL_TO_SLOPE:
        case TOKEN_BRIDGE_FALLING:
        case TOKEN_BRIDGE_FALL_TO_SLOPE:
        case TOKEN_BLOCK_FALLING:
        case TOKEN_BLOCK_FALL_TO_SLOPE:
        case TOKEN_CLIMB_FALLING:
        case TOKEN_CLIMB_FALL_TO_SLOPE:
        case TOKEN_EXPLODE_FALL_TO_SLOPE:
        case TOKEN_EXPLODE_FALLING:
        case TOKEN_BROLLY_FALLING:
        case TOKEN_BROLLY_FALL_TO_SLOPE:
        case TOKEN_BREAKBLOCK_FALLING: //gyh
        case TOKEN_BREAKBLOCK_FALL_TO_SLOPE:
        {
            ++y;
            System.out.println(state);

            if ( y >= world.size.height )
            {
                world.changes.removeToken( this );
            }

            return;
        }


        case TOKEN_BREAKBLOCK_ON_SLOPE:
        case TOKEN_BREAKBLOCK_STILL: { //gyh
            Block belowBlock = world.getBlockAt( x, y + 1 );
            Block onBlock = world.getBlockAt( x, y );

            if(onBlock != null) { //gyh 주석 : on, below를 구분하지 않으면, 블록 안쪽에 토큰을 겹치게 소환한 경우, on을 안하면 그 아래만 지워짐



                world.changes.removeBlockAt( x, y );
                world.changes.removeToken( this );
                return;
            }
            if(belowBlock != null) {



                world.changes.removeBlockAt( x, y + 1 );
                world.changes.removeToken( this );
            }

        }
        default:
            // Nothing to do
        }
    }

    @Override
    public Map<String, String> saveState( boolean runtimeMeta )
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
    public String overlayText()
    {
        return type.toString();
    }
}

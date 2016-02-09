package rabbitescape.engine;

import static rabbitescape.engine.ChangeDescription.State.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import rabbitescape.engine.ChangeDescription.State;
import rabbitescape.engine.util.Position;

public class Fire extends Thing
{
    private final State baseVariant;

    public Fire( int x, int y )
    {
        super( x, y, chooseVariant() );
        baseVariant = state;
    }

    private static State chooseVariant()
    {
        int r = ThreadLocalRandom.current().nextInt( 0, 4 );
        switch ( r )
        {
        case 0:
            return FIRE_A;
        case 1:
            return FIRE_B;
        case 2:
            return FIRE_C;
        case 3:
            return FIRE_D;
        }
        throw new RuntimeException(
            "Random number outside expected range (0 - 3):" + r );
    }

    @Override
    public void calcNewState( World world )
    {
        boolean onFlat = BehaviourTools.s_isFlat( world.getBlockAt( x, y + 1 ) ); 
        boolean still = (
                   onFlat
                || ( world.getBlockAt( x, y ) != null )
                || someoneIsBridgingAt( world, x, y )
            );
        
        if ( still )
        {
            Block onBlock = world.getBlockAt( x, y );
            if ( BehaviourTools.isLeftRiseSlope( onBlock ) )
            {
                switch ( baseVariant )
                {
                case FIRE_A:
                    state = FIRE_A_RISE_LEFT;
                    return;
                case FIRE_B:
                    state = FIRE_B_RISE_LEFT;
                    return;
                case FIRE_C:
                    state = FIRE_C_RISE_LEFT;
                    return;
                case FIRE_D:
                    state = FIRE_D_RISE_LEFT;
                    return;
                default:
                    throw new RuntimeException( "Fire not in fire state:" + state );
                }
            }
            if ( BehaviourTools.isRightRiseSlope( onBlock ) )
            {
                switch ( baseVariant )
                {
                case FIRE_A:
                    state = FIRE_A_RISE_RIGHT;
                    return;
                case FIRE_B:
                    state = FIRE_B_RISE_RIGHT;
                    return;
                case FIRE_C:
                    state = FIRE_C_RISE_RIGHT;
                    return;
                case FIRE_D:
                    state = FIRE_D_RISE_RIGHT;
                    return;
                default:
                    throw new RuntimeException( "Fire not in fire state:" + state );
                }
            }
            // TODO: check here for fire falling on a bridger. Fire going to a falling state may be OK
            // as bridger is burnt
            if ( onFlat )
            {
                state = baseVariant;
                return;
            }
        }

        // Fire is falling, but may not have been last step
        switch ( baseVariant )
        {
        case FIRE_A:
            state = FIRE_A_FALLING;
            return;
        case FIRE_B:
            state = FIRE_B_FALLING;
            return;
        case FIRE_C:
            state = FIRE_C_FALLING;
            return;
        case FIRE_D:
            state = FIRE_D_FALLING;
            return;
        default:
            throw new RuntimeException( "Fire not in fire state:" + state );
        }
    }

    private static boolean someoneIsBridgingAt( World world, int x, int y )
    {
        for ( Rabbit rabbit : world.rabbits )
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
        switch ( state )
        {
        case FIRE_A_FALLING:
        case FIRE_B_FALLING:
        case FIRE_C_FALLING:
        case FIRE_D_FALLING:
            ++y;

            if ( y >= world.size.height )
            {
                world.changes.removeFire( this );
            }
            return;
        default:
            return;
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

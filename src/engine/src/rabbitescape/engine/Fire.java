package rabbitescape.engine;

import static rabbitescape.engine.ChangeDescription.State.*;

import java.util.HashMap;
import java.util.Map;

import rabbitescape.engine.ChangeDescription.State;

public class Fire extends Thing
{
    public int variant;

    private final State baseVariant;

    public Fire( int x, int y, int variant )
    {
        super( x, y, stateForVariant( variant ) );
        this.variant = variant;
        baseVariant = state;
    }

    private static State stateForVariant( int variant )
    {
        switch ( variant )
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
            "Variant outside expected range (0 - 3):" + variant );
    }

    @Override
    public void calcNewState( World world )
    {
        // Check if being extinguished.
        for ( WaterRegion waterRegion : world.waterTable.getItemsAt( x, y ) )
        {
            if ( waterRegion.getContents() > 0 )
            {
                state = FIRE_EXTINGUISHING;
                return;
            }
        }

        Block blockBelow = world.getBlockAt( x, y + 1 );
        // Note: when flatBelow is true may be on a slope with a flat below,
        // or sitting on the flat
        boolean flatBelow = BehaviourTools.s_isFlat( blockBelow );
        boolean still = (
                   flatBelow
                || ( world.getBlockAt( x, y ) != null )
                || BridgeTools.someoneIsBridgingAt( world, x, y )
            );
        if ( still )
        {
            Block onBlock = world.getBlockAt( x, y );
            if ( BehaviourTools.isLeftRiseSlope( onBlock ) )
            {
                state = baseVariantSwitch( 
                    FIRE_A_RISE_LEFT, 
                    FIRE_B_RISE_LEFT,     
                    FIRE_C_RISE_LEFT, 
                    FIRE_D_RISE_LEFT 
                );
                return;
            }
            if ( BehaviourTools.isRightRiseSlope( onBlock ) )
            {
                state = baseVariantSwitch( 
                    FIRE_A_RISE_RIGHT, 
                    FIRE_B_RISE_RIGHT,
                    FIRE_C_RISE_RIGHT, 
                    FIRE_D_RISE_RIGHT 
                );
                return;
            }
            // TODO: check here for fire falling on a bridger.
            // Fire going to a falling state may be OK
            // as bridger is burnt
            if ( flatBelow )
            {
                state = baseVariant;
                return;
            }
        }
        else // Falling
        {
            if ( BehaviourTools.isLeftRiseSlope( blockBelow ) )
            {
                state = baseVariantSwitch( 
                    FIRE_A_FALL_TO_RISE_LEFT,
                    FIRE_B_FALL_TO_RISE_LEFT,
                    FIRE_C_FALL_TO_RISE_LEFT,
                    FIRE_D_FALL_TO_RISE_LEFT 
                );
                return;
            }
            if ( BehaviourTools.isRightRiseSlope( blockBelow ) )
            {
                state = baseVariantSwitch( 
                    FIRE_A_FALL_TO_RISE_RIGHT,
                    FIRE_B_FALL_TO_RISE_RIGHT,
                    FIRE_C_FALL_TO_RISE_RIGHT,
                    FIRE_D_FALL_TO_RISE_RIGHT 
                );
                return;
            }
            state = baseVariantSwitch( 
                FIRE_A_FALLING, 
                FIRE_B_FALLING,       
                FIRE_C_FALLING, 
                FIRE_D_FALLING 
            );
            return;
        }
    }

    private State baseVariantSwitch( State a, State b, State c, State d )
    {
        switch ( baseVariant )
        {
        case FIRE_A:
            return a;
        case FIRE_B:
            return b;
        case FIRE_C:
            return c;
        case FIRE_D:
            return d;
        default:
            throw new RuntimeException( "Fire not in fire state:" + state );
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
        case FIRE_A_FALL_TO_RISE_RIGHT:
        case FIRE_B_FALL_TO_RISE_RIGHT:
        case FIRE_C_FALL_TO_RISE_RIGHT:
        case FIRE_D_FALL_TO_RISE_RIGHT:
        case FIRE_A_FALL_TO_RISE_LEFT:
        case FIRE_B_FALL_TO_RISE_LEFT:
        case FIRE_C_FALL_TO_RISE_LEFT:
        case FIRE_D_FALL_TO_RISE_LEFT:
            ++y;

            if ( y >= world.size.height )
            {
                world.changes.removeFire( this );
            }
            return;
        case FIRE_EXTINGUISHING:
            world.changes.removeFire( this );
            return;
        default:
            return;
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

    @Override
    public String overlayText()
    {
        return "Fire";
    }
}

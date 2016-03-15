package rabbitescape.engine.textworld;

import rabbitescape.engine.WaterRegion;

public class WaterRenderer
{
    public static
        void
        render( Chars chars, Iterable<WaterRegion> waterTable )
    {
        for ( WaterRegion waterRegion : waterTable )
        {
            switch ( waterRegion.state )
            {
            case WATER_REGION:
                chars.set( waterRegion.x, waterRegion.y, 'N' );
                break;
            case WATER_REGION_HALF:
                chars.set( waterRegion.x, waterRegion.y, 'n' );
                break;
            case WATER_REGION_FALLING:
                chars.set( waterRegion.x, waterRegion.y, 'n' );
                break;
            case WATER_REGION_EMPTY:
                break;
            default:
                throw new AssertionError(
                    "Unknown WaterRegion state: " + waterRegion.state );
            }
        }
    }
}

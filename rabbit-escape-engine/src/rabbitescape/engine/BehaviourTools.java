package rabbitescape.engine;

import static rabbitescape.engine.Direction.RIGHT;
import static rabbitescape.engine.Token.Type.climb;

public class BehaviourTools
{
    private final Rabbit rabbit;
    private final World world;

    public BehaviourTools( Rabbit rabbit, World world )
    {
        this.rabbit = rabbit;
        this.world = world;
    }

    public ChangeDescription.State rl(
        ChangeDescription.State rightState,
        ChangeDescription.State leftState
    )
    {
        return rabbit.dir == RIGHT ? rightState : leftState;
    }

    public boolean pickUpToken( Token.Type type )
    {
        return pickUpToken( type, false );
    }

    public boolean pickUpToken( Token.Type type, boolean evenIfNotOnGround )
    {
        if ( evenIfNotOnGround || onGround() )
        {
            Token token = world.getTokenAt( rabbit.x, rabbit.y );
            if ( token != null && token.type == type )
            {
                world.changes.removeToken( token );
                return true;
            }
        }
        return false;
    }

    private boolean onGround()
    {
        return ( rabbit.onSlope || blockBelow() != null );
    }

    private Block blockBelow()
    {
        return world.getBlockAt( rabbit.x, rabbit.y + 1 );
    }
}

package rabbitescape.engine;

import static rabbitescape.engine.Block.Type.solid_flat;
import static rabbitescape.engine.Block.Type.solid_up_left;
import static rabbitescape.engine.Block.Type.solid_up_right;
import static rabbitescape.engine.Direction.RIGHT;
import static rabbitescape.engine.Direction.opposite;

import java.awt.Point;

import rabbitescape.engine.ChangeDescription.State;

public class BehaviourTools
{
    public final Rabbit rabbit;
    public final World world;

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

    public boolean rabbitIsFallingToDeath()
    {
        return
               State.RABBIT_FALLING_1_TO_DEATH == rabbit.state
            || State.RABBIT_DYING_OF_FALLING_2 == rabbit.state
            || State.RABBIT_DYING_OF_FALLING == rabbit.state ;
    }
    
    public boolean pickUpToken( Token.Type type, boolean evenIfNotOnGround )
    {
        if ( rabbitIsFallingToDeath() )
        {
            return false; // Dying rabbits not allowed to consume tokens
        }

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

    public Block blockHere()
    {
        return world.getBlockAt( rabbit.x, rabbit.y );
    }

    public Block blockNext()
    {
        return world.getBlockAt( nextX(), rabbit.y );
    }

    public Block blockBelow()
    {
        return world.getBlockAt( rabbit.x, rabbit.y + 1 );
    }

    public Block block2Below()
    {
        return world.getBlockAt( rabbit.x, rabbit.y + 2 );
    }

    public Block blockBelowNext()
    {
        return world.getBlockAt( nextX(), rabbit.y + 1 );
    }

    public Block blockAbove()
    {
        return world.getBlockAt( rabbit.x, rabbit.y - 1 );
    }

    public Block blockAboveNext()
    {
        return world.getBlockAt( nextX(), rabbit.y - 1 );
    }

    private boolean onGround()
    {
        return ( rabbit.onSlope || blockBelow() != null );
    }

    public boolean isWall( Block block )
    {
        return (
               block != null
            && (
                   block.type == solid_flat
                || (
                    block.riseDir() == opposite( rabbit.dir )
                    && isSolid( block )
                )
            )
        );
    }

    private boolean isSolid( Block block )
    {
        return (
               block.type == solid_flat
            || block.type == solid_up_left
            || block.type == solid_up_right
        );
    }

    public boolean isRoof( Block block )
    {
        return
            (
                block != null
                && (
                       block.type == solid_flat
                    || block.type == solid_up_left
                    || block.type == solid_up_right
                )
            );
    }

    public boolean isOnSlopeStateUnreliable()
    {
        Block block = blockHere();
        return
            null != block &&
            (
                   solid_up_left == block.type
                || solid_up_right == block.type
                || Block.Type.bridge_up_left == block.type
                || Block.Type.bridge_up_right == block.type
            );
    }

    public boolean isFlat( Block block )
    {
        return s_isFlat( block );
    }

    public static boolean s_isFlat( Block block )
    {
        return ( block != null && block.type == solid_flat );
    }

    private boolean goingUpSlope()
    {
        if ( rabbit.onSlope )
        {
            if( isOnUpSlope() )
            {
                return true;
            }
        }
        return false;
    }

    public boolean isOnUpSlope()
    {
        return rabbit.onSlope && hereIsUpSlope();
    }

    public boolean hereIsUpSlope()
    {
        return isUpSlope( blockHere() );
    }

    public boolean isUpSlope( Block block )
    {
        return ( block != null && block.riseDir() == rabbit.dir );
    }

    public boolean isOnDownSlope()
    {
        return rabbit.onSlope && hereIsDownSlope();
    }

    private boolean hereIsDownSlope()
    {
        return isDownSlope( blockHere() );
    }

    public boolean isDownSlope( Block block )
    {
        return ( block != null && block.riseDir() == opposite( rabbit.dir ) );
    }

    public int nextX()
    {
        return
            rabbit.x + (
                rabbit.dir == RIGHT ? 1 : -1
            );
    }

    public int nextY()
    {
        if ( goingUpSlope() )
        {
            return rabbit.y - 1;
        }
        else
        {
            return rabbit.y;
        }
    }

    /**
     * @brief A rabbit may be on a slope block as a digger
     *        or basher removes it. This is here to make sure
     *        they fall.
     */
    public boolean blockHereJustRemoved()
    {
        for ( Point p : world.changes.blocksJustRemoved )
        {
            if ( rabbit.x == p.x && rabbit.y == p.y )
            {
                return true;
            }
        }
        return false;
    }
}

package rabbitescape.engine.behaviours;

import static rabbitescape.engine.ChangeDescription.State.*;
import static rabbitescape.engine.Direction.*;
import static rabbitescape.engine.Token.Type.*;

import java.util.Map;

import rabbitescape.engine.*;
import rabbitescape.engine.ChangeDescription.State;

public class Climbing extends Behaviour
{
    boolean hasAbility = false;
    public boolean abilityActive = false;

    @Override
    public void cancel()
    {
        abilityActive = false;
    }

    @Override
    public boolean checkTriggered( Rabbit rabbit, World world )
    {
        BehaviourTools t = new BehaviourTools( rabbit, world );

        return !hasAbility && t.pickUpToken( climb, true );
    }

    @Override
    public State newState( BehaviourTools t, boolean triggered )
    {
        if ( triggered )
        {
            hasAbility = true;
        }

        if ( !hasAbility )
        {
            return null;
        }

        switch ( t.rabbit.state )
        {
            case RABBIT_CLIMBING_RIGHT_START:
            case RABBIT_CLIMBING_LEFT_START:
                return newStateStart( t );
            case RABBIT_CLIMBING_RIGHT_CONTINUE_1:
            case RABBIT_CLIMBING_LEFT_CONTINUE_1:
                return newStateCont1( t );
            case RABBIT_CLIMBING_RIGHT_CONTINUE_2:
            case RABBIT_CLIMBING_LEFT_CONTINUE_2:
                return newStateCont2( t );
            default:
                return newStateNotClimbing( t );
        }
    }

    private State newStateStart( BehaviourTools t )
    {
        Block endBlock = t.blockAboveNext();

        if ( t.isWall( endBlock ) )
        {
            return t.rl(
                RABBIT_CLIMBING_RIGHT_CONTINUE_2,
                RABBIT_CLIMBING_LEFT_CONTINUE_2
            );
        }
        else
        {
            return t.rl(
                RABBIT_CLIMBING_RIGHT_END,
                RABBIT_CLIMBING_LEFT_END
            );
        }
    }

    private State newStateCont1( BehaviourTools t )
    {
        return t.rl(
            RABBIT_CLIMBING_RIGHT_CONTINUE_2,
            RABBIT_CLIMBING_LEFT_CONTINUE_2
        );
    }

    private State newStateCont2( BehaviourTools t )
    {
        Block aboveBlock = t.blockAbove();

        if ( t.isRoof( aboveBlock ) )
        {
            abilityActive = false;
            return t.rl(
                RABBIT_CLIMBING_RIGHT_BANG_HEAD,
                RABBIT_CLIMBING_LEFT_BANG_HEAD
            );
        }

        Block endBlock = t.blockAboveNext();

        if ( t.isWall( endBlock ) )
        {
            return t.rl(
                RABBIT_CLIMBING_RIGHT_CONTINUE_1,
                RABBIT_CLIMBING_LEFT_CONTINUE_1
            );
        }
        else
        {
            return t.rl(
                RABBIT_CLIMBING_RIGHT_END,
                RABBIT_CLIMBING_LEFT_END
            );
        }
    }

    private State newStateNotClimbing( BehaviourTools t )
    {
        int nextX = t.nextX();
        int nextY = t.nextY();
        Block nextBlock = t.world.getBlockAt( nextX, nextY );
        Block aboveBlock = t.world.getBlockAt( t.rabbit.x, t.rabbit.y - 1 );

        if ( !t.isRoof( aboveBlock ) && t.isWall( nextBlock ) )
        {
            return t.rl(
                RABBIT_CLIMBING_RIGHT_START,
                RABBIT_CLIMBING_LEFT_START
            );
        }

        return null;
    }

    @Override
    public boolean behave( World world, Rabbit rabbit, State state )
    {
        BehaviourTools t = new BehaviourTools( rabbit, world );

        if( t.rabbitIsClimbing() )
        { // Can't be both on a wall and on a slope.
            rabbit.onSlope = false;
        }

        switch ( state )
        {
            case RABBIT_CLIMBING_RIGHT_START:
            case RABBIT_CLIMBING_LEFT_START:
            {
                abilityActive = true;
                return true;
            }
            case RABBIT_CLIMBING_RIGHT_END:
            case RABBIT_CLIMBING_LEFT_END:
            {
                rabbit.x = t.nextX();
                --rabbit.y;
                if ( t.hereIsUpSlope() )
                {
                    rabbit.onSlope = true;
                }
                abilityActive = false;
                return true;
            }
            case RABBIT_CLIMBING_RIGHT_CONTINUE_1:
            case RABBIT_CLIMBING_LEFT_CONTINUE_1:
            {
                abilityActive = true;
                return true;
            }
            case RABBIT_CLIMBING_RIGHT_CONTINUE_2:
            case RABBIT_CLIMBING_LEFT_CONTINUE_2:
            {
                abilityActive = true;
                --rabbit.y;
                return true;
            }
            case RABBIT_CLIMBING_RIGHT_BANG_HEAD:
            case RABBIT_CLIMBING_LEFT_BANG_HEAD:
            {
                rabbit.dir = opposite( rabbit.dir );
                return true;
            }
            default:
            {
                return false;
            }
        }
    }

    @Override
    public void saveState( Map<String, String> saveState )
    {
        BehaviourState.addToStateIfTrue(
            saveState, "Climbing.hasAbility", hasAbility
        );

        BehaviourState.addToStateIfTrue(
            saveState, "Climbing.abilityActive", abilityActive
        );
    }

    @Override
    public void restoreFromState( Map<String, String> saveState )
    {
        hasAbility = BehaviourState.restoreFromState(
            saveState, "Climbing.hasAbility", hasAbility
        );

        abilityActive = BehaviourState.restoreFromState(
            saveState, "Climbing.abilityActive", abilityActive
        );
    }
}

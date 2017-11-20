package rabbitescape.engine.behaviours;

import static rabbitescape.engine.Token.Type.*;
import static rabbitescape.engine.ChangeDescription.State.*;

import rabbitescape.engine.*;
import rabbitescape.engine.ChangeDescription.State;

public class Exploding extends Behaviour
{
    @Override
    public void cancel()
    {
    }

    @Override
    public boolean checkTriggered( Rabbit rabbit, World world )
    {
        BehaviourTools t = new BehaviourTools( rabbit, world );
        return t.pickUpToken( explode, true );
    }

    @Override
    public State newState( BehaviourTools t, boolean triggered )
    {
        if ( triggered )
        {
            return RABBIT_EXPLODING;
        }
        return null;
    }

    @Override
    public boolean behave( World world, Rabbit rabbit, State state )
    {
        if ( state == RABBIT_EXPLODING )
        {
        	
        	world.changes.killRabbit( rabbit );
        	
        	
        	Block block = world.getBlockAt( rabbit.x+1, rabbit.y );//if the location has a block, then block is not null. if has not, it will be null. If a rabbit explodes, blocks that are on right, left and bottom of the rabbit are removed 
        	if(block!=null){
        		world.changes.removeBlockAt( rabbit.x+1, rabbit.y);	
        	}
            block= world.getBlockAt( rabbit.x-1, rabbit.y );
            if(block!=null){
            	world.changes.removeBlockAt( rabbit.x-1, rabbit.y);
            }
            block= world.getBlockAt( rabbit.x, rabbit.y+1 );
            if(block!=null){
            	world.changes.removeBlockAt( rabbit.x, rabbit.y+1);
            }
            
            world.changes.killRabbit( rabbit );
            
            return true;
        }

        return false;
    }
}

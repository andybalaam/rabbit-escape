package rabbitescape.engine.behaviours;

import static rabbitescape.engine.Token.Type.*;
import static rabbitescape.engine.ChangeDescription.State.*;

import rabbitescape.engine.*;
import rabbitescape.engine.ChangeDescription.State;

public class Dynamiting extends Behaviour
{
    @Override
    public void cancel()
    {
    }

    @Override
    public boolean checkTriggered( Rabbit rabbit, World world )
    {
        BehaviourTools t = new BehaviourTools( rabbit, world );
        return t.pickUpToken( dynamite, true );
    }

    @Override
    public State newState( BehaviourTools t, boolean triggered )
    {
        if ( triggered )
        {
            return RABBIT_DYNAMITING;
        }
        return null;
    }

    @Override
    public boolean behave( World world, Rabbit rabbit, State state )
    {
        if ( state == RABBIT_DYNAMITING )
        {
        	
        	
            Block block = world.getBlockAt(rabbit.x+1, rabbit.y);
            if (block!=null)
            	world.changes.removeBlockAt(rabbit.x+1, rabbit.y);
            
            block = world.getBlockAt(rabbit.x-1, rabbit.y);
            if(block != null)
            	world.changes.removeBlockAt(rabbit.x-1, rabbit.y);
            
            block = world.getBlockAt(rabbit.x, rabbit.y+1);
            if(block != null)
            	world.changes.removeBlockAt(rabbit.x, rabbit.y+1);
            
            block = world.getBlockAt(rabbit.x, rabbit.y-1);
            if(block != null)
            	world.changes.removeBlockAt(rabbit.x, rabbit.y-1);
            
            block = world.getBlockAt(rabbit.x+1, rabbit.y+1);
            if(block != null)
            	world.changes.removeBlockAt(rabbit.x+1, rabbit.y+1);
            
            block = world.getBlockAt(rabbit.x+1, rabbit.y-1);
            if(block != null)
            	world.changes.removeBlockAt(rabbit.x+1, rabbit.y-1);
            
            block = world.getBlockAt(rabbit.x-1, rabbit.y+1);
            if(block != null)
            	world.changes.removeBlockAt(rabbit.x-1, rabbit.y+1);
            
            block = world.getBlockAt(rabbit.x-1, rabbit.y-1);
            if(block != null)
            	world.changes.removeBlockAt(rabbit.x-1, rabbit.y-1);
          
        	
            return true;
            
        }
        return false;
      
    }
}

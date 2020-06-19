package rabbitescape.engine.behaviours;

import rabbitescape.engine.Rabbit;
import rabbitescape.engine.World;
import rabbitescape.engine.ChangeDescription.State;

public interface BridgingState {
	
	public boolean moveRabbit( World world, Rabbit rabbit, State state ); 
	
}

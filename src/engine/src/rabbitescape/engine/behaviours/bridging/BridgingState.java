package rabbitescape.engine.behaviours.bridging;

import rabbitescape.engine.Rabbit;
import rabbitescape.engine.World;
import rabbitescape.engine.ChangeDescription.State;

public interface BridgingState {
	
	public boolean moveRabbit( World world, Rabbit rabbit, State state ); 
	
}

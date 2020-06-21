package rabbitescape.engine.behaviours;

import static org.junit.jupiter.api.Assertions.*;
import static rabbitescape.engine.Direction.RIGHT;
import static rabbitescape.engine.textworld.TextWorldManip.createEmptyWorld;
import static rabbitescape.engine.ChangeDescription.State.*;
import rabbitescape.engine.ChangeDescription.State;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.World;
import rabbitescape.engine.behaviours.Bridging;
import rabbitescape.engine.behaviours.Bridging.BridgeType;

import org.junit.jupiter.api.Test;

class BridgingTest {
	Bridging bridging = new Bridging();
	Rabbit r = new Rabbit( 0, 1, RIGHT, Rabbit.Type.RABBIT );
	World world = createEmptyWorld( 3, 4 );
	
	/**
	 * Purpose: test method Bridging::setState(State)
	 * Input: State RABBIT_BRIDGING_RIGHT_1
	 * Expected: return BridgeType.ALONG
	 */
	@Test
	public void testSetBridgeType() {
		State state = RABBIT_BRIDGING_RIGHT_1;
		BridgingState bridgingState = new BridgingRight();
		bridging.setState(bridgingState);
		bridging.setBridgeType(state);
		
		assertEquals(BridgeType.ALONG, bridging.getBridgeType());
	}
	
	/**
	 * Purpose: test State Pattern applied to moveRabbit method
	 * Input: Rabbit(0, 1, RIGHT, Rabbit.Type.RABBIT), World(3, 4), 
	 * 		  State {RABBIT_BRIDGING_RIGHT_1, RABBIT_BRIDGING_RIGHT_3, RABBIT_BRIDGING_LEFT_2},
	 * Expected: return true if state pattern applied successfully
	 */
	@Test
	public void testMoveRabbit() {
		State state = RABBIT_BRIDGING_RIGHT_1;
		BridgingState bridgingState = new BridgingRight();
		bridging.setState(bridgingState);
		assertEquals(true, bridging.moveRabbit(world, r, state));
		
		state = RABBIT_BRIDGING_RIGHT_3;
		assertEquals(true, bridging.moveRabbit(world, r, state));
		
		state = RABBIT_BRIDGING_LEFT_2;
		bridgingState = new BridgingLeft();
		bridging.setState(bridgingState);
		assertEquals(true, bridging.moveRabbit(world, r, state));
		
		
		
	}
	
}

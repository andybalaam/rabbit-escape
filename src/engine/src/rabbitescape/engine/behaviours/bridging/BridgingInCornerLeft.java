package rabbitescape.engine.behaviours.bridging;

import static rabbitescape.engine.Block.Material.EARTH;
import static rabbitescape.engine.Block.Shape.BRIDGE_UP_LEFT;

import rabbitescape.engine.Block;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.World;
import rabbitescape.engine.ChangeDescription.State;

public class BridgingInCornerLeft implements BridgingState {
	@Override
	public boolean moveRabbit( World world, Rabbit rabbit, State state ) {
		rabbit.onSlope = true;
        world.changes.addBlock(
            new Block(
                rabbit.x,
                rabbit.y,
                EARTH,
                BRIDGE_UP_LEFT,
                0
            )
        );
        return true;
	}
}

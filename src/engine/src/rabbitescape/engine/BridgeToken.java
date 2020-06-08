package rabbitescape.engine;

import static rabbitescape.engine.ChangeDescription.State.TOKEN_BRIDGE_FALLING;
import static rabbitescape.engine.ChangeDescription.State.TOKEN_BRIDGE_FALL_TO_SLOPE;
import static rabbitescape.engine.ChangeDescription.State.TOKEN_BRIDGE_ON_SLOPE;
import static rabbitescape.engine.ChangeDescription.State.TOKEN_BRIDGE_STILL;

import rabbitescape.engine.ChangeDescription.State;

public class BridgeToken extends Token {
	public BridgeToken(int x, int y) {
		super(x, y, Token.Type.bridge);
	}
	
	public BridgeToken(int x, int y, World world) {
		super(x, y, Token.Type.bash, world);
	}
	
	protected static State switchType(Type type, 
			boolean moving, 
			boolean slopeBelow, 
			boolean onSlope
	) {
		return chooseState( 
			moving, 
			slopeBelow, 
			onSlope,
			TOKEN_BRIDGE_FALLING, 
			TOKEN_BRIDGE_STILL,
			TOKEN_BRIDGE_FALL_TO_SLOPE, 
			TOKEN_BRIDGE_ON_SLOPE
		);
	}
}

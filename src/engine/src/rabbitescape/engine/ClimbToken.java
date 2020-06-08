package rabbitescape.engine;

import static rabbitescape.engine.ChangeDescription.State.TOKEN_CLIMB_FALLING;
import static rabbitescape.engine.ChangeDescription.State.TOKEN_CLIMB_FALL_TO_SLOPE;
import static rabbitescape.engine.ChangeDescription.State.TOKEN_CLIMB_ON_SLOPE;
import static rabbitescape.engine.ChangeDescription.State.TOKEN_CLIMB_STILL;

import rabbitescape.engine.ChangeDescription.State;

public class ClimbToken extends Token {
	public ClimbToken(int x, int y) {
		super(x, y, Token.Type.climb);
	}
	
	public ClimbToken(int x, int y, World world) {
		super(x, y, Token.Type.climb, world);
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
			TOKEN_CLIMB_FALLING, 
			TOKEN_CLIMB_STILL,
			TOKEN_CLIMB_FALL_TO_SLOPE, 
			TOKEN_CLIMB_ON_SLOPE
		);
	}
}

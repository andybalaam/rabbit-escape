package rabbitescape.engine;

import static rabbitescape.engine.ChangeDescription.State.TOKEN_DIG_FALLING;
import static rabbitescape.engine.ChangeDescription.State.TOKEN_DIG_FALL_TO_SLOPE;
import static rabbitescape.engine.ChangeDescription.State.TOKEN_DIG_ON_SLOPE;
import static rabbitescape.engine.ChangeDescription.State.TOKEN_DIG_STILL;

import rabbitescape.engine.ChangeDescription.State;

public class DigToken extends Token {
	public DigToken(int x, int y) {
		super(x, y, Token.Type.dig);
	}
	
	public DigToken(int x, int y, World world) {
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
			TOKEN_DIG_FALLING, 
			TOKEN_DIG_STILL,
			TOKEN_DIG_FALL_TO_SLOPE, 
			TOKEN_DIG_ON_SLOPE
		);
	}
}

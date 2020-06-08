package rabbitescape.engine;

import static rabbitescape.engine.ChangeDescription.State.TOKEN_BASH_FALLING;
import static rabbitescape.engine.ChangeDescription.State.TOKEN_BASH_FALL_TO_SLOPE;
import static rabbitescape.engine.ChangeDescription.State.TOKEN_BASH_ON_SLOPE;
import static rabbitescape.engine.ChangeDescription.State.TOKEN_BASH_STILL;

import rabbitescape.engine.ChangeDescription.State;

public class BashToken extends Token {
	public BashToken(int x, int y) {
		super(x, y, Token.Type.bash);
	}
	
	public BashToken(int x, int y, World world) {
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
			TOKEN_BASH_FALLING, 
			TOKEN_BASH_STILL,
			TOKEN_BASH_FALL_TO_SLOPE, 
			TOKEN_BASH_ON_SLOPE
		);
	}
}

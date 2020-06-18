package rabbitescape.engine;

import static rabbitescape.engine.ChangeDescription.State.TOKEN_BROLLY_FALLING;
import static rabbitescape.engine.ChangeDescription.State.TOKEN_BROLLY_FALL_TO_SLOPE;
import static rabbitescape.engine.ChangeDescription.State.TOKEN_BROLLY_ON_SLOPE;
import static rabbitescape.engine.ChangeDescription.State.TOKEN_BROLLY_STILL;

import rabbitescape.engine.ChangeDescription.State;

public class BrollyToken extends Token {
	public BrollyToken(int x, int y) {
		super(x, y, Token.Type.brolly);
	}
	
	public BrollyToken(int x, int y, World world) {
		super(x, y, Token.Type.brolly, world);
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
			TOKEN_BROLLY_FALLING, 
			TOKEN_BROLLY_STILL,
			TOKEN_BROLLY_FALL_TO_SLOPE, 
			TOKEN_BROLLY_ON_SLOPE
		);
	}
}

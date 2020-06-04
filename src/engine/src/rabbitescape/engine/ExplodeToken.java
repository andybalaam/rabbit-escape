package rabbitescape.engine;

import static rabbitescape.engine.ChangeDescription.State.TOKEN_EXPLODE_FALLING;
import static rabbitescape.engine.ChangeDescription.State.TOKEN_EXPLODE_FALL_TO_SLOPE;
import static rabbitescape.engine.ChangeDescription.State.TOKEN_EXPLODE_ON_SLOPE;
import static rabbitescape.engine.ChangeDescription.State.TOKEN_EXPLODE_STILL;

import rabbitescape.engine.ChangeDescription.State;

public class ExplodeToken extends Token {
	public ExplodeToken(int x, int y) {
		super(x, y, Token.Type.explode);
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
			TOKEN_EXPLODE_FALLING, 
			TOKEN_EXPLODE_STILL,
			TOKEN_EXPLODE_FALL_TO_SLOPE, 
			TOKEN_EXPLODE_ON_SLOPE
		);
	}
}

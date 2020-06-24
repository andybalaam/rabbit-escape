package rabbitescape.engine.tokens;

import static rabbitescape.engine.ChangeDescription.State.TOKEN_BLOCK_FALLING;
import static rabbitescape.engine.ChangeDescription.State.TOKEN_BLOCK_FALL_TO_SLOPE;
import static rabbitescape.engine.ChangeDescription.State.TOKEN_BLOCK_ON_SLOPE;
import static rabbitescape.engine.ChangeDescription.State.TOKEN_BLOCK_STILL;

import rabbitescape.engine.ChangeDescription.State;
import rabbitescape.engine.Token;
import rabbitescape.engine.World;

public class BlockToken extends Token {
	public BlockToken(int x, int y) {
		super(x, y, Token.Type.block);
	}
	
	public BlockToken(int x, int y, World world) {
		super(x, y, Token.Type.block, world);
	}
	
	public static State switchType(Type type, 
			boolean moving, 
			boolean slopeBelow, 
			boolean onSlope
	) {
		return chooseState( 
			moving, 
			slopeBelow, 
			onSlope,
			TOKEN_BLOCK_FALLING, 
			TOKEN_BLOCK_STILL,
			TOKEN_BLOCK_FALL_TO_SLOPE, 
			TOKEN_BLOCK_ON_SLOPE
		);
	}
}

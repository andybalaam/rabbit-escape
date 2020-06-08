package rabbitescape.engine;

public class TokenFactory {
	public static Token createToken(int x, int y, char TypeChar) {
		Token ret = null;
		switch (TypeChar) {
		case 'b': {
	        ret = new BashToken(x, y);
	        break;
	    }
	    case 'd': {
	        ret = new DigToken(x, y);
	        break;
	    }
	    case 'i': {
	        ret = new BridgeToken(x, y);
	        break;
	    }
	    case 'k': {
	        ret = new BlockToken(x, y);
	        break;
	    }
	    case 'c': {
	        ret = new ClimbToken(x, y);
	        break;
	    }
	    case 'p': {
	        ret = new ExplodeToken(x, y);
	        break;
	    }
	    case 'l': {
	        ret = new BrollyToken(x, y);
	        break;
	    }
		}
		return ret;
	}
	public static Token createToken(int x, int y, Token.Type type) {
		Token ret = null;
		switch (type) {
		case bash: {
	        ret = new BashToken(x, y);
	        break;
	    }
	    case dig: {
	        ret = new DigToken(x, y);
	        break;
	    }
	    case bridge: {
	        ret = new BridgeToken(x, y);
	        break;
	    }
	    case block: {
	        ret = new BlockToken(x, y);
	        break;
	    }
	    case climb: {
	        ret = new ClimbToken(x, y);
	        break;
	    }
	    case explode: {
	        ret = new ExplodeToken(x, y);
	        break;
	    }
	    case brolly: {
	        ret = new BrollyToken(x, y);
	        break;
	    }
		}
		return ret;
	}
	
	public static Token createToken(int x, int y, Token.Type type, World world) {
		Token ret = null;
		switch (type) {
		case bash: {
	        ret = new BashToken(x, y, world);
	        break;
	    }
	    case dig: {
	        ret = new DigToken(x, y, world);
	        break;
	    }
	    case bridge: {
	        ret = new BridgeToken(x, y, world);
	        break;
	    }
	    case block: {
	        ret = new BlockToken(x, y, world);
	        break;
	    }
	    case climb: {
	        ret = new ClimbToken(x, y, world);
	        break;
	    }
	    case explode: {
	        ret = new ExplodeToken(x, y, world);
	        break;
	    }
	    case brolly: {
	        ret = new BrollyToken(x, y, world);
	        break;
	    }
		}
		return ret;
	}
}

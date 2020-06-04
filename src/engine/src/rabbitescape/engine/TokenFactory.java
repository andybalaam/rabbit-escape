package rabbitescape.engine;

public class TokenFactory {
	public static Token createToken(int x, int y, char TypeChar) {
		Token ret = null;
		switch (TypeChar) {
		case 'b':
	    {
	        ret = new BashToken(x, y);
	        break;
	    }
	    case 'd':
	    {
	        ret = new DigToken(x, y);
	        break;
	    }
	    case 'i':
	    {
	        ret = new BridgeToken(x, y);
	        break;
	    }
	    case 'k':
	    {
	        ret = new BlockToken(x, y);
	        break;
	    }
	    case 'c':
	    {
	        ret = new ClimbToken(x, y);
	        break;
	    }
	    case 'p':
	    {
	        ret = new ExplodeToken(x, y);
	        break;
	    }
	    case 'l':
	    {
	        ret = new BrollyToken(x, y);
	        break;
	    }
		}
		return ret;
	}
}

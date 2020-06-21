package rabbitescape.engine;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TokenTest {
	public TokenFactory testTokenFactory;
	
	@Before
	public void SetUp() {
	}
	
	@Test
	public void LineCoverageTest01() throws Exception {
		testTokenFactory = new TokenFactory();
		Token testToken = TokenFactory.createToken(25, 25, 'b');
		Assert.assertEquals(testToken.getClass(), BashToken.class);
		Assert.assertEquals(testToken.type, Token.Type.bash);
	}
	
	@Test
	public void LineCoverageTest02() throws Exception {
		testTokenFactory = new TokenFactory();
		Token testToken = TokenFactory.createToken(25, 25, 'd');
		Assert.assertEquals(testToken.getClass(), DigToken.class);
		Assert.assertEquals(testToken.type, Token.Type.dig);
	}
	
	@Test
	public void LineCoverageTest03() throws Exception {
		testTokenFactory = new TokenFactory();
		Token testToken = TokenFactory.createToken(25, 25, 'i');
		Assert.assertEquals(testToken.getClass(), BridgeToken.class);
		Assert.assertEquals(testToken.type, Token.Type.bridge);
	}
	
	@Test
	public void LineCoverageTest04() throws Exception {
		testTokenFactory = new TokenFactory();
		Token testToken = TokenFactory.createToken(25, 25, 'k');
		Assert.assertEquals(testToken.getClass(), BlockToken.class);
		Assert.assertEquals(testToken.type, Token.Type.block);
	}
	
	@Test
	public void LineCoverageTest05() throws Exception {
		testTokenFactory = new TokenFactory();
		Token testToken = TokenFactory.createToken(25, 25, 'c');
		Assert.assertEquals(testToken.getClass(), ClimbToken.class);
		Assert.assertEquals(testToken.type, Token.Type.climb);
	}
	
	@Test
	public void LineCoverageTest06() throws Exception {
		testTokenFactory = new TokenFactory();
		Token testToken = TokenFactory.createToken(25, 25, 'p');
		Assert.assertEquals(testToken.getClass(), ExplodeToken.class);
		Assert.assertEquals(testToken.type, Token.Type.explode);
	}
	
	@Test
	public void LineCoverageTest07() throws Exception {
		testTokenFactory = new TokenFactory();
		Token testToken = TokenFactory.createToken(25, 25, 'l');
		Assert.assertEquals(testToken.getClass(), BrollyToken.class);
		Assert.assertEquals(testToken.type, Token.Type.brolly);
	}
	
	@Test
	public void NullTest01() throws Exception {
		testTokenFactory = new TokenFactory();
		Token testToken = TokenFactory.createToken(25, 25, 'f');
		Assert.assertEquals(testToken, null);
	}

	// Test of Factory which has enum-typed-parameter
	@Test
	public void LineCoverageTest08() throws Exception {
		testTokenFactory = new TokenFactory();
		Token testToken = TokenFactory.createToken(25, 25, Token.Type.bash);
		Assert.assertEquals(testToken.getClass(), BashToken.class);
		Assert.assertEquals(testToken.type, Token.Type.bash);
	}
	
	@Test
	public void LineCoverageTest09() throws Exception {
		testTokenFactory = new TokenFactory();
		Token testToken = TokenFactory.createToken(25, 25, Token.Type.dig);
		Assert.assertEquals(testToken.getClass(), DigToken.class);
		Assert.assertEquals(testToken.type, Token.Type.dig);
	}
	
	@Test
	public void LineCoverageTest10() throws Exception {
		testTokenFactory = new TokenFactory();
		Token testToken = TokenFactory.createToken(25, 25, Token.Type.bridge);
		Assert.assertEquals(testToken.getClass(), BridgeToken.class);
		Assert.assertEquals(testToken.type, Token.Type.bridge);
	}
	
	@Test
	public void LineCoverageTest11() throws Exception {
		testTokenFactory = new TokenFactory();
		Token testToken = TokenFactory.createToken(25, 25, Token.Type.block);
		Assert.assertEquals(testToken.getClass(), BlockToken.class);
		Assert.assertEquals(testToken.type, Token.Type.block);
	}
	
	@Test
	public void LineCoverageTest12() throws Exception {
		testTokenFactory = new TokenFactory();
		Token testToken = TokenFactory.createToken(25, 25, Token.Type.climb);
		Assert.assertEquals(testToken.getClass(), ClimbToken.class);
		Assert.assertEquals(testToken.type, Token.Type.climb);
	}
	
	@Test
	public void LineCoverageTest13() throws Exception {
		testTokenFactory = new TokenFactory();
		Token testToken = TokenFactory.createToken(25, 25, Token.Type.explode);
		Assert.assertEquals(testToken.getClass(), ExplodeToken.class);
		Assert.assertEquals(testToken.type, Token.Type.explode);
	}
	
	@Test
	public void LineCoverageTest14() throws Exception {
		testTokenFactory = new TokenFactory();
		Token testToken = TokenFactory.createToken(25, 25, Token.Type.brolly);
		Assert.assertEquals(testToken.getClass(), BrollyToken.class);
		Assert.assertEquals(testToken.type, Token.Type.brolly);
	}
	
	
	
	
	@After
	public void tearDown() {
		testTokenFactory = null;
	}
}

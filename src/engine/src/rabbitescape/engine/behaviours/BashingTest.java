package rabbitescape.engine.behaviours;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import rabbitescape.engine.behaviours.bashing.BashingInterFace;
import rabbitescape.engine.behaviours.bashing.BashingLeft;
import rabbitescape.engine.behaviours.bashing.NotBashing;

public class BashingTest {
	private Bashing bashing;
	private BashingInterFace bashingState1;
	

	@Before
	public void setUp() {
		bashing = new Bashing();
	}
	
	/*bashing state for Bashing is NotBashing when it is constructed*/
	@Test
	public void constructorTest() {
		bashingState1 = new NotBashing();
		assertSame(bashing.getBashingState(), bashingState1);
	}
	
	/*Test for getter and setter for Bashing state
	 * getter must return the state when set*/
	@Test
	public void getBashingStateTest() {
		bashingState1 = new BashingLeft();
		bashing.setBashingState(bashingState1);
		assertSame(bashing.getBashingState(), bashingState1);
	}
	


}

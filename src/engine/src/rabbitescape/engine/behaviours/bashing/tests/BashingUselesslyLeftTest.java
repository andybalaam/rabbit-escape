package rabbitescape.engine.behaviours.bashing.tests;

import static org.junit.Assert.*;
import static rabbitescape.engine.ChangeDescription.State.RABBIT_BASHING_USELESSLY_LEFT;
import static rabbitescape.engine.Direction.RIGHT;

import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.engine.World;
import rabbitescape.engine.behaviours.bashing.BashingUselesslyLeft;
import rabbitescape.engine.Rabbit;

import org.junit.Before;
import org.junit.Test;

public class BashingUselesslyLeftTest {
    private World world;
    private Rabbit rabbit;
    private static BashingUselesslyLeft bashingUselesslyLeft;
    
    /*setup for the test*/
    @Before
    public void setUp() {
    	 world = TextWorldManip.createEmptyWorld(3, 4);
    	 rabbit = new Rabbit(1, 1, RIGHT, Rabbit.Type.RABBIT);
    	 bashingUselesslyLeft = new BashingUselesslyLeft();
    }
    
    /*
     * Purpose: Test BashingUselesslyLeft::getState()
     * Input: getState()
     * Expected: RABBIT_BASHING_USELESSLY_LEFT
     */
    @Test
    public void getState() {
        assertEquals(RABBIT_BASHING_USELESSLY_LEFT, bashingUselesslyLeft.getState());
    }
    
    /*
     * Purpose: Test BashingUselesslyLeft::behave().
     * Input: behave(), rabbit.slopeBashHop -> false
     * Expected: true
     */
    @Test
    public void behave() {
        assertTrue(bashingUselesslyLeft.behave(world, rabbit));
    }
}

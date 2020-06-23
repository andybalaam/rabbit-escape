package rabbitescape.engine.behaviours.bashing.tests;

import static org.junit.Assert.*;
import static rabbitescape.engine.ChangeDescription.State.RABBIT_BASHING_USELESSLY_RIGHT_UP;
import static rabbitescape.engine.Direction.RIGHT;

import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.engine.World;
import rabbitescape.engine.behaviours.bashing.BashingUselesslyRightUp;
import rabbitescape.engine.Rabbit;

import org.junit.Before;
import org.junit.Test;

public class BashingUselesslyRightUpTest {
    private World world;
    private Rabbit rabbit;
    private static BashingUselesslyRightUp bashingUselesslyRightUp;
    
    /*setup for the test*/
    @Before
    public void setUp() {
    	 world = TextWorldManip.createEmptyWorld(3, 4);
    	 rabbit = new Rabbit(1, 1, RIGHT, Rabbit.Type.RABBIT);
    	 bashingUselesslyRightUp = new BashingUselesslyRightUp();
    }
    
    /*
     * Purpose: Test BashingUselesslyRightUp::getState()
     * Input: getState()
     * Expected: RABBIT_BASHING_USELESSLY_RIGHT_UP
     */
    @Test
    public void getState() {
        assertEquals(RABBIT_BASHING_USELESSLY_RIGHT_UP, bashingUselesslyRightUp.getState());
    }
    
    /*
     * Purpose: Test BashingUselesslyRightUp::behave().
     * Input: behave(), rabbit.slopeBashHop -> false, rabbit.y -> rabbit.y - 1
     * Expected: true
     */
    @Test
    public void behave() {
        assertTrue(bashingUselesslyRightUp.behave(world, rabbit));
    }
}

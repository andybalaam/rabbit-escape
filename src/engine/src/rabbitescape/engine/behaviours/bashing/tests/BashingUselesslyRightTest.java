package rabbitescape.engine.behaviours.bashing.tests;

import static org.junit.Assert.*;
import static rabbitescape.engine.ChangeDescription.State.RABBIT_BASHING_USELESSLY_RIGHT;
import static rabbitescape.engine.Direction.RIGHT;

import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.engine.World;
import rabbitescape.engine.behaviours.bashing.BashingUselesslyRight;
import rabbitescape.engine.Rabbit;

import org.junit.Before;
import org.junit.Test;

public class BashingUselesslyRightTest {
    private World world;
    private Rabbit rabbit;
    private static BashingUselesslyRight bashingUselesslyRight;
    
    /*setup for the test*/
    @Before
    public void setUp() {
    	 world = TextWorldManip.createEmptyWorld(3, 4);
    	 rabbit = new Rabbit(1, 1, RIGHT, Rabbit.Type.RABBIT);
    	 bashingUselesslyRight = new BashingUselesslyRight();
    }
    
    /*
     * Purpose: Test BashingUselesslyRight::getState()
     * Input: getState()
     * Expected: RABBIT_BASHING_USELESSLY_RIGHT
     */
    @Test
    public void getState() {
        assertEquals(RABBIT_BASHING_USELESSLY_RIGHT, bashingUselesslyRight.getState());
    }
    
    /*
     * Purpose: Test BashingUselesslyRight::behave().
     * Input: behave(), rabbit.slopeBashHop -> false
     * Expected: true
     */
    @Test
    public void behave() {
        assertTrue(bashingUselesslyRight.behave(world, rabbit));
    }

}

package rabbitescape.engine.behaviours.bashing.tests;

import static org.junit.Assert.*;
import static rabbitescape.engine.Direction.RIGHT;

import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.engine.World;
import rabbitescape.engine.behaviours.bashing.NotBashing;
import rabbitescape.engine.Rabbit;

import org.junit.Before;
import org.junit.Test;

public class NotBashingTest {
    private World world;
    private Rabbit rabbit;
    private static NotBashing notBashing;
    
    /*setup for the test*/
    @Before
    public void setUp() {
    	 world = TextWorldManip.createEmptyWorld(3, 4);
    	 rabbit = new Rabbit(1, 1, RIGHT, Rabbit.Type.RABBIT);
    	 notBashing = new NotBashing();
    }
    
    /*
     * Purpose: Test NotBashing::getState()
     * Input: getState()
     * Expected: null
     */
    @Test
    public void getState() {
        assertNull(notBashing.getState());
    }
    
    /*
     * Purpose: Test NotBashing::behave().
     * Input: behave()
     * Expected: false
     */
    @Test
    public void behave() {
        assertFalse(notBashing.behave(world, rabbit));
    }
}

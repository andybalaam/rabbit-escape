package rabbitescape.engine.behaviours.bashing.tests;

import static org.junit.Assert.*;
import static rabbitescape.engine.ChangeDescription.State.RABBIT_BASHING_UP_LEFT;
import static rabbitescape.engine.Direction.RIGHT;
import static rabbitescape.engine.Block.Material.EARTH;
import static rabbitescape.engine.Block.Shape.FLAT;

import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.engine.World;
import rabbitescape.engine.behaviours.bashing.BashingUpLeft;
import rabbitescape.engine.Block;
import rabbitescape.engine.Rabbit;

import org.junit.Before;
import org.junit.Test;

public class BashingUpLeftTest {
    private World world;
    private Rabbit rabbit;
    private static BashingUpLeft bashingUpLeft;
    
    /*setup for the test*/
    @Before
    public void setUp() {
    	 world = TextWorldManip.createEmptyWorld(3, 4);
    	 rabbit = new Rabbit(1, 1, RIGHT, Rabbit.Type.RABBIT);
    	 bashingUpLeft = new BashingUpLeft();
    }
    
    /*
     * Purpose: Test BashingUpLeft::getState().
     * Input: getState()
     * Expected: RABBIT_BASHING_UP_LEFT
     */
    @Test
    public void getState() {
        assertEquals(RABBIT_BASHING_UP_LEFT, bashingUpLeft.getState());
    }
    
    /*
     * Purpose: Test BashingUpLeft::behave().
     * Input: behave(), rabbit.slopeBashHop -> false
     * Expected: true
     */
    @Test
    public void behave() {
        world.blockTable.add(new Block(2, 0, EARTH, FLAT, 4));
        assertTrue(bashingUpLeft.behave(world, rabbit));
    }
}

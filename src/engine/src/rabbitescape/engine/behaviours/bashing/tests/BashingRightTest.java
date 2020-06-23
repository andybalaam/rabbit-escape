package rabbitescape.engine.behaviours.bashing.tests;

import static org.junit.Assert.*;
import static rabbitescape.engine.ChangeDescription.State.RABBIT_BASHING_RIGHT;
import static rabbitescape.engine.Direction.RIGHT;
import static rabbitescape.engine.Block.Material.EARTH;
import static rabbitescape.engine.Block.Shape.FLAT;

import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.engine.World;
import rabbitescape.engine.behaviours.bashing.BashingRight;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.Block;

import org.junit.Before;
import org.junit.Test;

public class BashingRightTest {
    private World world;
    private Rabbit rabbit;
    private static BashingRight bashingRight;
    
    /*setup for the test*/
    @Before
    public void setUp() {
    	 world = TextWorldManip.createEmptyWorld(3, 4);
    	 rabbit = new Rabbit(1, 1, RIGHT, Rabbit.Type.RABBIT);
    	 bashingRight = new BashingRight();
    }
    
    /*
     * Purpose: Test BashingRight::getState()
     * Input: getState()
     * Expected: RABBIT_BASHING_RIGHT
     */
    @Test
    public void getState() {
        assertEquals(RABBIT_BASHING_RIGHT, bashingRight.getState());
    }
    
    /*
     * Purpose: Test BashingLeft::behave().
     * Input: behave(), rabbit.slopeBashHop -> false
     * Expected: true
     */
    @Test
    public void behave() {
        world.blockTable.add(new Block(2, 1, EARTH, FLAT, 4));
        assertTrue(bashingRight.behave(world, rabbit));
    }
}

package rabbitescape.engine.behaviours.bashing.tests;

import static org.junit.Assert.*;
import static rabbitescape.engine.ChangeDescription.State.RABBIT_BASHING_UP_RIGHT;
import static rabbitescape.engine.Direction.RIGHT;
import static rabbitescape.engine.Block.Material.EARTH;
import static rabbitescape.engine.Block.Shape.FLAT;

import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.engine.World;
import rabbitescape.engine.behaviours.bashing.BashingUpRight;
import rabbitescape.engine.Block;
import rabbitescape.engine.Rabbit;

import org.junit.Before;
import org.junit.Test;

public class BashingUpRightTest {
    private World world;
    private Rabbit rabbit;
    private static BashingUpRight bashingUpRight;
    
    /*setup for the test*/
    @Before
    public void setUp() {
    	 world = TextWorldManip.createEmptyWorld(3, 4);
    	 rabbit = new Rabbit(1, 1, RIGHT, Rabbit.Type.RABBIT);
    	 bashingUpRight = new BashingUpRight();
    }
    
    /*
     * Purpose: Test BashingUpRight::getState().
     * Input: getState()
     * Expected: RABBIT_BASHING_UP_LEFT
     */
    @Test
    public void getState() {
        assertEquals(RABBIT_BASHING_UP_RIGHT, bashingUpRight.getState());
    }
    
    /*
     * Purpose: Test BashingUpRight::behave().
     * Input: behave(), rabbit.slopeBashHop -> false, rabbit.y -> rabbit.y - 1
     * Expected: true
     */
    @Test
    public void behave() {
        world.blockTable.add(new Block(2, 0, EARTH, FLAT, 4));
        assertTrue(bashingUpRight.behave(world, rabbit));
    }
}

package rabbitescape.engine.behaviours.climbing.tests;

import static org.junit.Assert.*;
import static rabbitescape.engine.ChangeDescription.State.RABBIT_CLIMBING_RIGHT_START;
import static rabbitescape.engine.Direction.RIGHT;
import static rabbitescape.engine.Block.Material.EARTH;
import static rabbitescape.engine.Block.Shape.FLAT;

import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.engine.World;
import rabbitescape.engine.behaviours.Climbing;
import rabbitescape.engine.behaviours.climbing.ClimbingRightStart;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.BehaviourTools;
import rabbitescape.engine.Block;

import org.junit.Before;
import org.junit.Test;

public class ClimbingRightStartTest {
    private World world;
    private Rabbit rabbit;
    private BehaviourTools behaviourTools;
    private Climbing climbing;
    private ClimbingRightStart climbingRightStart;
    
    /*setup for the test*/
    @Before
    public void setUp() {
    	 world = TextWorldManip.createEmptyWorld(3, 4);
    	 rabbit = new Rabbit(1, 1, RIGHT, Rabbit.Type.RABBIT);
    	 behaviourTools = new BehaviourTools(rabbit, world);
    	 climbing = new Climbing();
    	 climbingRightStart = new ClimbingRightStart();
    }    
    
    /*
     * Purpose: Test ClimbingRightStart::getState().
     * Input: getState()
     * Expected: RABBIT_CLIMBING_RIGHT_START
     */
    @Test
    public void getState() {
        assertEquals(RABBIT_CLIMBING_RIGHT_START, climbingRightStart.getState());
    }
    
    /*
     * Purpose: Test ClimbingRightStart::newState().
     * Input: newState() ClimbingRightStart() -> ClimbingRightContinue2()/ClimbingRightEnd()
     * Expected: ClimbingRightContinue2(), ClimbingRightEnd()
     */
    @Test
    public void newState() {
        assertNotNull(climbingRightStart.newState(behaviourTools, climbing));

        world.blockTable.add(new Block(2, 0, EARTH, FLAT, 4));
        behaviourTools = new BehaviourTools(rabbit, world);
        assertNotNull(climbingRightStart.newState(behaviourTools, climbing));
    }
    
    /*
     * Purpose: Test ClimbingRightStart::behave().
     * Input: behave(), climbing.abilityActive -> true
     * Expected: true
     */
    @Test
    public void behave() {
        assertTrue(climbingRightStart.behave(world, rabbit, climbing));
    }
}

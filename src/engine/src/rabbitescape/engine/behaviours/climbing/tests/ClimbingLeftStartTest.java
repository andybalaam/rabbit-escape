package rabbitescape.engine.behaviours.climbing.tests;

import static org.junit.Assert.*;
import static rabbitescape.engine.ChangeDescription.State.RABBIT_CLIMBING_LEFT_START;
import static rabbitescape.engine.Direction.RIGHT;
import static rabbitescape.engine.Block.Material.EARTH;
import static rabbitescape.engine.Block.Shape.FLAT;
import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.engine.World;
import rabbitescape.engine.behaviours.Climbing;
import rabbitescape.engine.behaviours.climbing.ClimbingLeftStart;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.BehaviourTools;
import rabbitescape.engine.Block;

import org.junit.Before;
import org.junit.Test;

public class ClimbingLeftStartTest {
    private World world;
    private Rabbit rabbit;
    private BehaviourTools behaviourTools;
    private Climbing climbing;
    private ClimbingLeftStart climbingLeftStart;
    
    /*setup for the test*/
    @Before
    public void setUp() {
    	 world = TextWorldManip.createEmptyWorld(3, 4);
    	 rabbit = new Rabbit(1, 1, RIGHT, Rabbit.Type.RABBIT);
    	 behaviourTools = new BehaviourTools(rabbit, world);
    	 climbing = new Climbing();
    	 climbingLeftStart = new ClimbingLeftStart();
    }
    
    /*
     * Purpose: Test ClimbingLeftStart::getState().
     * Input: getState()
     * Expected: RABBIT_CLIMBING_LEFT_START
     */
    @Test
    public void getState() {
        assertEquals(RABBIT_CLIMBING_LEFT_START, climbingLeftStart.getState());
    }

    /*
     * Purpose: Test ClimbingLeftStart::newState().
     * Input: newState() ClimbingLeftStart() -> ClimbingLeftContinue2()/ClimbingLeftEnd()
     * Expected: ClimbingLeftContinue2(), ClimbingLeftEnd()
     */
    @Test
    public void newState() {
        assertNotNull(climbingLeftStart.newState(behaviourTools, climbing));

        world.blockTable.add(new Block(2, 0, EARTH, FLAT, 4));
        behaviourTools = new BehaviourTools(rabbit, world);
        assertNotNull(climbingLeftStart.newState(behaviourTools, climbing));
    }

    /*
     * Purpose: Test ClimbingLeftStart::behave().
     * Input: behave(), climbing.abilityActive -> true
     * Expected: true
     */
    @Test
    public void behave() {
        assertTrue(climbingLeftStart.behave(world, rabbit, climbing));
    }
}

package rabbitescape.engine.behaviours.climbing.tests;

import static org.junit.Assert.*;
import static rabbitescape.engine.ChangeDescription.State.RABBIT_CLIMBING_LEFT_CONTINUE_1;
import static rabbitescape.engine.Direction.RIGHT;

import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.engine.World;
import rabbitescape.engine.behaviours.Climbing;
import rabbitescape.engine.behaviours.climbing.ClimbingLeftContinue1;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.BehaviourTools;

import org.junit.Before;
import org.junit.Test;

public class ClimbingLeftContinue1Test {
    private World world;
    private Rabbit rabbit;
    private BehaviourTools behaviourTools;
    private Climbing climbing;
    private ClimbingLeftContinue1 climbingLeftContinue1;
    
    /*setup for the test*/
    @Before
    public void setUp() {
    	 world = TextWorldManip.createEmptyWorld(3, 4);
    	 rabbit = new Rabbit(1, 1, RIGHT, Rabbit.Type.RABBIT);
    	 behaviourTools = new BehaviourTools(rabbit, world);
    	 climbing = new Climbing();
    	 climbingLeftContinue1 = new ClimbingLeftContinue1();
    }
    
    /*
     * Purpose: Test climbingLeftContinue1::getState().
     * Input: getState()
     * Expected: RABBIT_CLIMBING_LEFT_CONTINUE_1
     */
    @Test
    public void getState() {
        assertEquals(RABBIT_CLIMBING_LEFT_CONTINUE_1, climbingLeftContinue1.getState());
    }
    
    /*
    Purpose: Test climbingLeftContinue1::newState().
    Input: newState() ClimbingLeftContinue1() -> ClimbingLeftContinue2()
    Expected: ClimbingLeftContinue2()
     */
    @Test
    public void newState() {
        assertNotNull(climbingLeftContinue1.newState(behaviourTools, climbing));
    }    
    
    /*
     * Purpose: Test climbingLeftContinue1::behave().
     * Input: behave(), climbing.abilityActive -> true
     * Expected: true
     */
    @Test
    public void behave() {
        assertTrue(climbingLeftContinue1.behave(world, rabbit, climbing));
    }
}

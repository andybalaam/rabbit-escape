package rabbitescape.engine.behaviours.climbing.tests;

import static org.junit.Assert.*;
import static rabbitescape.engine.ChangeDescription.State.RABBIT_CLIMBING_RIGHT_CONTINUE_1;
import static rabbitescape.engine.Direction.RIGHT;

import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.engine.World;
import rabbitescape.engine.behaviours.Climbing;
import rabbitescape.engine.behaviours.climbing.ClimbingRightContinue1;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.BehaviourTools;
import org.junit.Before;
import org.junit.Test;

public class ClimbingRightContinue1Test {
    private World world;
    private Rabbit rabbit;
    private BehaviourTools behaviourTools;
    private Climbing climbing;
    private ClimbingRightContinue1 climbingRightContinue1;
    
    /*setup for the test*/
    @Before
    public void setUp() {
    	 world = TextWorldManip.createEmptyWorld(3, 4);
    	 rabbit = new Rabbit(1, 1, RIGHT, Rabbit.Type.RABBIT);
    	 behaviourTools = new BehaviourTools(rabbit, world);
    	 climbing = new Climbing();
    	 climbingRightContinue1 = new ClimbingRightContinue1();
    }
    
    /*
     * Purpose: Test ClimbingRightContinue1::getState().
     * Input: getState()
     * Expected: RABBIT_CLIMBING_RIGHT_CONTINUE_1
     */
    @Test
    public void getState() {
        assertEquals(RABBIT_CLIMBING_RIGHT_CONTINUE_1, climbingRightContinue1.getState());
    }

    /*
     * Purpose: Test ClimbingRightContinue1::newState().
     * Input: newState() ClimbingRightContinue1() -> ClimbingRightContinue2()
     * Expected: ClimbingRightContinue2()
     */
    @Test
    public void newState() {
        assertNotNull(climbingRightContinue1.newState(behaviourTools, climbing));
    }

    /*
     * Purpose: Test ClimbingRightContinue1::behave().
     * Input: behave(), climbing.abilityActive -> true
     * Expected: true, true
     */
    @Test
    public void behave() {
        assertTrue(climbingRightContinue1.behave(world, rabbit, climbing));
    }
}

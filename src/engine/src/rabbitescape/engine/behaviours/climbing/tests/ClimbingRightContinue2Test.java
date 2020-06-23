package rabbitescape.engine.behaviours.climbing.tests;

import static org.junit.Assert.*;
import static rabbitescape.engine.ChangeDescription.State.RABBIT_CLIMBING_RIGHT_CONTINUE_2;
import static rabbitescape.engine.Direction.RIGHT;
import static rabbitescape.engine.Block.Material.EARTH;
import static rabbitescape.engine.Block.Shape.FLAT;

import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.engine.World;
import rabbitescape.engine.behaviours.Climbing;
import rabbitescape.engine.behaviours.climbing.ClimbingRightContinue2;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.BehaviourTools;
import rabbitescape.engine.Block;

import org.junit.Before;
import org.junit.Test;

public class ClimbingRightContinue2Test {
    private World world;
    private Rabbit rabbit;
    private BehaviourTools behaviourTools;
    private Climbing climbing;
    private ClimbingRightContinue2 climbingRightContinue2;
    
    /*setup for the test*/
    @Before
    public void setUp() {
    	 world = TextWorldManip.createEmptyWorld(3, 4);
    	 rabbit = new Rabbit(1, 1, RIGHT, Rabbit.Type.RABBIT);
    	 behaviourTools = new BehaviourTools(rabbit, world);
    	 climbing = new Climbing();
    	 climbingRightContinue2 = new ClimbingRightContinue2();
    }
    
    /*
     * Purpose: Test ClimbingRightContinue2::getState().
     * Input: getState()
     * Expected: RABBIT_CLIMBING_RIGHT_CONTINUE_2
     */
    @Test
    public void getState() {
        assertEquals(RABBIT_CLIMBING_RIGHT_CONTINUE_2, climbingRightContinue2.getState());
    }

    /*
     * Purpose: Test ClimbingRightContinue2::newState().
     * Input: newState() ClimbingRightContinue2() -> ClimbingRightBangHead()/ClimbingRightContinue1()/ClimbingRightEnd()
     * Expected: ClimbingRightBangHead(), ClimbingRightContinue1(), ClimbingRightEnd()
     */
    @Test
    public void newState() {
        assertNotNull(climbingRightContinue2.newState(behaviourTools, climbing));

        world.blockTable.add(new Block(1, 0, EARTH, FLAT, 4));
        behaviourTools = new BehaviourTools(rabbit, world);
        assertNotNull(climbingRightContinue2.newState(behaviourTools, climbing));

        World world1 = TextWorldManip.createEmptyWorld(10, 10);
        rabbit = new Rabbit(1, 1, RIGHT, Rabbit.Type.RABBIT);
        world1.blockTable.add(new Block(2, 0, EARTH, FLAT, 4));
        behaviourTools = new BehaviourTools(rabbit, world1);
        assertNotNull(climbingRightContinue2.newState(behaviourTools, climbing));
    }

    /*
     * Purpose: Test ClimbingRightContinue2::behave().
     * Input: behave(), rabbit.y -> rabbit.y - 1, climbing.abilityActive -> true
     * Expected: true, true
     */
    @Test
    public void behave() {
        assertTrue(climbingRightContinue2.behave(world, rabbit, climbing));
    }
}

package rabbitescape.engine.behaviours.climbing.tests;

import static org.junit.Assert.*;
import static rabbitescape.engine.ChangeDescription.State.RABBIT_CLIMBING_LEFT_CONTINUE_2;
import static rabbitescape.engine.Direction.RIGHT;
import static rabbitescape.engine.Block.Material.EARTH;
import static rabbitescape.engine.Block.Shape.FLAT;

import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.engine.World;
import rabbitescape.engine.behaviours.Climbing;
import rabbitescape.engine.behaviours.climbing.ClimbingLeftContinue2;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.BehaviourTools;
import rabbitescape.engine.Block;

import org.junit.Before;
import org.junit.Test;

public class ClimbingLeftContinue2Test {
    private World world;
    private Rabbit rabbit;
    private BehaviourTools behaviourTools;
    private Climbing climbing;
    private ClimbingLeftContinue2 climbingLeftContinue2;
    
    /*setup for the test*/
    @Before
    public void setUp() {
    	 world = TextWorldManip.createEmptyWorld(3, 4);
    	 rabbit = new Rabbit(1, 1, RIGHT, Rabbit.Type.RABBIT);
    	 behaviourTools = new BehaviourTools(rabbit, world);
    	 climbing = new Climbing();
    	 climbingLeftContinue2 = new ClimbingLeftContinue2();
    }

    /*
     * Purpose: Test ClimbingLeftContinue2::getState().
     * Input: getState()
     * Expected: RABBIT_CLIMBING_LEFT_CONTINUE_2
     */
    @Test
    public void getState() {
        assertEquals(RABBIT_CLIMBING_LEFT_CONTINUE_2, climbingLeftContinue2.getState());
    }
    
    /*
     * Purpose: Test ClimbingLeftContinue2::newState().
     * Input: newState() ClimbingLeftContinue2() -> ClimbingLeftBangHead()/ClimbingLeftContinue1()/ClimbingLeftEnd()
     * Expected: ClimbingLeftBangHead(), ClimbingLeftContinue1(), ClimbingLeftEnd()
     */
    @Test
    public void newState() {
        assertNotNull(climbingLeftContinue2.newState(behaviourTools, climbing));

        world.blockTable.add(new Block(1, 0, EARTH, FLAT, 4));
        behaviourTools = new BehaviourTools(rabbit, world);
        assertNotNull(climbingLeftContinue2.newState(behaviourTools, climbing));

        World world1 = TextWorldManip.createEmptyWorld(10, 10);
        rabbit = new Rabbit(1, 1, RIGHT, Rabbit.Type.RABBIT);
        world1.blockTable.add(new Block(2, 0, EARTH, FLAT, 4));
        behaviourTools = new BehaviourTools(rabbit, world1);
        assertNotNull(climbingLeftContinue2.newState(behaviourTools, climbing));
    }
    
    /*
     * Purpose: Test ClimbingLeftContinue1::behave().
     * Input: behave(), rabbit.y -> rabbit.y - 1, climbing.abilityActive -> true
     * Expected: true
     */
    @Test
    public void behave() {
        assertTrue(climbingLeftContinue2.behave(world, rabbit, climbing));
    }
}

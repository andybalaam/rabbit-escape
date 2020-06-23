package rabbitescape.engine.behaviours.climbing.tests;

import static org.junit.Assert.*;
import static rabbitescape.engine.ChangeDescription.State.RABBIT_CLIMBING_LEFT_END;
import static rabbitescape.engine.Direction.RIGHT;
import static rabbitescape.engine.Direction.LEFT;
import static rabbitescape.engine.Block.Material.EARTH;
import static rabbitescape.engine.Block.Shape.FLAT;
import static rabbitescape.engine.Block.Shape.UP_RIGHT;

import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.engine.World;
import rabbitescape.engine.behaviours.Climbing;
import rabbitescape.engine.behaviours.climbing.ClimbingLeftEnd;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.BehaviourTools;
import rabbitescape.engine.Block;

import org.junit.Before;
import org.junit.Test;

public class ClimbingLeftEndTest {
    private World world;
    private Rabbit rabbit;
    private BehaviourTools behaviourTools;
    private Climbing climbing;
    private ClimbingLeftEnd climbingLeftEnd;
    
    /*setup for the test*/
    @Before
    public void setUp() {
    	 world = TextWorldManip.createEmptyWorld(3, 4);
    	 rabbit = new Rabbit(1, 1, RIGHT, Rabbit.Type.RABBIT);
    	 behaviourTools = new BehaviourTools(rabbit, world);
    	 climbing = new Climbing();
    	 climbingLeftEnd = new ClimbingLeftEnd();
    }
    
    /*
     * Purpose: Test ClimbingLeftEnd::getState().
     * Input: getState()
     * Expected: RABBIT_CLIMBING_LEFT_END
     */
    @Test
    public void getState() {
        assertEquals(RABBIT_CLIMBING_LEFT_END, climbingLeftEnd.getState());
    }

    /*
     * Purpose: Test ClimbingLeftEnd::newState().
     * Input: newState() ClimbingLeftContinue2() -> NotClimbing()/ClimbingRightStart()/ClimbingLeftStart()
     * Expected: NotClimbing(), ClimbingRightStart(), ClimbingLeftStart()
     */
    @Test
    public void newState() {
        assertNotNull(climbingLeftEnd.newState(behaviourTools, climbing));

        world.blockTable.add(new Block(2, 1, EARTH, FLAT, 4));
        behaviourTools = new BehaviourTools(rabbit, world);
        assertNotNull(climbingLeftEnd.newState(behaviourTools, climbing));

        rabbit = new Rabbit(1, 1, LEFT, Rabbit.Type.RABBIT);
        world.blockTable.add(new Block(0, 1, EARTH, FLAT, 4));
        behaviourTools = new BehaviourTools(rabbit, world);
        assertNotNull(climbingLeftEnd.newState(behaviourTools, climbing));
    }
    
    /*
     * Purpose: Test ClimbingLeftEnd::behave().
     * Input: behave(), rabbit.y -> rabbit.y - 1, climbing.abilityActive -> false
     * Expected: true, true
     */
    @Test
    public void behave() {
        assertTrue(climbingLeftEnd.behave(world, rabbit, climbing));

        rabbit = new Rabbit(1, 1, RIGHT, Rabbit.Type.RABBIT);
        world.blockTable.add(new Block(2, 0, EARTH, UP_RIGHT, 4));
        behaviourTools = new BehaviourTools(rabbit, world);
        assertTrue(climbingLeftEnd.behave(world, rabbit, climbing));
    }
}

package rabbitescape.engine.behaviours.climbing.tests;

import static org.junit.Assert.*;
import static rabbitescape.engine.ChangeDescription.State.RABBIT_CLIMBING_RIGHT_END;
import static rabbitescape.engine.Direction.RIGHT;
import static rabbitescape.engine.Direction.LEFT;
import static rabbitescape.engine.Block.Material.EARTH;
import static rabbitescape.engine.Block.Shape.FLAT;
import static rabbitescape.engine.Block.Shape.UP_RIGHT;

import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.engine.World;
import rabbitescape.engine.behaviours.Climbing;
import rabbitescape.engine.behaviours.climbing.ClimbingRightEnd;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.BehaviourTools;
import rabbitescape.engine.Block;

import org.junit.Before;
import org.junit.Test;

public class ClimbingRightEndTest {
    private World world;
    private Rabbit rabbit;
    private BehaviourTools behaviourTools;
    private Climbing climbing;
    private ClimbingRightEnd climbingRightEnd;
    
    /*setup for the test*/
    @Before
    public void setUp() {
    	 world = TextWorldManip.createEmptyWorld(3, 4);
    	 rabbit = new Rabbit(1, 1, RIGHT, Rabbit.Type.RABBIT);
    	 behaviourTools = new BehaviourTools(rabbit, world);
    	 climbing = new Climbing();
    	 climbingRightEnd = new ClimbingRightEnd();
    }
    
    /*
     * Purpose: Test ClimbingRightEnd::getState().
     * Input: getState()
     * Expected: RABBIT_CLIMBING_RIGHT_END
     */
    @Test
    public void getState() {
        assertEquals(RABBIT_CLIMBING_RIGHT_END, climbingRightEnd.getState());
    }

    /*
     * Purpose: Test ClimbingRightEnd::newState().
     * Input: newState() ClimbingRightEnd() -> NotClimbing()/ClimbingRightStart()/ClimbingLeftStart()
     * Expected:  NotClimbing(), ClimbingRightStart(), ClimbingLeftStart()
     */
    @Test
    public void newState() {
        assertNotNull(climbingRightEnd.newState(behaviourTools, climbing));

        world.blockTable.add(new Block(2, 1, EARTH, FLAT, 4));
        behaviourTools = new BehaviourTools(rabbit, world);
        assertNotNull(climbingRightEnd.newState(behaviourTools, climbing));

        rabbit = new Rabbit(1, 1, LEFT, Rabbit.Type.RABBIT);
        world.blockTable.add(new Block(0, 1, EARTH, FLAT, 4));
        behaviourTools = new BehaviourTools(rabbit, world);
        assertNotNull(climbingRightEnd.newState(behaviourTools, climbing));
    }

    /*
     * Purpose: Test ClimbingRightEnd::behave().
     * Input: behave(), character.onSlope -> true/false, climbing.abilityActive -> false
     * Expected: true, true
     */
    @Test
    public void behave() {
        assertTrue(climbingRightEnd.behave(world, rabbit, climbing));

        rabbit = new Rabbit(1, 1, RIGHT, Rabbit.Type.RABBIT);
        world.blockTable.add(new Block(2, 0, EARTH, UP_RIGHT, 4));
        behaviourTools = new BehaviourTools(rabbit, world);
        assertTrue(climbingRightEnd.behave(world, rabbit, climbing));
    }
}

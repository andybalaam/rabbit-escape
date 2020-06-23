package rabbitescape.engine.behaviours.climbing.tests;

import static org.junit.Assert.*;
import static rabbitescape.engine.ChangeDescription.State.RABBIT_CLIMBING_LEFT_BANG_HEAD;
import static rabbitescape.engine.Direction.RIGHT;
import static rabbitescape.engine.Direction.LEFT;
import static rabbitescape.engine.Block.Material.EARTH;
import static rabbitescape.engine.Block.Shape.FLAT;

import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.engine.World;
import rabbitescape.engine.behaviours.Climbing;
import rabbitescape.engine.behaviours.climbing.ClimbingLeftBangHead;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.BehaviourTools;
import rabbitescape.engine.Block;

import org.junit.Before;
import org.junit.Test;

public class ClimbingLeftBangHeadTest {
    private World world;
    private Rabbit rabbit;
    private BehaviourTools behaviourTools;
    private Climbing climbing;
    private ClimbingLeftBangHead climbingLeftBangHead;
    
    /*setup for the test*/
    @Before
    public void setUp() {
    	 world = TextWorldManip.createEmptyWorld(3, 4);
    	 rabbit = new Rabbit(1, 1, RIGHT, Rabbit.Type.RABBIT);
    	 behaviourTools = new BehaviourTools(rabbit, world);
    	 climbing = new Climbing();
    	 climbingLeftBangHead = new ClimbingLeftBangHead();
    }
    
    /*
     * Purpose: Test ClimbingLeftBangHead::getState().
     * Input: getState()
     * Expected: RABBIT_CLIMBING_LEFT_BANG_HEAD
     */
    @Test
    public void getState() {
        assertEquals(RABBIT_CLIMBING_LEFT_BANG_HEAD, climbingLeftBangHead.getState());
    }
    
    /*
    Purpose: Test ClimbingLeftBangHead::newState().
    Input: newState() ClimbingLeftBangHead() -> NotClimbing()/ClimbingRightStart()/ClimbingLeftStart()
    Expected: NotClimbing(), ClimbingRightStart(), ClimbingLeftStart()
     */
    @Test
    public void newState() {
        assertNotNull(climbingLeftBangHead.newState(behaviourTools, climbing));

        world.blockTable.add(new Block(2, 1, EARTH, FLAT, 4));
        behaviourTools = new BehaviourTools(rabbit, world);
        assertNotNull(climbingLeftBangHead.newState(behaviourTools, climbing));

        rabbit = new Rabbit(1, 1, LEFT, Rabbit.Type.RABBIT);
        world.blockTable.add(new Block(0, 1, EARTH, FLAT, 4));
        behaviourTools = new BehaviourTools(rabbit, world);
        assertNotNull(climbingLeftBangHead.newState(behaviourTools, climbing));
    }
    
    /*
     * Purpose: Test climbingLeftBangHead::behave().
     * Input: behave()
     * Expected: true
     */
    @Test
    public void behave() {
        assertTrue(climbingLeftBangHead.behave(world, rabbit, climbing));
    }
}

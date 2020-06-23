package rabbitescape.engine.behaviours.climbing.tests;

import static org.junit.Assert.*;
import static rabbitescape.engine.ChangeDescription.State.RABBIT_CLIMBING_RIGHT_BANG_HEAD;
import static rabbitescape.engine.Direction.RIGHT;
import static rabbitescape.engine.Direction.LEFT;
import static rabbitescape.engine.Block.Material.EARTH;
import static rabbitescape.engine.Block.Shape.FLAT;
import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.engine.World;
import rabbitescape.engine.behaviours.Climbing;
import rabbitescape.engine.behaviours.climbing.ClimbingRightBangHead;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.BehaviourTools;
import rabbitescape.engine.Block;

import org.junit.Before;
import org.junit.Test;

public class ClimbingRightBangHeadTest {
    private World world;
    private Rabbit rabbit;
    private BehaviourTools behaviourTools;
    private Climbing climbing;
    private ClimbingRightBangHead climbingRightBangHead;
    
    /*setup for the test*/
    @Before
    public void setUp() {
    	 world = TextWorldManip.createEmptyWorld(3, 4);
    	 rabbit = new Rabbit(1, 1, RIGHT, Rabbit.Type.RABBIT);
    	 behaviourTools = new BehaviourTools(rabbit, world);
    	 climbing = new Climbing();
    	 climbingRightBangHead = new ClimbingRightBangHead();
    }
    
    /*
     * Purpose: Test ClimbingRightBangHead::getState().
     * Input: getState()
     * Expected: RABBIT_CLIMBING_RIGHT_BANG_HEAD
     */
    @Test
    public void getState() {
        assertEquals(RABBIT_CLIMBING_RIGHT_BANG_HEAD, climbingRightBangHead.getState());
    }
    
    /*
     * Purpose: Test ClimbingRightBangHead::newState().
     * Input: newState() ClimbingRightBangHead() -> NotClimbing()/ClimbingRightStart()/ClimbingLeftStart()
     * Expected: NotClimbing(), ClimbingRightStart(), ClimbingLeftStart()
     */
    @Test
    public void newState() {
        assertNotNull(climbingRightBangHead.newState(behaviourTools, climbing));

        world.blockTable.add(new Block(2, 1, EARTH, FLAT, 4));
        behaviourTools = new BehaviourTools(rabbit, world);
        assertNotNull(climbingRightBangHead.newState(behaviourTools, climbing));

        rabbit = new Rabbit(1, 1, LEFT, Rabbit.Type.RABBIT);
        world.blockTable.add(new Block(0, 1, EARTH, FLAT, 4));
        behaviourTools = new BehaviourTools(rabbit, world);
        assertNotNull(climbingRightBangHead.newState(behaviourTools, climbing));
    }
    
    /*
     * Purpose: Test ClimbingRightBangHead::behave().
     * Input: behave()
     * Expected: true
     */
    @Test
    public void behave() {
        assertTrue(climbingRightBangHead.behave(world, rabbit, climbing));
    }

}

package rabbitescape.engine.behaviours.climbing.tests;

import static org.junit.Assert.*;
import static rabbitescape.engine.Direction.RIGHT;
import static rabbitescape.engine.Direction.LEFT;
import static rabbitescape.engine.Block.Material.EARTH;
import static rabbitescape.engine.Block.Shape.FLAT;
import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.engine.World;
import rabbitescape.engine.behaviours.Climbing;
import rabbitescape.engine.behaviours.climbing.NotClimbing;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.BehaviourTools;
import rabbitescape.engine.Block;

import org.junit.Before;
import org.junit.Test;

public class NotClimbingTest {
    private World world;
    private Rabbit rabbit;
    private BehaviourTools behaviourTools;
    private Climbing climbing;
    private NotClimbing notClimbing;
    
    /*setup for the test*/
    @Before
    public void setUp() {
    	 world = TextWorldManip.createEmptyWorld(3, 4);
    	 rabbit = new Rabbit(1, 1, RIGHT, Rabbit.Type.RABBIT);
    	 behaviourTools = new BehaviourTools(rabbit, world);
    	 climbing = new Climbing();
    	 notClimbing = new NotClimbing();
    } 
    
    /*
     * Purpose: Test NotClimbing::getState().
     * Input: getState()
     * Expected: null
     */
    @Test
    public void getState() {
        assertNull(notClimbing.getState());
    }
    
    /*
     * Purpose: Test NotClimbing::newState().
     * Input: newState() NotClimbing() -> NotClimbing()/ClimbingRightStart()/ClimbingLeftStart()
     * Expected: NotClimbing(), ClimbingRightStart(), ClimbingLeftStart()
     */
    @Test
    public void newState() {
        assertNotNull(notClimbing.newState(behaviourTools, climbing));

        world.blockTable.add(new Block(2, 1, EARTH, FLAT, 4));
        behaviourTools = new BehaviourTools(rabbit, world);
        assertNotNull(notClimbing.newState(behaviourTools, climbing));

        rabbit = new Rabbit(1, 1, LEFT, Rabbit.Type.RABBIT);
        world.blockTable.add(new Block(0, 1, EARTH, FLAT, 4));
        behaviourTools = new BehaviourTools(rabbit, world);
        assertNotNull(notClimbing.newState(behaviourTools, climbing));
    }
    
    /*
     * Purpose: Test NotClimbing::behave().
     * Input: behave()
     * Expected: false
     */
    @Test
    public void behave() {
        assertFalse(notClimbing.behave(world, rabbit, climbing));
    }
}

package rabbitescape.engine.behaviours.climbing.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ClimbingLeftBangHeadTest.class, ClimbingLeftContinue1Test.class, ClimbingLeftContinue2Test.class,
		ClimbingLeftEndTest.class, ClimbingLeftStartTest.class, ClimbingRightBangHeadTest.class,
		ClimbingRightContinue1Test.class, ClimbingRightContinue2Test.class, ClimbingRightEndTest.class,
		ClimbingRightStartTest.class, NotClimbingTest.class })
public class AllTests {

}

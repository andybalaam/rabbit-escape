package rabbitescape.engine.behaviours.bashing.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ BashingLeftTest.class, BashingRightTest.class, BashingUpLeftTest.class, BashingUpRightTest.class,
		BashingUselesslyLeftTest.class, BashingUselesslyLeftUpTest.class, BashingUselesslyRightTest.class,
		BashingUselesslyRightUpTest.class, NotBashingTest.class })
public class AllTests {

}

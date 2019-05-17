package rabbitescape.engine.config.upgrades;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import static rabbitescape.engine.util.Util.*;

import java.util.Arrays;

import org.junit.Test;

import rabbitescape.engine.config.IConfigUpgrade;

public class TestUpgradeTo01NonAndroidDummy
{
    @Test
    public void Updates_config_version_but_does_nothing_else()
    {
        // Create an empty config
        IConfigUpgrade upgrade = new UpgradeTo01NonAndroidDummy();
        TrackingConfigStorage storage = new TrackingConfigStorage();

        // This is what we are testing - run the upgrade
        upgrade.run( storage );

        // Version was updated, and nothing else was done
        assertThat(
            list( storage.log ),
            equalTo( Arrays.asList( "set( config.version, 1 )" ) )
        );
    }
}

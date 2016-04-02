package rabbitescape.engine.config.upgrades;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static rabbitescape.engine.config.ConfigKeys.*;
import static rabbitescape.engine.util.Util.*;

import org.junit.Test;

import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.ConfigSchema;
import rabbitescape.engine.config.ConfigTools;
import rabbitescape.engine.config.IConfigUpgrade;
import rabbitescape.engine.menu.ByNameConfigBasedLevelsCompleted;
import rabbitescape.engine.menu.ByNumberConfigBasedLevelsCompleted;

public class TestUpgradeTo02LevelsCompletedByName
{
    @Test
    public void Updates_config_version()
    {
        // Create an empty config
        IConfigUpgrade upgrade = new UpgradeTo02LevelsCompletedByName();

        TrackingConfigStorage storage = new TrackingConfigStorage(
            Config.CFG_VERSION, "1" );

        // This is what we are testing - run the upgrade
        upgrade.run( storage );

        // Version was updated, and nothing else was done
        assertThat(
            list( storage.log ),
            hasItem( "set( config.version, 2 )" )
        );
    }

    @Test
    public void Does_nothing_if_no_levels_completed()
    {
        // Create a config with levels completed in the Easy set
        Map<String, Integer> easy5Done = new HashMap<String, Integer>();
        easy5Done.put( "easy", 5 );

        IConfigUpgrade upgrade = new UpgradeTo02LevelsCompletedByName();

        TrackingConfigStorage storage = new TrackingConfigStorage(
            Config.CFG_VERSION, "1" );

        upgrade.run( storage );

        assertThat(
            storage.log,
            equalTo(
                Arrays.asList(
                    "get( config.version ) = 1",
                    "get( levels.completed ) = null",
                    "set( config.version, 2 )"
                )
            )
        );
    }

    @Test
    public void Inserts_the_names_of_completed_levels_in_one_set()
    {
        // Create a config with levels completed in the Easy set
        Map<String, Integer> easy5Done = new HashMap<String, Integer>();
        easy5Done.put( "easy", 5 );

        IConfigUpgrade upgrade = new UpgradeTo02LevelsCompletedByName();

        TrackingConfigStorage storage = new TrackingConfigStorage(
            Config.CFG_VERSION, "1",
            "levels.completed", ConfigTools.mapToString( easy5Done )
        );

        upgrade.run( storage );

        String namesList =
            "[" +
                "\"bashing_practice\"," +
                "\"block_before_you_drop\"," +
                "\"build_your_way_out\"," +
                "\"digging_practice\"," +
                "\"get_down\"" +
            "]";

        assertThat(
            storage.log,
            hasItem( String.format( "set( levels.completed, %s )", namesList ) )
        );
    }

    @Test
    public void Inserts_the_names_of_completed_levels_in_multiple_set()
    {
        // Create a config with levels completed in the Easy set
        Map<String, Integer> easy5Done = new HashMap<String, Integer>();
        easy5Done.put( "easy", 20 );
        easy5Done.put( "medium", 4 );
        easy5Done.put( "hard", 7 );
        easy5Done.put( "outdoors", 2 );
        easy5Done.put( "arcade", 0 );

        IConfigUpgrade upgrade = new UpgradeTo02LevelsCompletedByName();

        TrackingConfigStorage storage = new TrackingConfigStorage(
            Config.CFG_VERSION, "1",
            "levels.completed", ConfigTools.mapToString( easy5Done )
        );

        upgrade.run( storage );

        String namesList =
            "[" +
                "\"across_the_void\"," +
                "\"bashing_practice\"," +
                "\"block_before_you_drop\"," +
                "\"build_your_way_out\"," +
                "\"choppy\"," +
                "\"cliff_face\"," +
                "\"climbing_practice\"," +
                "\"come_together\"," +
                "\"crowd\"," +
                "\"dig_quick\"," +
                "\"digging_practice\"," +
                "\"easy_for_some\"," +
                "\"face_the_right_way\"," +
                "\"flat_back\"," +
                "\"get_down\"," +
                "\"get_in_the_burrow\"," +
                "\"give_us_a_leg_up\"," +
                "\"go_on_without_us\"," +
                "\"keep_it_simple\"," +
                "\"leaky_bucket\"," +
                "\"minecraft\"," +
                "\"minefield\"," +
                "\"placement\"," +
                "\"quadrabbit\"," +
                "\"saw\"," +
                "\"saw_tooth\"," +
                "\"stairs\"," +
                "\"the_lone_bridger\"," +
                "\"three_strikes\"," +
                "\"to_the_top\"," +
                "\"tree_climb\"," +
                "\"walls\"," +
                "\"you_wait_here\"" +
            "]";

        assertThat(
            storage.log,
            hasItem( String.format( "set( levels.completed, %s )", namesList ) )
        );
    }

    @Test
    public void Levels_completed_agrees_before_and_after_upgrade()
    {
        // Build some v1 Config
        ConfigSchema definition = normalConfigDefinition();
        TrackingConfigStorage storage = new TrackingConfigStorage(
            Config.CFG_VERSION, "1" );
        Config oldConfig = new Config(
            definition, storage, new UpgradeTo01NonAndroidDummy() );

        // Set some levels completed
        ByNumberConfigBasedLevelsCompleted oldLevelsCompleted =
            new ByNumberConfigBasedLevelsCompleted( oldConfig );

        oldLevelsCompleted.setCompletedLevel( "easy", 3 );
        oldLevelsCompleted.setCompletedLevel( "02_medium", 5 );

        // This is what we are testing - upgrade to the v2 config format
        Config newConfig = new Config(
            definition,
            storage,
            new UpgradeTo01NonAndroidDummy(),
            new UpgradeTo02LevelsCompletedByName()
        );

        // Use the new levels completed to ask what is completed
        ByNameConfigBasedLevelsCompleted newLevelsCompleted =
            new ByNameConfigBasedLevelsCompleted(
                newConfig,
                UpgradeTo02LevelsCompletedByName.levelNames
            );

        // The same levels are completed
        assertThat(
            newLevelsCompleted.highestLevelCompleted( "easy" ),
            equalTo( 3 )
        );

        assertThat(
            newLevelsCompleted.highestLevelCompleted( "medium" ),
            equalTo( 5 )
        );
    }

    private ConfigSchema normalConfigDefinition()
    {
        ConfigSchema definition = new ConfigSchema();
        definition.set(
            CFG_LEVELS_COMPLETED,
            "{}",
            "Which level you have got to in each level set."
        );
        return definition;
    }
}

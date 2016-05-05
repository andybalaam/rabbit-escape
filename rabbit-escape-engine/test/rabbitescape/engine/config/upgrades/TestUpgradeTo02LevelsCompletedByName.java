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
import rabbitescape.engine.config.StandardConfigSchema;
import rabbitescape.engine.menu.ByNameConfigBasedLevelsCompleted;
import rabbitescape.engine.menu.ByNumberConfigBasedLevelsCompleted;
import rabbitescape.engine.menu.LevelsList;
import rabbitescape.engine.menu.LevelsList.LevelInfo;
import rabbitescape.engine.menu.LevelsList.LevelSetInfo;

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
    public void Inserts_the_names_of_completed_levels_in_multiple_sets()
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
        ConfigSchema v1definition = normalv1ConfigDefinition();
        TrackingConfigStorage storage = new TrackingConfigStorage(
            Config.CFG_VERSION, "1" );
        Config oldConfig = new Config(
            v1definition, storage, new UpgradeTo01NonAndroidDummy() );

        // Set some levels completed
        ByNumberConfigBasedLevelsCompleted oldLevelsCompleted =
            new ByNumberConfigBasedLevelsCompleted( oldConfig );

        oldLevelsCompleted.setCompletedLevel( "easy", 3 );
        oldLevelsCompleted.setCompletedLevel( "02_medium", 5 );

        ByNameConfigBasedLevelsCompleted newLevelsCompleted =
            makeNewLevelsCompleted( storage );

        // The same levels are completed
        assertThat(
            newLevelsCompleted.highestLevelCompleted( "01_easy" ),
            equalTo( 3 )
        );

        assertThat(
            newLevelsCompleted.highestLevelCompleted( "02_medium" ),
            equalTo( 5 )
        );
    }

    @Test
    public void Levels_completed_works_when_no_existing_config()
    {
        // Old config with no entries for levels.completed
        TrackingConfigStorage storage = new TrackingConfigStorage();

        ByNameConfigBasedLevelsCompleted newLevelsCompleted =
            makeNewLevelsCompleted( storage );

        // The same levels are completed
        assertThat(
            newLevelsCompleted.highestLevelCompleted( "01_easy" ),
            equalTo( 0 )
        );

        assertThat(
            newLevelsCompleted.highestLevelCompleted( "02_medium" ),
            equalTo( 0 )
        );

        assertThat(
            newLevelsCompleted.highestLevelCompleted( "03_hard" ),
            equalTo( 0 )
        );

        assertThat(
            newLevelsCompleted.highestLevelCompleted( "04_outdoors" ),
            equalTo( 0 )
        );

        assertThat(
            newLevelsCompleted.highestLevelCompleted( "05_arcade" ),
            equalTo( 0 )
        );
    }

    @Test
    public void Levels_completed_agrees_0_6_1_to_0_9()
    {
        // Build some config as created by v0.6.1.  I also checked after 0.7.1,
        // when level set dirs were numbered, and the config looks the same.
        // I also tested with 0.8.0.7, which is after the dummy config upgrade
        // on PC, and it was the same again.
        // TODO: check the same sequence of upgrades on Android.

        TrackingConfigStorage storage = new TrackingConfigStorage(
            "levels.completed", "{\"easy\":5,\"hard\":1,\"medium\":3}"
        );

        ByNameConfigBasedLevelsCompleted newLevelsCompleted =
            makeNewLevelsCompleted( storage );

        // The same levels are completed
        assertThat(
            newLevelsCompleted.highestLevelCompleted( "01_easy" ),
            equalTo( 5 )
        );

        assertThat(
            newLevelsCompleted.highestLevelCompleted( "02_medium" ),
            equalTo( 3 )
        );

        assertThat(
            newLevelsCompleted.highestLevelCompleted( "03_hard" ),
            equalTo( 1 )
        );

        assertThat(
            newLevelsCompleted.highestLevelCompleted( "04_outdoors" ),
            equalTo( 0 )
        );

        assertThat(
            newLevelsCompleted.highestLevelCompleted( "05_arcade" ),
            equalTo( 0 )
        );
    }

    // ---

    private static ByNameConfigBasedLevelsCompleted makeNewLevelsCompleted(
        TrackingConfigStorage storage )
    {
        ConfigSchema v2definition = new ConfigSchema();
        StandardConfigSchema.setSchema( v2definition );

        // This is what we are testing - upgrade to the v2 config format
        Config newConfig = new Config(
            v2definition,
            storage,
            new UpgradeTo01NonAndroidDummy(),
            new UpgradeTo02LevelsCompletedByName()
        );

        // Use the new levels completed to ask what is completed
        ByNameConfigBasedLevelsCompleted newLevelsCompleted =
            new ByNameConfigBasedLevelsCompleted(
                newConfig,
                levelNamesAsAt0_8_0_9()
            );
        return newLevelsCompleted;
    }

    private static LevelsList levelNamesAsAt0_8_0_9()
    {
        // Copied from MenuDefinition.allLevels at version 0.8.0.9, but with the
        // level names included
        return new LevelsList(
            new LevelSetInfo(
                "Easy",
                "01_easy",
                Arrays.asList(
                    info( "Digging practice" ),
                    info( "Bashing practice" ),
                    info( "Build your way out" ),
                    info( "Block before you drop" ),
                    info( "Get down" ),
                    info( "Come together" ),
                    info( "Get in the burrow" ),
                    info( "Dig quick" ),
                    info( "Climbing practice" ),
                    info( "Cliff face" ),
                    info( "You wait here" ),
                    info( "Across the void" ),
                    info( "Give us a leg up" ),
                    info( "Three strikes" ),
                    info( "Keep it simple" ),
                    info( "The Lone Bridger" ),
                    info( "Go on without us" ),
                    info( "Face the right way" ),
                    info( "Saw tooth" ),
                    info( "To the top" )
                ),
                false
            ),
            new LevelSetInfo(
                "Medium",
                "02_medium",
                Arrays.asList(
                    info( "Easy for some" ),
                    info( "Placement" ),
                    info( "Minefield" ),
                    info( "Leaky Bucket" ),
                    info( "Full Bucket" ),
                    info( "One Pop Shot" ),
                    info( "Scramble" ),
                    info( "Greek" ),
                    info( "The box" ),
                    info( "Castle" ),
                    info( "Rescuer" ),
                    info( "Family" ),
                    info( "Turn around, rabbit ears" ),
                    info( "Chess" ),
                    info( "Hills" ),
                    info( "Dig?" ),
                    info( "Jet Set" ),
                    info( "Assist" ),
                    info( "Charing Cross the Void" ),
                    info( "Branches" )
                ),
                false
            ),
            new LevelSetInfo(
                "Hard",
                "03_hard",
                Arrays.asList(
                    info( "Choppy" ),
                    info( "Walls" ),
                    info( "Quadrabbit" ),
                    info( "Stairs" ),
                    info( "Minecraft" ),
                    info( "Flat Back" ),
                    info( "Crowd" ),
                    info( "Ruts" ),
                    info( "Chimneys" ),
                    info( "Three Down" ),
                    info( "Flag" ),
                    info( "Not so easy for others" ),
                    info( "Co-operative" ),
                    info( "Whizz Bang" ),
                    info( "Cliff Path" ),
                    info( "Makes Me Cross" ),
                    info( "Maze" ),
                    info( "Mates" ),
                    info( "Panic!" ),
                    info( "Mary Poppins" )
                ),
                false
            ),
            new LevelSetInfo(
                "Outdoors",
                "04_outdoors",
                Arrays.asList(
                    info( "Tree climb" ),
                    info( "Saw" ),
                    info( "C" ),
                    info( "Blockers" ),
                    info( "Platforms, small" ),
                    info( "Spare a blocker" ),
                    info( "Prison break" ),
                    info( "Rabbit hole descent" ),
                    info( "London Bridge" ),
                    info( "Home sweet home" ),
                    info( "Re-entry" ),
                    info( "Cloud bunnies" ),
                    info( "Spare 2 blockers?" ),
                    info( "Platforms, large" ),
                    info( "F" ),
                    info( "Half The World Away" ),
                    info( "Chasm" ),
                    info( "Cups and bridges" ),
                    info( "Sailing" ),
                    info( "K2" )
                ),
                false
            ),
            new LevelSetInfo(
                "Arcade",
                "05_arcade",
                Arrays.asList(
                    info( "Ghost versus pie" ),
                    info( "Cliff hanger" ),
                    info( "The matrix" ),
                    info( "Tomb raider" ),
                    info( "Invader" ),
                    info( "UFO" ),
                    info( "Breakout" ),
                    info( "Slot machine" ),
                    info( "Asteroids" ),
                    info( "Forest" ),
                    info( "Dig for victory" ),
                    info( "Keep" ),
                    info( "The traitor" ),
                    info( "The mothership" ),
                    info( "Space invaders" ),
                    info( "Meander" ),
                    info( "Tunnels" ),
                    info( "Tetris" ),
                    info( "Galton box" ),
                    info( "Catch me if you can" )
                ),
                false
            )
        );
    }

    private static LevelInfo info( String name )
    {
        return new LevelInfo( null, name );
    }

    private static ConfigSchema normalv1ConfigDefinition()
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

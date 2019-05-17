package rabbitescape.engine.config.upgrades;

import static rabbitescape.engine.config.ConfigKeys.CFG_LEVELS_COMPLETED;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.ConfigTools;
import rabbitescape.engine.config.IConfigStorage;
import rabbitescape.engine.config.IConfigUpgrade;
import rabbitescape.engine.menu.ByNameConfigBasedLevelsCompleted;
import rabbitescape.engine.menu.LevelsList;
import rabbitescape.engine.menu.LevelsList.LevelInfo;
import rabbitescape.engine.menu.LevelsList.LevelSetInfo;
import rabbitescape.engine.util.Util;

public class UpgradeTo02LevelsCompletedByName implements IConfigUpgrade
{
    @Override
    public void run( IConfigStorage storage )
    {
        Util.reAssert( storage.get( Config.CFG_VERSION ).equals( "1" ) );

        String levelsCompletedV1 = storage.get( CFG_LEVELS_COMPLETED );

        if ( levelsCompletedV1 != null )
        {
            storage.set(
                CFG_LEVELS_COMPLETED,
                upgradeLevelsCompleted( levelsCompletedV1 )
            );
        }

        storage.set( Config.CFG_VERSION, "2" );
    }

    public static final LevelsList levelNames = makeLevelNames();

    private static String upgradeLevelsCompleted( String levelsV1 )
    {
        SortedSet<String> ret = new TreeSet<String>();

        Map<String, Integer> levelsV1Map = ConfigTools.stringToMap(
            levelsV1, Integer.class );

        for ( LevelSetInfo set : levelNames )
        {
            Integer progressInSet = levelsV1Map.get( set.dirName );

            if ( progressInSet != null )
            {
                for ( int i = 0; i < progressInSet; ++i )
                {
                    ret.add( set.levels.get( i ).name );
                }
            }
        }

        return ConfigTools.setToString( ret );
    }

    // Hard-coded list of level names, exactly as they were at the time
    // immediately before the upgrade.
    private static LevelsList makeLevelNames()
    {
        List<LevelSetInfo> levelSets = new ArrayList<LevelSetInfo>();

        levelSets.add(
            new LevelSetInfo(
                null,
                "easy",
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
                )
            )
        );

        levelSets.add(
            new LevelSetInfo(
                null,
                "medium",
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
                )
            )
        );

        levelSets.add(
            new LevelSetInfo(
                null,
                "hard",
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
                )
            )
        );

        levelSets.add(
            new LevelSetInfo(
                null,
                "outdoors",
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
                )
            )
        );

        levelSets.add(
            new LevelSetInfo(
                null,
                "arcade",
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
                )
            )
        );

        return new LevelsList( levelSets );
    }

    private static LevelInfo info( String name )
    {
        return new LevelInfo(
            null, ByNameConfigBasedLevelsCompleted.canonicalName( name ) );
    }
}

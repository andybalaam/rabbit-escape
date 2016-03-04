package rabbitescape.engine.config.upgrades;

import static rabbitescape.engine.config.ConfigKeys.CFG_LEVELS_COMPLETED;

import static rabbitescape.engine.menu.ByNameConfigBasedLevelsCompleted.canonicalName;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import rabbitescape.engine.config.ConfigTools;
import rabbitescape.engine.config.IConfigStorage;
import rabbitescape.engine.config.IConfigUpgrade;
import rabbitescape.engine.util.Util;

public class UpgradeTo02LevelsCompletedByName implements IConfigUpgrade
{
    @Override
    public void run( IConfigStorage storage )
    {
        Util.reAssert( storage.get( "config.version" ).equals( "1" ) );

        String levelsCompletedV1 = storage.get( CFG_LEVELS_COMPLETED );

        if ( levelsCompletedV1 != null )
        {
            storage.set(
                CFG_LEVELS_COMPLETED,
                upgradeLevelsCompleted( levelsCompletedV1 )
            );
        }

        storage.set( "config.version", "2" );
    }

    private static Map<String, List<String>> levelNames =
        new HashMap<String, List<String>>();

    private static String upgradeLevelsCompleted( String levelsV1 )
    {
        SortedSet<String> ret = new TreeSet<String>();

        Map<String, Integer> levelsV1Map = ConfigTools.stringToMap(
            levelsV1, Integer.class );

        for ( Map.Entry<String, List<String>> e : levelNames.entrySet() )
        {
            String levelSetName = e.getKey();
            List<String> namesOfLevelsInSet = e.getValue();

            Integer progressInSet = levelsV1Map.get( levelSetName );
            if ( progressInSet != null )
            {
                for ( int i = 0; i < progressInSet; ++i )
                {
                    ret.add( namesOfLevelsInSet.get( i ) );
                }
            }
        }

        return ConfigTools.setToString( ret );
    }

    // Hard-coded list of level names, exactly as they were at the time
    // immediately before the upgrade.
    static
    {
        levelNames.put(
            "easy",
            Arrays.asList(
                canonicalName( "Digging practice" ),
                canonicalName( "Bashing practice" ),
                canonicalName( "Build your way out" ),
                canonicalName( "Block before you drop" ),
                canonicalName( "Get down" ),
                canonicalName( "Come together" ),
                canonicalName( "Get in the burrow" ),
                canonicalName( "Dig quick" ),
                canonicalName( "Climbing practice" ),
                canonicalName( "Cliff face" ),
                canonicalName( "You wait here" ),
                canonicalName( "Across the void" ),
                canonicalName( "Give us a leg up" ),
                canonicalName( "Three strikes" ),
                canonicalName( "Keep it simple" ),
                canonicalName( "The Lone Bridger" ),
                canonicalName( "Go on without us" ),
                canonicalName( "Face the right way" ),
                canonicalName( "Saw tooth" ),
                canonicalName( "To the top" )
            )
        );

        levelNames.put(
            "medium",
            Arrays.asList(
                canonicalName( "Easy for some" ),
                canonicalName( "Placement" ),
                canonicalName( "Minefield" ),
                canonicalName( "Leaky Bucket" ),
                canonicalName( "Full Bucket" ),
                canonicalName( "One Pop Shot" ),
                canonicalName( "Scramble" ),
                canonicalName( "Greek" ),
                canonicalName( "The box" ),
                canonicalName( "Castle" ),
                canonicalName( "Rescuer" ),
                canonicalName( "Family" ),
                canonicalName( "Turn around, rabbit ears" ),
                canonicalName( "Chess" ),
                canonicalName( "Hills" ),
                canonicalName( "Dig?" ),
                canonicalName( "Jet Set" ),
                canonicalName( "Assist" ),
                canonicalName( "Charing Cross the Void" ),
                canonicalName( "Branches" )
            )
        );

        levelNames.put(
            "hard",
            Arrays.asList(
                canonicalName( "Choppy" ),
                canonicalName( "Walls" ),
                canonicalName( "Quadrabbit" ),
                canonicalName( "Stairs" ),
                canonicalName( "Minecraft" ),
                canonicalName( "Flat Back" ),
                canonicalName( "Crowd" ),
                canonicalName( "Ruts" ),
                canonicalName( "Chimneys" ),
                canonicalName( "Three Down" ),
                canonicalName( "Flag" ),
                canonicalName( "Not so easy for others" ),
                canonicalName( "Co-operative" ),
                canonicalName( "Whizz Bang" ),
                canonicalName( "Cliff Path" ),
                canonicalName( "Makes Me Cross" ),
                canonicalName( "Maze" ),
                canonicalName( "Mates" ),
                canonicalName( "Panic!" ),
                canonicalName( "Mary Poppins" )
            )
        );

        levelNames.put(
            "outdoors",
            Arrays.asList(
                canonicalName( "Tree climb" ),
                canonicalName( "Saw" ),
                canonicalName( "C" ),
                canonicalName( "Blockers" ),
                canonicalName( "Platforms, small" ),
                canonicalName( "Spare a blocker" ),
                canonicalName( "Prison break" ),
                canonicalName( "Rabbit hole descent" ),
                canonicalName( "London Bridge" ),
                canonicalName( "Home sweet home" ),
                canonicalName( "Re-entry" ),
                canonicalName( "Cloud bunnies" ),
                canonicalName( "Spare 2 blockers?" ),
                canonicalName( "Platforms, large" ),
                canonicalName( "F" ),
                canonicalName( "Half The World Away" ),
                canonicalName( "Chasm" ),
                canonicalName( "Cups and bridges" ),
                canonicalName( "Sailing" ),
                canonicalName( "K2" )
            )
        );

        levelNames.put(
            "arcade",
            Arrays.asList(
                canonicalName( "Ghost versus pie" ),
                canonicalName( "Cliff hanger" ),
                canonicalName( "The matrix" ),
                canonicalName( "Tomb raider" ),
                canonicalName( "Invader" ),
                canonicalName( "UFO" ),
                canonicalName( "Breakout" ),
                canonicalName( "Slot machine" ),
                canonicalName( "Asteroids" ),
                canonicalName( "Forest" ),
                canonicalName( "Dig for victory" ),
                canonicalName( "Keep" ),
                canonicalName( "The traitor" ),
                canonicalName( "The mothership" ),
                canonicalName( "Space invaders" ),
                canonicalName( "Meander" ),
                canonicalName( "Tunnels" ),
                canonicalName( "Tetris" ),
                canonicalName( "Galton box" ),
                canonicalName( "Catch me if you can" )
            )
        );
    }
}

package rabbitescape.engine;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.IsEqual.*;
import static rabbitescape.engine.ChangeDescription.State.*;
import static rabbitescape.engine.Tools.*;
import static rabbitescape.engine.textworld.TextWorldManip.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import rabbitescape.engine.textworld.ItemsLineProcessor;

public class TestTextWorldManip
{
    @Test
    public void Round_trip_basic_world()
    {
        String[] lines = {
            "###########",
            "#  Q    c #",
            "#\\      i/#",
            "#  O     d#",
            "#r j )(  b#",
            "###########"
        };

        assertThat(
            renderWorld( createWorld( lines ), false, false ),
            equalTo( lines )
        );

        // Also, shouldn't throw if we render this with states
        renderWorld( createWorld( lines ), true, false );
    }

    @Test
    public void Basic_world_with_coords()
    {
        String[] world = {
            "############",
            "#          #",
            "#          #",
            "#          #",
            "#          #",
            "############"
        };

        String[] expected = {
            "00 ############",
            "01 #          #",
            "02 #          #",
            "03 #          #",
            "04 #          #",
            "05 ############",
            "   000000000011",
            "   012345678901",
        };

        assertThat(
            renderWorld( createWorld( world ), false, true ),
            equalTo( expected )
        );
    }

    @Test
    public void Walking_rabbits()
    {
        World world = createEmptyWorld( 3, 3 );

        ChangeDescription desc = new ChangeDescription();
        desc.add( 0, 0, RABBIT_WALKING_RIGHT );
        desc.add( 2, 1, RABBIT_WALKING_LEFT  );
        desc.add( 1, 2, RABBIT_WALKING_RIGHT );

        assertThat(
            renderChangeDescription( world, desc, false ),
            equalTo(
                " > ",
                " < ",
                "  >"
            )
        );
    }

    @Test
    public void Turning_rabbits()
    {
        World world = createEmptyWorld( 3, 2 );

        ChangeDescription desc = new ChangeDescription();
        desc.add( 0, 0, RABBIT_TURNING_LEFT_TO_RIGHT );
        desc.add( 2, 0, RABBIT_TURNING_RIGHT_TO_LEFT  );

        assertThat(
            renderChangeDescription( world, desc, false ),
            equalTo(
                "| ?",
                "   "
            )
        );
    }

    @Test
    public void Rising_rabbits_right()
    {
        World world = createEmptyWorld( 5, 8 );

        ChangeDescription desc = new ChangeDescription();
        desc.add( 0, 1, RABBIT_RISING_RIGHT_START );
        desc.add( 0, 3, RABBIT_RISING_RIGHT_CONTINUE );
        desc.add( 0, 5, RABBIT_RISING_RIGHT_END );
        desc.add( 0, 7, RABBIT_TURNING_RIGHT_TO_LEFT_RISING );

        assertThat(
            renderChangeDescription( world, desc, false ),
            equalTo(
                "     ",
                " ~   ",
                " $   ",
                "     ",
                " '   ",
                "     ",
                "     ",
                "?    "
            )
        );
    }

    @Test
    public void Rising_rabbits_left()
    {
        World world = createEmptyWorld( 5, 8 );

        ChangeDescription desc = new ChangeDescription();
        desc.add( 4, 1, RABBIT_RISING_LEFT_START );
        desc.add( 4, 3, RABBIT_RISING_LEFT_CONTINUE  );
        desc.add( 4, 5, RABBIT_RISING_LEFT_END  );
        desc.add( 4, 7, RABBIT_TURNING_LEFT_TO_RIGHT_RISING  );

        assertThat(
            renderChangeDescription( world, desc, false ),
            equalTo(
                "     ",
                "   ` ",
                "   ^ ",
                "     ",
                "   ! ",
                "     ",
                "     ",
                "    |"
            )
        );
    }

    @Test
    public void Lowering_rabbits_right()
    {
        World world = createEmptyWorld( 5, 8 );

        ChangeDescription desc = new ChangeDescription();
        desc.add( 0, 0, RABBIT_LOWERING_RIGHT_START );
        desc.add( 0, 2, RABBIT_LOWERING_RIGHT_CONTINUE  );
        desc.add( 0, 4, RABBIT_LOWERING_RIGHT_END  );
        desc.add( 0, 6, RABBIT_TURNING_LEFT_TO_RIGHT_LOWERING  );

        assertThat(
            renderChangeDescription( world, desc, false ),
            equalTo(
                "     ",
                " -   ",
                "     ",
                " @   ",
                " _   ",
                "     ",
                "[    ",
                "     "
            )
        );
    }

    @Test
    public void Lowering_rabbits_left()
    {
        World world = createEmptyWorld( 5, 8 );

        ChangeDescription desc = new ChangeDescription();
        desc.add( 4, 0, RABBIT_LOWERING_LEFT_START );
        desc.add( 4, 2, RABBIT_LOWERING_LEFT_CONTINUE );
        desc.add( 4, 4, RABBIT_LOWERING_LEFT_END  );
        desc.add( 4, 6, RABBIT_TURNING_LEFT_TO_RIGHT_LOWERING  );

        assertThat(
            renderChangeDescription( world, desc, false ),
            equalTo(
                "     ",
                "   = ",
                "     ",
                "   % ",
                "   + ",
                "     ",
                "    [",
                "     "
            )
        );
    }

    @Test
    public void Falling_rabbits()
    {
        World world = createEmptyWorld( 3, 5 );

        ChangeDescription desc = new ChangeDescription();
        desc.add( 0, 0, RABBIT_FALLING );
        desc.add( 2, 1, RABBIT_FALLING  );
        desc.add( 1, 2, RABBIT_FALLING_1 );

        assertThat(
            renderChangeDescription( world, desc, false ),
            equalTo(
                "   ",
                "f  ",
                "f f",
                " ff",
                "   "
            )
        );
    }

    @Test
    public void Rabbits_falling_odd_num_squares_to_death()
    {
        World world = createEmptyWorld( 3, 2 );

        ChangeDescription desc = new ChangeDescription();
        desc.add( 0, 0, RABBIT_FALLING_1_TO_DEATH );
        desc.add( 2, 0, RABBIT_DYING_OF_FALLING_2  );

        assertThat(
            renderChangeDescription( world, desc, false ),
            equalTo(
                "  y",
                "x  "
            )
        );
    }

    @Test
    public void Rabbits_falling_even_num_squares_to_death()
    {
        World world = createEmptyWorld( 3, 2 );

        ChangeDescription desc = new ChangeDescription();
        desc.add( 0, 0, RABBIT_DYING_OF_FALLING );

        assertThat(
            renderChangeDescription( world, desc, false ),
            equalTo(
                "X  ",
                "   "
            )
        );
    }

    @Test
    public void Rabbits_walking_down_and_immediately_up()
    {
        World world = createEmptyWorld( 5, 2 );

        ChangeDescription desc = new ChangeDescription();
        desc.add( 0, 0, RABBIT_LOWERING_AND_RISING_RIGHT );
        desc.add( 4, 0, RABBIT_LOWERING_AND_RISING_LEFT );

        assertThat(
            renderChangeDescription( world, desc, false ),
            equalTo(
                " , . ",
                "     "
            )
        );
    }

    @Test
    public void Rabbits_walking_up_and_immediately_down()
    {
        World world = createEmptyWorld( 5, 2 );

        ChangeDescription desc = new ChangeDescription();
        desc.add( 0, 0, RABBIT_RISING_AND_LOWERING_RIGHT );
        desc.add( 4, 0, RABBIT_RISING_AND_LOWERING_LEFT );

        assertThat(
            renderChangeDescription( world, desc, false ),
            equalTo(
                " & m ",
                "     "
            )
        );
    }

    @Test
    public void Rabbits_falling_onto_slopes()
    {
        World world = createEmptyWorld( 8, 3 );

        ChangeDescription desc = new ChangeDescription();
        desc.add( 0, 0, RABBIT_FALLING_ONTO_LOWER_RIGHT );
        desc.add( 1, 0, RABBIT_FALLING_ONTO_RISE_RIGHT );
        desc.add( 2, 0, RABBIT_FALLING_ONTO_LOWER_LEFT );
        desc.add( 3, 0, RABBIT_FALLING_ONTO_RISE_LEFT );
        desc.add( 4, 0, RABBIT_FALLING_1_ONTO_LOWER_RIGHT );
        desc.add( 5, 0, RABBIT_FALLING_1_ONTO_RISE_RIGHT );
        desc.add( 6, 0, RABBIT_FALLING_1_ONTO_LOWER_LEFT );
        desc.add( 7, 0, RABBIT_FALLING_1_ONTO_RISE_LEFT );

        assertThat(
            renderChangeDescription( world, desc, false ),
            equalTo(
                "        ",
                "ffffehsa",
                "ehsa    "
            )
        );
    }

    @Test
    public void Tokens_falling()
    {
        World world = createEmptyWorld( 4, 2 );

        ChangeDescription desc = new ChangeDescription();
        desc.add( 0, 0, TOKEN_BASH_FALLING );
        desc.add( 1, 0, TOKEN_DIG_FALLING );
        desc.add( 2, 0, TOKEN_BRIDGE_FALLING );
        desc.add( 3, 0, TOKEN_BLOCK_FALLING );

        assertThat(
            renderChangeDescription( world, desc, false ),
            equalTo(
                "    ",
                "ffff"
            )
        );
    }

    @Test
    public void Bashing()
    {
        World world = createEmptyWorld( 3, 4 );

        ChangeDescription desc = new ChangeDescription();
        desc.add( 1, 0, RABBIT_BASHING_RIGHT );
        desc.add( 1, 1, RABBIT_BASHING_LEFT );
        desc.add( 1, 2, RABBIT_BASHING_USELESSLY_RIGHT );
        desc.add( 1, 3, RABBIT_BASHING_USELESSLY_LEFT );

        assertThat(
            renderChangeDescription( world, desc, false ),
            equalTo(
                "  K",
                "W  ",
                "  I",
                "J  "
            )
        );
    }

    @Test
    public void Climbing()
    {
        World world = createEmptyWorld( 17, 3 );

        ChangeDescription desc = new ChangeDescription();
        desc.add(  1, 1, RABBIT_CLIMBING_LEFT_START );
        desc.add(  3, 1, RABBIT_CLIMBING_LEFT_CONTINUE_1 );
        desc.add(  5, 1, RABBIT_CLIMBING_LEFT_CONTINUE_2 );
        desc.add(  7, 1, RABBIT_CLIMBING_LEFT_END );
        desc.add(  9, 1, RABBIT_CLIMBING_RIGHT_START );
        desc.add( 11, 1, RABBIT_CLIMBING_RIGHT_CONTINUE_1 );
        desc.add( 13, 1, RABBIT_CLIMBING_RIGHT_CONTINUE_2 );
        desc.add( 15, 1, RABBIT_CLIMBING_RIGHT_END );

        assertThat(
            renderChangeDescription( world, desc, false ),
            equalTo(
                "   Y YU    F F  L",
                " T       G       ",
                "                 "
            )
        );
    }

    @Test
    public void Can_supply_default_name()
    {
        assertThat(
            createWorldWithName(
                "defname", new IgnoreWorldStatsListener(), new String[] {}
            ).name,
            equalTo( "defname" )
        );
    }

    @Test
    public void Default_name_is_ignored_if_name_property_found()
    {
        String[] lines = {
            ":name=bar"
        };

        assertThat(
            createWorldWithName(
                "defname", new IgnoreWorldStatsListener(), lines ).name,
            equalTo( "bar" )
        );
    }

    @Test
    public void Can_provide_world_name()
    {
        String[] lines = {
            ":name=My World!"
        };

        assertThat(
            createWorld( lines ).name,
            equalTo( "My World!" )
        );
    }

    @Test
    public void Can_provide_world_description()
    {
        String[] lines = {
            ":description=Go here, then there"
        };

        assertThat(
            createWorld( lines ).description,
            equalTo( "Go here, then there" )
        );
    }

    @Test
    public void Can_provide_empty_description()
    {
        String[] lines = {
            ":description="
        };

        assertThat(
            createWorld( lines ).description,
            equalTo( "" )
        );
    }


    @Test
    public void Can_provide_number_of_rabbits()
    {
        String[] lines = {
            ":num_rabbits=10"
        };

        assertThat(
            createWorld( lines ).num_rabbits,
            equalTo( 10 )
        );
    }

    @Test
    public void Full_dump_shows_overlapping_things()
    {
        // Make an empty world
        World world = createWorld(
            "####",
            "#  #",
            "# /#",
            "####"
        );

        // put 2 rabbits and 2 items all in the same place, on top of a block
        world.rabbits.add( new Rabbit( 2, 2, Direction.RIGHT ) );
        world.rabbits.add( new Rabbit( 2, 2, Direction.LEFT ) );
        world.things.add( new Token( 2, 2, Token.Type.bash ) );
        world.things.add( new Token( 2, 2, Token.Type.bridge ) );

        assertThat(
            renderCompleteWorld( world, false ),
            equalTo(
                "####",
                "#  #",
                "# *#",
                "####",
                ":*=/rjbi"
            )
        );
    }

    @Test
    public void Multiple_overlapping_things_come_in_reading_order()
    {
        // Make an empty world
        World world = createWorld(
            "####",
            "#  #",
            "#\\/#",
            "####"
        );

        // Rabbits in top left
        world.rabbits.add( new Rabbit( 1, 1, Direction.RIGHT ) );
        world.rabbits.add( new Rabbit( 1, 1, Direction.LEFT ) );

        // bash and bridge in top right
        world.things.add( new Token( 2, 1, Token.Type.bash ) );
        world.things.add( new Token( 2, 1, Token.Type.bridge ) );

        // dig in bottom left and bottom right
        world.things.add( new Token( 1, 2, Token.Type.dig ) );
        world.things.add( new Token( 2, 2, Token.Type.dig ) );

        assertThat(
            renderCompleteWorld( world, false ),
            equalTo(
                "####",
                "#**#",
                "#**#",
                "####",
                ":*=rj",
                ":*=bi",
                ":*=\\d",
                ":*=/d"
            )
        );
    }

    @Test
    public void Overlap_meta_lines_can_come_straight_after_their_stars()
    {
        // This is what we are testing: we can have * meta lines any time
        // after the * they refer to
        World world = createWorld(
            "####",
            "#**#",
            ":*=rj",
            ":*=bi",
            "#**#",
            "####",
            ":*=\\d",
            ":*=/d"
        );

        // Result is the same as if they came at the end
        assertThat(
            renderCompleteWorld( world, false ),
            equalTo(
                "####",
                "#**#",
                "#**#",
                "####",
                ":*=rj",
                ":*=bi",
                ":*=\\d",
                ":*=/d"
            )
        );
    }

    @Test
    public void Blocking_state_is_preserved()
    {
        World world = createWorld(
            "rk ",
            "###"
        );

        // Pick up block token
        world.step();

        // We are now blocking
        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                " H ",
                "###"
            )
        );

        // Round trip
        World world2 = createWorld( renderCompleteWorld( world, false ) );

        // We are still blocking
        assertThat(
            renderWorld( world2, true, false ),
            equalTo(
                " H ",
                "###"
            )
        );
    }

    @Test
    public void Digging_state_is_preserved()
    {
        World world = createWorld(
            "rd ",
            "###",
            "###",
            "###"
        );

        // Pick up dig token
        world.step();

        // We are now digging
        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                " r ",
                "#D#",
                "###",
                "###"
            )
        );

        // Round trip
        World world2 = createWorld( renderCompleteWorld( world, false ) );

        // We are still digging
        assertThat(
            renderWorld( world2, true, false ),
            equalTo(
                " r ",
                "#D#",
                "###",
                "###"
            )
        );
    }

    @Test
    public void Digging_state_is_preserved_second_step()
    {
        World world = createWorld(
            "rd ",
            "###",
            "###",
            "###"
        );

        // Pick up dig token and begin digging
        world.step();
        world.step();

        // We are now digging
        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   ",
                "#D#",
                "###",
                "###"
            )
        );

        // Round trip
        @SuppressWarnings( "unused" )
        World world2 = createWorld( renderCompleteWorld( world, false ) );

        // We are still digging
        // TODO: bug: we moved on a step in the round trip
        /*assertThat(
            renderWorld( world2, true, false ),
            equalTo(
                "   ",
                "#D#",
                "###",
                "###"
            )
        );*/
    }

    @Test
    public void Bashing_state_is_preserved()
    {
        World world = createWorld(
            "rb#",
            "###"
        );

        // Pick up bash token
        world.step();

        // We are now bashing
        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                " rK",
                "###"
            )
        );

        // Round trip
        World world2 = createWorld( renderCompleteWorld( world, false ) );

        // We are still bashing
        assertThat(
            renderWorld( world2, true, false ),
            equalTo(
                " rK",
                "###"
            )
        );
    }

    @Test
    public void Round_trip_world_with_overlaps()
    {
        String[] lines = {
            "####",
            "#**#",
            "#**#",
            "####",
            ":*=rj",
            ":*=bi",
            ":*=\\d",
            ":*=/d"
        };

        assertThat(
            renderCompleteWorld( createWorld( lines ), false ),
            equalTo( lines )
        );
    }

    @Test
    public void Deprecated_items_are_still_allowed_in_world_serialisation()
    {
        String[] lines = {
            ":name=My X Trip",
            ":description=",
            ":num_rabbits=25",
            ":num_to_save=4",
            ":rabbit_delay=2",
            ":num_saved=5",
            ":num_killed=4",
            ":num_waiting=16",
            ":intro=true",                  // Deprecated
            ":paused=false",
            ":ready_to_explode_all=false",  // Deprecated
            ":bash=1",
            ":block=4",
            ":bridge=3",
            ":dig=2",
            ":explode=5",
            "####",
            "#**#",
            "#**#",
            "####",
            ":*=rj",
            ":*=bi",
            ":*=\\d",
            ":*=/d"
        };

        World world = createWorld( lines );

        // Just a basic sanity check
        assertThat( world.name, equalTo( "My X Trip" ) );
    }

    @Test
    public void Round_trip_world_with_metadata()
    {
        String[] lines = {
            ":name=My X Trip",
            ":description=",
            ":num_rabbits=25",
            ":num_to_save=4",
            ":rabbit_delay=2",
            ":num_saved=5",
            ":num_killed=4",
            ":num_waiting=16",
            ":paused=false",
            ":bash=1",
            ":block=4",
            ":bridge=3",
            ":dig=2",
            ":explode=5",
            "####",
            "#**#",
            "#**#",
            "####",
            ":*=rj",
            ":*=bi",
            ":*=\\d",
            ":*=/d"
        };

        assertThat(
            renderCompleteWorld( createWorld( lines ), true ),
            equalTo( lines )
        );
    }

    @Test
    public void State_map_from_empty_string_is_empty()
    {
        assertThat(
            ItemsLineProcessor.stateMap( "" ),
            equalTo( map() )
        );
    }

    @Test
    public void Single_field_state_map()
    {
        assertThat(
            ItemsLineProcessor.stateMap( "adsfg.foo:9" ),
            equalTo( map( "adsfg.foo", "9" ) )
        );
    }

    @Test
    public void Multiple_field_state_map()
    {
        assertThat(
            ItemsLineProcessor.stateMap( "adsfg.foo:9,x:6,Agf.d.9:10" ),
            equalTo(
                map(
                    "adsfg.foo", "9",
                    "x",         "6",
                    "Agf.d.9",   "10"
                )
            )
        );
    }

    private static Map<String, String> map( String... keysAndValues )
    {
        assert ( keysAndValues.length % 2 ) == 0;

        Map<String, String> ret = new HashMap<String, String>();

        for ( int i = 0; i < keysAndValues.length; i += 2 )
        {
            ret.put( keysAndValues[i], keysAndValues[i+1] );
        }

        return ret;
    }

    @Test
    public void Round_trip_world_with_state()
    {
        String[] lines = {
            ":name=My Round Trip",
            ":description=Go around",
            ":num_rabbits=25",
            ":num_to_save=4",
            ":rabbit_delay=2",
            ":num_saved=5",
            ":num_killed=4",
            ":num_waiting=16",
            ":paused=true",
            ":bash=1",
            ":bridge=3",
            ":dig=2",
            "###########",
            "#         #",
            "#         #",
            "# * * * * #",
            "###########",
            ":*=r{Bashing.stepsOfBashing:1}Q{Entrance.timeToNextRabbit:3}",
            ":*=j{Bridging.bigSteps:1,Bridging.bridgeType:DOWN_UP,Bridging.smallSteps:1,onSlope:true}",
            ":*=j{Climbing.hasAbility:true}",
            ":*=j{Climbing.abilityActive:true,Climbing.hasAbility:true}",
        };

        assertThat(
            renderCompleteWorld( createWorld( lines ), true ),
            equalTo( lines )
        );
    }
    
    @Test
    public void Test_variable_rabbit_delay()
    {
        String[] lines = {
            ":rabbit_delay=1,2,3",
            ":num_rabbits=5",
            "Q                                                                            ",
            "                                                                             ",
            "#############################################################################"
        };
        
        World world = createWorld( lines );
        for ( int i = 0 ; i<16 ; i++ )
        {
            world.step();
        }
        
        String[] resultLines=renderCompleteWorld( world, false );

        String[] expectedLines = {
            "*                                                                            ",
            "      r  r  r rr                                                             ",
            "#############################################################################",
            ":*=Q{Entrance.timeToNextRabbit:2}"
        };
        assertThat( resultLines, equalTo( expectedLines ));

    }
}

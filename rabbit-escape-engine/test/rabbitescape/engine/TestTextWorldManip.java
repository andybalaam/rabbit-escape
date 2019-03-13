package rabbitescape.engine;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.fail;
import static rabbitescape.engine.ChangeDescription.State.*;
import static rabbitescape.engine.Tools.*;
import static rabbitescape.engine.textworld.TextWorldManip.*;
import static rabbitescape.engine.Rabbit.Type.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static rabbitescape.engine.util.Util.*;

import rabbitescape.engine.World.CompletionState;
import rabbitescape.engine.solution.Solution;
import rabbitescape.engine.solution.SolutionExceptions;
import rabbitescape.engine.solution.SolutionParser;
import rabbitescape.engine.solution.SolutionRunner;
import rabbitescape.engine.solution.SolutionExceptions.RanPastEnd;
import rabbitescape.engine.textworld.ArrayByKeyElementMissing;
import rabbitescape.engine.textworld.DuplicateMetaKey;
import rabbitescape.engine.textworld.ItemsLineProcessor;
import rabbitescape.engine.textworld.LineProcessor;
import rabbitescape.engine.util.Util.IdxObj;

public class TestTextWorldManip
{
    @Test
    public void Round_trip_basic_world()
    {
        String[] lines = {
            "###########",
            "#  Q  A c #",
            "#\\  M   i/#",
            "#  O     d#",
            "#r j )(  b#",
            "# t y     #",
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
    public void Can_obfuscate_hints()
    {
        String[] lines = {
            ":hint.1.code=_Uf?>3ZH_8>U>{3",
            ":hint.2.code=@XW@W:)e+:+ ",
            ":hint.3.code=X5g..T[6X4["
        };

        World world = createWorld( lines );

        assertThat( world.hints[0], equalTo( "Select the bash" ) );
        assertThat( world.hints[1], equalTo( "Use the bash" ) );
        assertThat( world.hints[2], equalTo( "Be the bash" ) );
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
    public void Empty_lines_are_treated_as_spaces()
    {
        World world = createWorld(
            "####",
            "",
            "#  #",
            ""
        );

        assertThat(
            renderCompleteWorld( world, false ),
            equalTo(
                "####",
                "    ",
                "#  #",
                "    "
            )
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
        world.rabbits.add( new Rabbit( 2, 2, Direction.RIGHT, RABBIT ) );
        world.rabbits.add( new Rabbit( 2, 2, Direction.LEFT, RABBIT ) );
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
        world.rabbits.add( new Rabbit( 1, 1, Direction.RIGHT, RABBIT ) );
        world.rabbits.add( new Rabbit( 1, 1, Direction.LEFT, RABBIT ) );

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
            renderCompleteWorld( world, false, false ),
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
    public void Object_properties_are_excluded_when_runtime_meta_is_off()
    {
        World world = createWorld(
            "*",
            ":*=r{Blocking.abilityActive:true}k"  // Make blocking active
        );

        world.rabbits.get( 0 ).restoreFromState( map("Blocking.abilityActive", "true") );

        assertThat(
            renderCompleteWorld( world, false, false ),
            equalTo(
                "*",
                ":*=rk"  // No {Blocking.abilityActive:true} because runtime
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
            renderCompleteWorld( createWorld( lines ), false, false ),
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
            ":author_name=Alice Jones",
            ":author_url=http://example.com",
            ":hint.1=foo\nbar",
            ":hint.2=baz",
            ":hint.3=bash",
            ":num_rabbits=25",
            ":num_to_save=4",
            ":rabbit_delay=2",
            ":music=",
            ":num_saved=5",
            ":num_killed=4",
            ":num_waiting=16",
            ":rabbit_index_count=7",
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
            ":*=r{index:5}j{index:7}",
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
            ":author_name=bob",
            ":author_url=",
            ":hint.1=",
            ":hint.2=",
            ":hint.3=",
            ":num_rabbits=25",
            ":num_to_save=4",
            ":rabbit_delay=2",
            ":music=",
            ":num_saved=5",
            ":num_killed=4",
            ":num_waiting=16",
            ":rabbit_index_count=4",
            ":paused=true",
            ":bash=1",
            ":bridge=3",
            ":dig=2",
            "###########",
            "#         #",
            "#         #",
            "# * * * * #",
            "###########",
            ":*=r{Bashing.stepsOfBashing:1,index:1}Q{Entrance.timeToNextRabbit:3}",
            ":*=j{Bridging.bigSteps:1,Bridging.bridgeType:DOWN_UP,Bridging.smallSteps:1,index:2,onSlope:true}",
            ":*=j{Climbing.hasAbility:true,index:3}",
            ":*=j{Climbing.abilityActive:true,Climbing.hasAbility:true,index:4}",
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

        String[] resultLines = renderCompleteWorld( world, false, false );

        String[] expectedLines = {
            "*                                                                            ",
            "      r  r  r rr                                                             ",
            "#############################################################################",
            ":*=Q{Entrance.timeToNextRabbit:2}"
        };
        assertThat( resultLines, equalTo( expectedLines ));

    }

    /**
     * @brief Test an example world with variable rabbit_delay.
     * Parse it, reserialise it, and test for changes.
     */
    @Test
    public void Round_trip_for_variable_delay_world()
    {
        String[] lines = {
            ":name=var delay round trip",
            ":description=trippy",
            ":author_name=cyril",
            ":author_url=",
            ":hint.1=take",
            ":hint.2=a",
            ":hint.3=hint",
            ":solution.1=",
            ":solution.2=",
            ":solution.3=",
            ":num_rabbits=20",
            ":num_to_save=18",
            ":rabbit_delay=10,3,2,10",
            ":music=",
            ":num_saved=0",
            ":num_killed=0",
            ":num_waiting=20",
            ":rabbit_index_count=0",
            ":paused=false",
            "#######",
            "#Q   Q#",
            "#     #",
            "#######"
        };

        assertThat(
            renderCompleteWorld( createWorld( lines ), true ),
            equalTo( lines )
        );
    }

    @Test
    public void Comments_for_string_arrays_by_key_associate_correctly()
    {
        String[] lines = {
            ":name=Comments",
            ":description=verbose",
            ":author_name=bob",
            ":author_url=",
            "% something erudite",
            ":hint.1=take",
            "% insight",
            ":hint.2=a",
            "% wisdom regarding hint.3",
            ":hint.3=hint",
            ":hint.4=hint",
            ":hint.5=hint",
            ":hint.6=hint",
            ":hint.7=hint",
            ":hint.8=hint",
            "% some acumen",
            ":hint.9=hint",
            "% sagacity personified",
            ":hint.10=hint",
            ":hint.11=hint",
            "% deep understanding",
            ":hint.12=hint",
            ":solution.1=",
            "% a lot of rabbits",
            ":num_rabbits=20",
            ":num_to_save=18",
            ":rabbit_delay=10,3,2,10",
            ":music=",
            ":num_saved=0",
            ":num_killed=0",
            ":num_waiting=20",
            ":rabbit_index_count=0",
            ":paused=false",
            "#######",
            "#Q   Q#",
            "#     #",
            "#######",
        };

        assertThat(
            renderCompleteWorld( createWorld( lines ), true ),
            equalTo( lines )
        );
    }

    @Test
    public void Comments_for_abilities_round_trip()
    {
        String[] lines = {
            ":name=Comments",
            ":description=verbose",
            ":author_name=bob",
            ":author_url=",
            ":num_rabbits=20",
            ":num_to_save=18",
            ":rabbit_delay=10,3,2,10",
            ":music=",
            ":num_saved=0",
            ":num_killed=0",
            ":num_waiting=20",
            ":rabbit_index_count=0",
            ":paused=false",
            "% a",
            ":bash=1",
            "% b",
            "% c",
            ":block=4",
            "% d",
            ":bridge=3",
            "% e",
            ":climb=5",
            ":dig=2",
            "%f",
            ":explode=6",
            "#######",
            "#Q   Q#",
            "#     #",
            "#######",
        };

        assertThat(
            renderCompleteWorld( createWorld( lines ), true ),
            equalTo( lines )
        );
    }

    @Test
    public void Round_trip_comments()
    {
        String[] lines = {
            ":name=Comments",
            "% desc 1.",
            "% desc 2. can have 2 comment line about something ",
            ":description=verbose",
            "% I love bob's work",
            ":author_name=bob",
            "% his website is great",
            ":author_url=",
            "% something erudite about hint.1",
            ":hint.1=take",
            ":hint.2=a",
            "% wisdom regarding hint.3",
            ":hint.3=hint",
            ":solution.1=",
            "% s2 looks like cheeating",
            ":solution.2=",
            ":solution.3=",
            "% a lot of rabbits",
            ":num_rabbits=20",
            "% save how many?",
            ":num_to_save=18",
            "% why are we waiting?",
            ":rabbit_delay=10,3,2,10",
            ":music=",
            "% some already out: no",
            ":num_saved=0",
            "% dead already",
            ":num_killed=0",
            "% the bunnies are queuing in the pre-life",
            ":num_waiting=20",
            ":rabbit_index_count=20",
            "% paused ",
            ":paused=false",
            "% pretty ascii art",
            "#######",
            "#Q   Q#",
            "# *** #",
            "#######",
            "% starpoint comment",
            ":*=r{index:1}r{index:2}",
            ":*=j{index:3}j{index:4}",
            ":*=r{index:5}j{index:20}",
            "% comments are also OK after",
            "% all the substantive metadata"
        };

        assertThat(
            renderCompleteWorld( createWorld( lines ), true ),
            equalTo( lines )
        );
    }

    @Test
    public void Round_trip_comments_move_with_meta()
    {
        String[] lines = {
            "% something erudite about hint.1",
            ":hint.1=take",
            ":hint.2=a",
            "% wisdom regarding hint.3",
            ":hint.3=hint",
            ":solution.1=",
            "% a lot of rabbits",
            ":num_rabbits=20",
            "% save how many?",
            ":num_to_save=18",
            "% why are we waiting?",
            ":rabbit_delay=10,3,2,10",
            "% some already out: no",
            ":num_saved=0",
            "% dead already",
            ":num_killed=0",
            "% his website is great",
            ":author_url=",
            "% the bunnies are queuing in the pre-life",
            ":num_waiting=20",
            ":rabbit_index_count=0",
            "% paused ",
            ":paused=false",
            "% pretty ascii art",
            "#######",
            ":name=Comments",
            "#Q   Q#",
            "% desc 1.",
            "% desc 2. can have 2 comment line about something ",
            ":description=verbose",
            "#     #",
            "% I love bob's work",
            ":author_name=bob",
            "#######",
            "% s2 looks like cheeating",
            ":solution.2=",
            ":solution.3="
        };

        String[] expectedLines = {
            ":name=Comments",
            "% desc 1.",
            "% desc 2. can have 2 comment line about something ",
            ":description=verbose",
            "% I love bob's work",
            ":author_name=bob",
            "% his website is great",
            ":author_url=",
            "% something erudite about hint.1",
            ":hint.1=take",
            ":hint.2=a",
            "% wisdom regarding hint.3",
            ":hint.3=hint",
            ":solution.1=",
            "% s2 looks like cheeating",
            ":solution.2=",
            ":solution.3=",
            "% a lot of rabbits",
            ":num_rabbits=20",
            "% save how many?",
            ":num_to_save=18",
            "% why are we waiting?",
            ":rabbit_delay=10,3,2,10",
            ":music=",
            "% some already out: no",
            ":num_saved=0",
            "% dead already",
            ":num_killed=0",
            "% the bunnies are queuing in the pre-life",
            ":num_waiting=20",
            ":rabbit_index_count=0",
            "% paused ",
            ":paused=false",
            "% pretty ascii art",
            "#######",
            "#Q   Q#",
            "#     #",
            "#######"
        };

        assertThat(
            renderCompleteWorld( createWorld( lines ), true ),
            equalTo( expectedLines )
        );
    }

    @Test
    public void Starpoint_comments_move_to_a_block()
    {
        String[] lines = {
            ":name=Comments",
            ":description=verbose",
            ":author_name=bob",
            ":author_url=",
            ":hint.1=take",
            ":hint.2=a",
            ":hint.3=hint",
            ":solution.1=",
            ":solution.2=",
            ":solution.3=",
            ":num_rabbits=20",
            ":num_to_save=18",
            ":rabbit_delay=10,3,2,10",
            "#######",
            "#Q   Q#",
            "# *** #",
            "#######",
            "% starpoint comment 1",
            ":*=rr",
            "% starpoint comment 2",
            ":*=jj",
            "% starpoint comment 3",
            ":*=rj"
        };

        String[] expectedLines = {
            ":name=Comments",
            ":description=verbose",
            ":author_name=bob",
            ":author_url=",
            ":hint.1=take",
            ":hint.2=a",
            ":hint.3=hint",
            ":solution.1=",
            ":solution.2=",
            ":solution.3=",
            ":num_rabbits=20",
            ":num_to_save=18",
            ":rabbit_delay=10,3,2,10",
            ":music=",
            "#######",
            "#Q   Q#",
            "# *** #",
            "#######",
            "% starpoint comment 1",
            "% starpoint comment 2",
            "% starpoint comment 3",
            ":*=rr",
            ":*=jj",
            ":*=rj"
        };

        assertThat(
            renderCompleteWorld( createWorld( lines ), true, false ),
            equalTo( expectedLines )
        );
    }

    @Test
    public void World_comments_move_to_a_block()
    {
        String[] lines = {
            ":name=Comments",
            ":description=verbose",
            ":author_name=bob",
            ":author_url=",
            ":hint.1=take",
            ":hint.2=a",
            ":hint.3=hint",
            ":solution.1=",
            ":solution.2=",
            ":solution.3=",
            ":num_rabbits=20",
            ":num_to_save=18",
            ":rabbit_delay=10,3,2,10",
            "% interspersed",
            "#######",
            "% comments",
            "#Q   Q#",
            "% move to a",
            "% block",
            "#     #",
            "#######"
        };

        String[] expectedLines = {
            ":name=Comments",
            ":description=verbose",
            ":author_name=bob",
            ":author_url=",
            ":hint.1=take",
            ":hint.2=a",
            ":hint.3=hint",
            ":solution.1=",
            ":solution.2=",
            ":solution.3=",
            ":num_rabbits=20",
            ":num_to_save=18",
            ":rabbit_delay=10,3,2,10",
            ":music=",
            "% interspersed",
            "% comments",
            "% move to a",
            "% block",
            "#######",
            "#Q   Q#",
            "#     #",
            "#######"
        };

        assertThat(
            renderCompleteWorld( createWorld( lines ), true, false ),
            equalTo( expectedLines )
        );
    }


    /**
     * @brief Key meta should be unique. Test that Duplicate name
     * entries cause a DuplicateMetaKey to be thrown.
     */
    @Test( expected=DuplicateMetaKey.class )
    public void Duplicate_meta_string_is_an_error()
    {
        String[] lines = {
            ":name=1",
            ":name=2",
            "#####",
            "#r  #",
            "#####"
        };

        renderCompleteWorld( createWorld( lines ), true );
    }

    /**
     * @brief Key meta should be unique. Test that Duplicate num_rabbits
     * entries cause a DuplicateMetaKey to be thrown.
     */
    @Test( expected=DuplicateMetaKey.class )
    public void Duplicate_meta_int_is_an_error()
    {
        String[] lines = {
            ":num_rabbits=1",
            ":num_rabbits=2",
            "#####",
            "#r  #",
            "#####"
        };

        renderCompleteWorld( createWorld( lines ), true );
    }

    /**
     * @brief Key meta should be unique. Test that Duplicate paused
     * entries cause a DuplicateMetaKey to be thrown.
     */
    @Test( expected=DuplicateMetaKey.class )
    public void Duplicate_meta_boolean_is_an_error()
    {
        String[] lines = {
            ":paused=true",
            ":paused=true",
            "#####",
            "#r  #",
            "#####"
        };

        renderCompleteWorld( createWorld( lines ), true );
    }

    /**
     * @brief Key meta should be unique. Test that Duplicate rabbit_delay
     * entries cause a DuplicateMetaKey to be thrown.
     */
    @Test( expected=DuplicateMetaKey.class )
    public void Duplicate_meta_intarray_is_an_error()
    {
        String[] lines = {
            ":rabbit_delay=1,2,6",
            ":rabbit_delay=4",
            "#####",
            "#r  #",
            "#####"
        };
        renderCompleteWorld( createWorld( lines ), true );
    }

    /**
     * @brief Key meta should be unique. Test that Duplicate dig
     * entries cause a DuplicateMetaKey to be thrown.
     */
    @Test( expected=DuplicateMetaKey.class )
    public void Duplicate_ability_is_an_error()
    {
        String[] lines = {
            ":dig=10",
            ":dig=10",
            "#####",
            "#r  #",
            "#####"
        };
        renderCompleteWorld( createWorld( lines ), true );
    }

    /**
     * @brief Test an example world with some solutions.
     * Parse it, reserialise it, and test for changes.
     */
    @Test
    public void Round_trip_for_solutions()
    {
        String[] lines = {
            ":name=solution round trip",
            ":description=trippy",
            ":author_name=cyril",
            ":author_url=",
            ":hint.1=take",
            ":hint.2=a",
            ":hint.3=hint",
            ":solution.1=10;6",
            ":solution.2=bash;(3,2)",
            ":solution.3=6;5",
            ":num_rabbits=20",
            ":num_to_save=18",
            ":rabbit_delay=10,3,2,10",
            ":music=",
            ":num_saved=0",
            ":num_killed=0",
            ":num_waiting=20",
            ":rabbit_index_count=40",
            ":paused=false",
            "#######",
            "#Q   Q#",
            "#     #",
            "#######"
        };

        assertThat(
            renderCompleteWorld( createWorld( lines ), true ),
            equalTo( lines )
        );
    }

    @Test
    public void Incorrect_solution_string_throws_exception()
    {
        String[] lines = {
            ":num_rabbits=1",
            ":solution.1=5",
            "Q    ",
            "   p ",
            "#####"
        };

        try
        {
            runSolutions( lines );
            fail( "Exception expected!" );
        }
        catch ( SolutionExceptions.DidNotWin e )
        {
            assertThat( e.solutionId, equalTo( 1 ) );
            assertThat( e.commandIndex, equalTo( 2 ) );
            assertThat( e.actual, equalTo( CompletionState.LOST ) );
        }
    }

    @Test
    public void Incorrect_solution_2_string_throws_exception()
    {
        String[] lines = {
            ":num_rabbits=1",
            ":solution.1=6",
            ":solution.2=1;WON",
            "Q    ",
            "    O",
            "#####"
        };

        try
        {
            runSolutions( lines );
            fail( "Exception expected!" );
        }
        catch ( SolutionExceptions.DidNotWin e )
        {
            assertThat( e.solutionId, equalTo( 2 ) );
            assertThat( e.commandIndex, equalTo( 2 ) );
            assertThat( e.actual, equalTo( CompletionState.RUNNING ) );
        }
    }

    @Test
    public void Valid_solution_string_throws_no_exception()
    {
        String[] lines = {
            ":num_rabbits=1",
            ":solution.1=6",
            "Q    ",
            "    O",
            "#####"
        };

        runSolutions( lines );
    }

    @Test
    public void Stepping_one_past_end_throws_no_exception()
    {
        // TODO: see SolutionRunner.shouldStepWorld: yuck - this should
        //       not pass!
        String[] lines = {
            ":num_rabbits=1",
            ":solution.1=7",
            "Q    ",
            "    O",
            "#####"
        };

        runSolutions( lines );
    }

    @Test( expected = RanPastEnd.class )
    public void Solution_too_many_steps_throws_exception()
    {
        String[] lines = {
            ":num_rabbits=1",
            ":solution.1=8",
            "Q    ",
            "    O",
            "#####"
        };

        runSolutions( lines );
    }

    @Test
    public void Complex_solution_strings()
    {
        String[] lines = {
            ":num_rabbits=1",
            ":bash=2",
            ":solution.1=bash&(1,0);4;RUNNING;1;WON",
            ":solution.2=bash;(1,1);1;(0,1);RUNNING;2;WON",
            ":solution.3=bash;(1,1)&(1,1);5;RUNNING",
            ":solution.4=bash;(4,1);(3,1);3;LOST",
            "Q    ",
            "  # O",
            "#####"
        };

        runSolutions( lines );
    }

    @Test
    public void Obfuscated_solution()
    {
        String[] lines = {
            ":num_rabbits=1",
            ":bash=2",
            ":solution.1.code=WTSMTzF4=n M>DmTBMTr}3$Mz~",
            "Q    ",
            "  # O",
            "#####"
        };

        runSolutions( lines );
    }

    @Test
    public void Solutions_are_held_in_world()
    {
        String[] lines = {
            ":num_rabbits=1",
            ":bash=2",
            ":solution.1=1",
            ":solution.2=2",
            " Q   ",
            "#   #",
            "#####"
        };

        World world = createWorld( lines );

        assertThat( world.solutions[0],  equalTo( "1" ) );
        assertThat( world.solutions[1],  equalTo( "2" ) );
    }

    @Test
    public void Over_10_solutions_are_held_in_world()
    {
        String[] lines = {
            ":num_rabbits=1",
            ":bash=2",
            ":solution.1=10",
            ":solution.2=10",
            ":solution.3=10",
            ":solution.4=10",
            ":solution.5=10",
            ":solution.6=10",
            ":solution.7=10",
            ":solution.8=10",
            ":solution.9=10",
            ":solution.10=10",
            ":solution.11=10",
            " Q   ",
            "#   #",
            "#####"
        };

        World world = createWorld( lines );

        assertThat( world.solutions[0],  equalTo( "10" ) );
        assertThat( world.solutions[1],  equalTo( "10" ) );
        assertThat( world.solutions[9],  equalTo( "10" ) );
        assertThat( world.solutions[10], equalTo( "10" ) );
    }

    @Test ( expected = ArrayByKeyElementMissing.class )
    public void Disorderly_solutions_throw_exceptions()
    {
        String[] lines = {
            ":num_rabbits=1",
            ":bash=2",
            ":solution.1=10",
            ":solution.3=11",
            ":solution.2=10;bash",
            " Q   ",
            "#   #",
            "#####"
        };

        createWorld( lines );
    }

    @Test ( expected = ArrayByKeyElementMissing.class )
    public void Many_solutions_with_gaps()
    {
        String[] lines = {
            ":num_rabbits=1",
            ":bash=2",
            ":solution.1=10",
            ":solution.101=10;bash",
            " Q   ",
            "#   #",
            "#####"
        };

        World world = createWorld( lines );

        assertThat( world.solutions[0],   equalTo( "10" ) );
        assertThat( world.solutions[1],   equalTo( "" ) );
        assertThat( world.solutions[100], equalTo( "10;bash" ) );
    }

    @Test
    public void Identical_KeyListKeys_are_equal()
    {
        assertThat(
            new LineProcessor.KeyListKey( "xyz", 3 ),
            equalTo( new LineProcessor.KeyListKey( "xyz", 3 ) )
        );

        assertThat(
            new LineProcessor.KeyListKey( "xyz", 3 ).hashCode(),
            equalTo( new LineProcessor.KeyListKey( "xyz", 3 ).hashCode() )
        );
    }

    @Test
    public void Different_KeyListKeys_are_not_equal()
    {
        assertThat(
            new LineProcessor.KeyListKey( "xyz", 3 ),
            not( equalTo( new LineProcessor.KeyListKey( "xyz", 4 ) ) )
        );

        assertThat(
            new LineProcessor.KeyListKey( "xyz", 3 ).hashCode(),
            not(
                equalTo( new LineProcessor.KeyListKey( "xyz", 4 ).hashCode() )
            )
        );
    }

    @Test
    public void Can_parse_KeyListKey()
    {
        assertThat(
            LineProcessor.parseKeyListKey( "solution.1" ),
            equalTo( new LineProcessor.KeyListKey( "solution", 1 ) )
        );
    }

    @Test
    public void Parsing_non_KeyListKey_returns_no_match()
    {
        assertThat(
            LineProcessor.parseKeyListKey( "solution1" ),
            equalTo( new LineProcessor.KeyListKey( "NO KEY LIST MATCH", -1 ) )
        );
    }

    @Test
    public void Comments_only_active_at_start_of_line()
    {
        String[] lines = {
            ":name=Commentary % look at me",
            ":description=trippy",
            ":author_name=cyril",
            ":author_url=",
            ":hint.1=",
            ":hint.2=",
            ":hint.3=",
            ":num_rabbits=20",
            ":num_to_save=18",
            ":rabbit_delay=1",
            ":music=",
            ":num_saved=0",
            ":num_killed=0",
            ":num_waiting=20",
            ":rabbit_index_count=0",
            ":paused=false",
            "#######",
            "#Q   Q#",
            "#     #",
            "#######"
        };

        assertThat(
            renderCompleteWorld( createWorld( lines ), true ),
            equalTo( lines )
        );
    }

    @Test
    public void String_hash_is_reproducible()
    {
        String[] sa = new String[]{
            "Dig for victory", "Tomb raider", "Slot machine", "Space invaders", "UFO" };
        int[] h = new int[]{
             1451,              1065,          1175,           1384,             234
        };
        for ( int i = 0; i < sa.length ; i++ )
        {
            assertThat(
                LineProcessor.stringHash( sa[i] ),
                equalTo( h[i] ) );
        }


    }

    @Test
    public void Gentest_contains_extra_quotes_and_line_breaks()
    {
        String[] lines = {
            "#####",
            "# r #",
            "#####"
        };

        assertThat(
            renderWorldForTest( createWorld( lines ) ),
            equalTo(
                  "            \"#####\" + \"\\n\" +\n"
                + "            \"# r>#\" + \"\\n\" +\n"
                + "            \"#####\",\n"
            )
        );
    }

    @Test
    public void Gentest_escapes_backslashes()
    {
        String[] lines = {
            "#####",
            "#\\  #",
            "#####"
        };

        assertThat(
            renderWorldForTest( createWorld( lines ) ),
            equalTo(
                  "            \"#####\" + \"\\n\" +\n"
                + "            \"#\\\\  #\" + \"\\n\" +\n"
                + "            \"#####\",\n"
            )
        );
    }

    @Test
    public void Gentest_contains_extra_quotes_and_line_breaks_lots_of_types()
    {
        String[] lines = {
            "###########",
            "#  Q A  c #",
            "#\\      i/#",
            "#  O     d#",
            "#r j )(  b#",
            "###########"
        };

        assertThat(
            renderWorldForTest( createWorld( lines ) ),
            equalTo(
                  "            \"###########\" + \"\\n\" +\n"
                + "            \"#  Q A  c #\" + \"\\n\" +\n"
                + "            \"#\\\\   g  f/#\" + \"\\n\" +\n"
                + "            \"#  O    fd#\" + \"\\n\" +\n"
                + "            \"#r<j )(  f#\" + \"\\n\" +\n"
                + "            \"###########\",\n"
            )
        );
    }

    // ---

    private void runSolutions( String[] lines )
    {
        World world = createWorld( lines );

        for ( IdxObj<String> s : enumerate1( world.solutions ) )
        {
            Solution solution = SolutionParser.parse( s.object );
            try
            {
                SolutionRunner.runSolution( solution, world );
            }
            catch ( SolutionExceptions.ProblemRunningSolution e )
            {
                e.solutionId = s.index;
                throw e;
            }
        }
    }

}

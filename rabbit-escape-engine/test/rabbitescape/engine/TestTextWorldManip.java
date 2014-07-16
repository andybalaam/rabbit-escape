package rabbitescape.engine;

import static org.hamcrest.MatcherAssert.*;
import static rabbitescape.engine.ChangeDescription.State.*;
import static rabbitescape.engine.TextWorldManip.*;
import static rabbitescape.engine.Tools.*;

import org.junit.Test;

public class TestTextWorldManip
{
    @Test
    public void Round_trip_basic_world()
    {
        String[] lines = {
            "#####",
            "#   #",
            "#\\ /#",
            "#   #",
            "#r j#",
            "#####"
        };

        assertThat(
            renderWorld( createWorld( lines ), false ),
            equalTo( lines )
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
            renderChangeDescription( world, desc ),
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
        World world = createEmptyWorld( 3, 1 );

        ChangeDescription desc = new ChangeDescription();
        desc.add( 0, 0, RABBIT_TURNING_LEFT_TO_RIGHT );
        desc.add( 2, 0, RABBIT_TURNING_RIGHT_TO_LEFT  );

        assertThat(
            renderChangeDescription( world, desc ),
            equalTo(
                "| ?"
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
            renderChangeDescription( world, desc ),
            equalTo(
                "     ",
                " ~   ",
                " $   ",
                "     ",
                " '   ",
                "     ",
                "     ",
                "}    "
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
            renderChangeDescription( world, desc ),
            equalTo(
                "     ",
                "   ` ",
                "   ^ ",
                "     ",
                "   ! ",
                "     ",
                "     ",
                "    {"
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
            renderChangeDescription( world, desc ),
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
            renderChangeDescription( world, desc ),
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
            renderChangeDescription( world, desc ),
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
            renderChangeDescription( world, desc ),
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
            renderChangeDescription( world, desc ),
            equalTo(
                "X  ",
                "   "
            )
        );
    }

    @Test
    public void Rabbits_walking_down_and_immediately_up()
    {
        World world = createEmptyWorld( 5, 1 );

        ChangeDescription desc = new ChangeDescription();
        desc.add( 0, 0, RABBIT_LOWERING_AND_RISING_RIGHT );
        desc.add( 4, 0, RABBIT_LOWERING_AND_RISING_LEFT );

        assertThat(
            renderChangeDescription( world, desc ),
            equalTo(
                " , . "
            )
        );
    }

    @Test
    public void Rabbits_walking_up_and_immediately_down()
    {
        World world = createEmptyWorld( 5, 1 );

        ChangeDescription desc = new ChangeDescription();
        desc.add( 0, 0, RABBIT_RISING_AND_LOWERING_RIGHT );
        desc.add( 4, 0, RABBIT_RISING_AND_LOWERING_LEFT );

        assertThat(
            renderChangeDescription( world, desc ),
            equalTo(
                " & * "
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
            renderChangeDescription( world, desc ),
            equalTo(
                "        ",
                "ffffedsa",
                "edsa    "
            )
        );
    }
}

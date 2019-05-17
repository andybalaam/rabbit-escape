package rabbitescape.engine.logic;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static rabbitescape.engine.Tools.*;
import static rabbitescape.engine.textworld.TextWorldManip.*;
import static rabbitescape.engine.util.WorldAssertions.*;

import org.junit.Test;

import rabbitescape.engine.World;

public class TestFalling
{
    @Test
    public void Fall_when_no_floor()
    {
        World world = createWorld(
            "   r ",
            "     ",
            "     ",
            "     ",
            "#####"
        );

        world.step();

        assertThat(
            renderWorld( world, false, false ),
            equalTo(
                "     ",
                "     ",
                "   r ",
                "     ",
                "#####"
            )
        );
    }

    @Test
    public void Fall_only_1_when_floor_is_1_below()
    {
        World world = createWorld(
            "   r ",
            "     ",
            "   # ",
            "     ",
            "#####"
        );

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   r ",
                "   f ",
                "   # ",
                "     ",
                "#####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, false, false ),
            equalTo(
                "     ",
                "   r ",
                "   # ",
                "     ",
                "#####"
            )
        );
    }

    @Test
    public void Fall_4_squares_without_dieing()
    {
        World world = createWorld(
            "   r ",
            "     ",
            "     ",
            "     ",
            "     ",
            "#####"
        );

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   r ",
                "   f ",
                "   f ",
                "     ",
                "     ",  // Falling
                "#####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "     ",
                "     ",
                "   r ",
                "   f ",
                "   f ",  // Falling
                "#####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "     ",
                "     ",
                "     ",
                "     ",
                "   r>",  // Landed and going to walk off
                "#####"
            )
        );
    }

    @Test
    public void Fall_6_squares_and_you_die()
    {
        World world = createWorld(
            "   r ",
            "     ",
            "     ",
            "     ",
            "     ",
            "     ",
            "     ",
            "#####"
        );

        world.step();     // Falling
        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "     ",
                "     ",
                "     ",
                "     ",
                "   r ",
                "   f ",
                "   f ",  // Still falling
                "#####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, false, false ),
            equalTo(
                "     ",
                "     ",
                "     ",
                "     ",
                "     ",
                "     ",
                "   r ",  // Landed ...
                "#####"
            )
        );

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "     ",
                "     ",
                "     ",
                "     ",
                "     ",
                "     ",
                "   X ",  // ... and going to die
                "#####"
            )
        );

        world.step();


        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "     ",
                "     ",
                "     ",
                "     ",
                "     ",
                "     ",
                "     ",  // and now dead
                "#####"
            )
        );
    }

    @Test
    public void Fall_5_squares_and_you_die_earlier()
    {
        World world = createWorld(
            "   r ",
            "     ",
            "     ",
            "     ",
            "     ",
            "     ",
            "#####"
        );

        world.step();
        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "     ",
                "     ",
                "     ",
                "     ",
                "   r ",  // Not quite landed ...
                "   x ",  // ... but about to die ...
                "#####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "     ",
                "     ",
                "     ",
                "     ",
                "     ",
                "   y ",  // ... and will continue dying next step
                "#####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "     ",
                "     ",
                "     ",
                "     ",
                "     ",
                "     ",  // and now dead
                "#####"
            )
        );
    }

    @Test
    public void Rabbot_fall_5_squares_and_survive()
    {
        World world = createWorld(
            "   t ",
            "     ",
            "     ",
            "     ",
            "     ",
            "     ",
            "#####"
        );

        world.step();
        world.step();
        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "     ",
                "     ",
                "     ",
                "     ",
                "     ",
                "   t>",  // Lands and starts walking.
                "#####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "     ",
                "     ",
                "     ",
                "     ",
                "     ",
                "    t",  // Walks off.
                "#####"
            )
        );
    }

    @Test
    public void Rabbot_falls_6_squares_and_dies()
    {
        World world = createWorld(
            "   t ",
            "     ",
            "     ",
            "     ",
            "     ",
            "     ",
            "     ",
            "#####"
        );

        world.step();     // Falling
        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "     ",
                "     ",
                "     ",
                "     ",
                "   t ",
                "   f ",
                "   f ",  // Still falling
                "#####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, false, false ),
            equalTo(
                "     ",
                "     ",
                "     ",
                "     ",
                "     ",
                "     ",
                "   t ",  // Landed ...
                "#####"
            )
        );

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "     ",
                "     ",
                "     ",
                "     ",
                "     ",
                "     ",
                "   X ",  // ... and going to die
                "#####"
            )
        );

        world.step();


        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "     ",
                "     ",
                "     ",
                "     ",
                "     ",
                "     ",
                "     ",  // and now dead
                "#####"
            )
        );
    }

    @Test
    public void Fall_onto_slope_down_right()
    {
        World world = createWorld(
            "   r ",
            "     ",
            "  #\\ ",
            "#####"
        );

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   r ",
                "   f ",
                "  #e ",
                "#####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "     ",
                "     ",
                "  #r_",
                "#####"
            )
        );
    }

    @Test
    public void Fall_onto_brige_down_right()
    {
        World world = createWorld(
            "   r ",
            "  )  ",
            "   ) ",
            "    )"
        );

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   r ",
                "  )f ",
                "   e ",
                "    )"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "     ",
                "  )  ",
                "   r ",
                "    @"
            )
        );
    }

    @Test
    public void Fall_odd_to_death_on_bridge()
    {
        assertWorldEvolvesLike(
            "r r" + "\n" +
            "   " + "\n" +
            "   " + "\n" +
            "   " + "\n" +
            "   " + "\n" +
            ") (",

            "   " + "\n" +
            "   " + "\n" +
            "r r" + "\n" +
            "f f" + "\n" +
            "f f" + "\n" +
            ") (",

            "   " + "\n" +
            "   " + "\n" +
            "   " + "\n" +
            "   " + "\n" +
            "r r" + "\n" +
            "x x"
        );
    }

    @Test
    public void Fall_even_to_death_on_bridge()
    {
        assertWorldEvolvesLike(
            "r r" + "\n" +
            "   " + "\n" +
            "   " + "\n" +
            "   " + "\n" +
            "   " + "\n" +
            "   " + "\n" +
            ") (",

            "   " + "\n" +
            "   " + "\n" +
            "r r" + "\n" +
            "f f" + "\n" +
            "f f" + "\n" +
            "   " + "\n" +
            ") (",

            "   " + "\n" +
            "   " + "\n" +
            "   " + "\n" +
            "   " + "\n" +
            "r r" + "\n" +
            "f f" + "\n" +
            "x x",

            "   " + "\n" +
            "   " + "\n" +
            "   " + "\n" +
            "   " + "\n" +
            "   " + "\n" +
            "   " + "\n" +
            "X X"
        );
    }

    @Test
    public void Fall_onto_slope_down_left()
    {
        World world = createWorld(
            "  j  ",
            "     ",
            "  /# ",
            "#####"
        );

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "  j  ",
                "  f  ",
                "  s# ",
                "#####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "     ",
                "     ",
                " +j# ",
                "#####"
            )
        );
    }

    @Test
    public void Fall_onto_bridge_down_left()
    {
        World world = createWorld(
            "  j  ",
            "     ",
            "  (# ",
            "#####"
        );

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "  j  ",
                "  f  ",
                "  s# ",
                "#####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "     ",
                "     ",
                " +j# ",
                "#####"
            )
        );
    }

    @Test
    public void Fall_onto_slope_up_right()
    {
        World world = createWorld(
            "  r  ",
            "     ",
            "  /# ",
            "#####"
        );

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "  r  ",
                "  f  ",
                "  h# ",
                "#####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "     ",
                "   ' ",
                "  r# ",
                "#####"
            )
        );
    }

    @Test
    public void Fall_onto_bridge_up_right()
    {
        World world = createWorld(
            "  r  ",
            "     ",
            "  (# ",
            "#####"
        );

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "  r  ",
                "  f  ",
                "  h# ",
                "#####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "     ",
                "   ' ",
                "  r# ",
                "#####"
            )
        );
    }

    @Test
    public void Fall_onto_slope_up_left()
    {
        World world = createWorld(
            "   j ",
            "     ",
            "  #\\ ",
            "#####"
        );

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   j ",
                "   f ",
                "  #a ",
                "#####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "     ",
                "  !  ",
                "  #j ",
                "#####"
            )
        );
    }

    @Test
    public void Fall_onto_bridge_up_left()
    {
        World world = createWorld(
            "   j ",
            "     ",
            "  #) ",
            "#####"
        );

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   j ",
                "   f ",
                "  #a ",
                "#####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "     ",
                "  !  ",
                "  #j ",
                "#####"
            )
        );
    }

    @Test
    public void Fall_1_onto_slope_down_right()
    {
        World world = createWorld(
            "   r ",
            "  #\\ ",
            "#####"
        );

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   r ",
                "  #e ",
                "#####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "     ",
                "  #r_",
                "#####"
            )
        );
    }

    @Test
    public void Fall_1_onto_bridge_down()
    {
        assertWorldEvolvesLike(
            "   r " + "\n" +
            "   ) " + "\n" +
            "     ",

            "     " + "\n" +
            "   r_" + "\n" +
            "     "
        );
    }

    @Test
    public void Fall_1_onto_slope_down()
    {
        assertWorldEvolvesLike(
            "   j " + "\n" +
            "   / " + "\n" +
            "     ",

            "     " + "\n" +
            "  +j " + "\n" +
            "     "
        );
    }

    @Test
    public void Fall_1_onto_slope_up_right()
    {
        World world = createWorld(
            "  r  ",
            "  /# ",
            "#####"
        );

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "  r  ",
                "  h# ",
                "#####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   ' ",
                "  r# ",
                "#####"
            )
        );
    }

    @Test
    public void Fall_1_onto_bridge_up_right()
    {
        World world = createWorld(
            "  r  ",
            "  (# ",
            "#####"
        );

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "  r  ",
                "  h# ",
                "#####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   ' ",
                "  r# ",
                "#####"
            )
        );
    }

    @Test
    public void Fall_1_onto_slope_up_left()
    {
        World world = createWorld(
            "   j ",
            "  #\\ ",
            "#####"
        );

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   j ",
                "  #a ",
                "#####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "  !  ",
                "  #j ",
                "#####"
            )
        );
    }

    @Test
    public void Fall_1_onto_bridge_up_left()
    {
        World world = createWorld(
            "   j ",
            "  #) ",
            "#####"
        );

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   j ",
                "  #a ",
                "#####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "  !  ",
                "  #j ",
                "#####"
            )
        );
    }

    @Test
    public void Fall_to_death_onto_slopes()
    {
        // The animation for this is wrong so this test will need updating,
        // but the behaviour must show the rabbits dying.
        // done for right slope. left rise outstanding.

        World world = createWorld(
            "rrrr",
            "    ",
            "    ",
            "    ",
            "    ",
            " /\\ ",
            "/##\\",
            "####"
        );

        world.step();
        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    ",
                "    ",
                "    ",
                "    ",
                "rrrr",
                "fxxf",
                "x##x",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    ",
                "    ",
                "    ",
                "    ",
                "    ",
                " yy ",
                "X##X",
                "####"
            )
        );


        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    ",
                "    ",
                "    ",
                "    ",
                "    ",
                " /\\ ",
                "/##\\",
                "####"
            )
        );
    }


    @Test
    public void Fall_to_death_onto_bridges()
    {
        // The animation for this is wrong so this test will need updating,
        // but the behaviour must show the rabbits dying.

        World world = createWorld(
            "rrrr",
            "    ",
            "    ",
            "    ",
            "    ",
            " () ",
            "(##)",
            "####"
        );

        world.step();
        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    ",
                "    ",
                "    ",
                "    ",
                "rrrr",
                "fxxf",
                "x##x",
                "####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    ",
                "    ",
                "    ",
                "    ",
                "    ",
                " yy ",
                "X##X",
                "####"
            )
        );


        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    ",
                "    ",
                "    ",
                "    ",
                "    ",
                " () ",
                "(##)",
                "####"
            )
        );
    }

    @Test
    public void Multiple_falls_are_not_additive()
    {
        World world = createWorld(
            "r    ",
            "     ",
            "     ",
            "     ",
            "     ",
            "#    ",
            "     ",
            "#####"
        );

        world.step();     // Falling
        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "     ",
                "     ",
                "     ",
                "     ",
                "r>   ",
                "#    ",
                "     ",  // Stopped
                "#####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "     ",
                "     ",
                "     ",
                "     ",
                " r   ",
                "#f   ",
                " f   ",  // Fall the last bit
                "#####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "     ",
                "     ",
                "     ",
                "     ",
                "     ",
                "#    ",
                " r>  ",  // Not dead
                "#####"
            )
        );
    }

    @Test
    public void Fall_off_the_end_of_a_rise()
    {
        assertWorldEvolvesLike(
            "      " + "\n" +
            " r /  " + "\n" +
            "######",

            "      " + "\n" +
            "  r~  " + "\n" +
            "######",

            "    ' " + "\n" +
            "   r  " + "\n" +
            "######",

            "    r " + "\n" +
            "   /f " + "\n" +
            "######"
        );
    }

    @Test
    public void Fall_off_the_end_of_a_slide()
    {
        assertWorldEvolvesLike(
            " r    " + "\n" +
            " #)   " + "\n" +
            "      " + "\n" +
            "      " + "\n" +
            "      ",

            "      " + "\n" +
            " #r_  " + "\n" +
            "      " + "\n" +
            "      " + "\n" +
            "      ",

            "      " + "\n" +
            " #)r  " + "\n" +
            "   f  " + "\n" +
            "   f  " + "\n" +
            "      "
        );
    }

    @Test
    public void Dont_pick_up_tokens_when_falling_except_explode()
    {
        assertWorldEvolvesLike(
            "rrrrrr" + "\n" +
            "bdikcp" + "\n" +
            "      " + "\n" +
            "      " + "\n" +
            "      " + "\n" +
            "      " + "\n" +
            "      " + "\n" +
            "      " + "\n" +
            "      ",

            "      " + "\n" +
            "      " + "\n" +
            "bdikrP" + "\n" +
            "fffff " + "\n" +
            "fffff " + "\n" +
            "      " + "\n" +
            "      " + "\n" +
            "      " + "\n" +
            "      ",

            "      " + "\n" +
            "      " + "\n" +
            "      " + "\n" +
            "bdik  " + "\n" +
            "ffffr " + "\n" +
            "fffff " + "\n" +
            "fffff " + "\n" +
            "      " + "\n" +
            "      ",

            "      " + "\n" +
            "      " + "\n" +
            "      " + "\n" +
            "      " + "\n" +
            "bdik  " + "\n" +
            "ffff  " + "\n" +
            "rrrrr " + "\n" +
            "fffff " + "\n" +
            "fffff "
        );
    }

    @Test
    public void Fall_through_fresh_dig()
    {
        // All must die
        World world = createWorld(
            "    d(      d/  rrr d  ",
            "    (       /   #####  ",
            "rrr(  O rrr/  O       O",
            "#### ## #### ## #### ##"
        );

        assertWorldEvolvesLike(
            world,
            10,
            new String[] {
                "     (       /         ",
                "                ####   ",
                "   (  O    /  O       O",
                "#### ## #### ## #### ##"
            });

        // None lived
        assertThat( world.num_saved, equalTo ( 0 ) );
    }

    @Test
    public void Fall_through_fresh_dig_again()
    {
        // All must exit
        World world = createWorld(
            "  r ",
            " rd(",
            " )( ",
            " (O ",
            " ###",
            " d  ",
            " )jj",
            " O) ",
            "####"
        );

        assertWorldEvolvesLike(
            world,
            6,
            new String[] {
                "    ",
                "   (",
                " )  ",
                " (O ",
                " ###",
                "    ",
                "    ",
                " O) ",
                "####"
            });

        assertThat( world.num_saved, equalTo ( 4 ) );
    }

    @Test
    public void Fall_through_fresh_dig_just_removed_block_cache_clear()
    {
        // First digs to exit. Next bridges over exit.
        World world = createWorld(
             "       ",
             "   id( ",
             "    (  ",
             "r r(O  ",
             "#######"
        );

        // Don't really need to insist on the double bridge
        // block, but it seems harmless.
        assertWorldEvolvesLike(
            world,
            14,
            new String[] {
                "      (",
                "     * ",
                "    (  ",
                "   (O  ",
                "#######",
                ":*=(("
            });

        assertThat( world.num_saved, equalTo ( 1 ) );
    }

    @Test
    public void Fall_through_fresh_bash()
    {
        World world = createWorld(
              "      ",
              "  rb( ",
              " r (O ",
              "######"
        );

        assertWorldEvolvesLike(
            world,
            14,
            new String[] {
                "      ",
                "      ",
                "   (O ",
                "######"
            });

        assertThat( world.num_saved, equalTo ( 2 ) );
    }
}

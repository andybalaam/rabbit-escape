package rabbitescape.engine.logic;

import static org.hamcrest.MatcherAssert.*;
import static rabbitescape.engine.Tools.*;
import static rabbitescape.engine.textworld.TextWorldManip.*;

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
            "     ",
            "  #) ",
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
                "  d# ",
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
                "  d# ",
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
    public void Fall_1_onto_bridge_down_right()
    {
        World world = createWorld(
            "   r ",
            "  #) ",
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
    public void Fall_1_onto_slope_down_left()
    {
        World world = createWorld(
            "  j  ",
            "  /# ",
            "#####"
        );

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "  j  ",
                "  s# ",
                "#####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "     ",
                " +j# ",
                "#####"
            )
        );
    }

    @Test
    public void Fall_1_onto_bridge_down_left()
    {
        World world = createWorld(
            "  j  ",
            "  (# ",
            "#####"
        );

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "  j  ",
                "  s# ",
                "#####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "     ",
                " +j# ",
                "#####"
            )
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
                "  d# ",
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
                "  d# ",
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
                "d##e",
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
                "d##e",
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
}

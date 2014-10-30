package rabbitescape.engine.logic;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import static rabbitescape.engine.textworld.TextWorldManip.*;

import org.junit.Test;

import rabbitescape.engine.World;

public class TestOutOfBounds
{
    @Test
    public void Die_if_walk_out_of_bounds()
    {
        assertRabbitDeadAfter(
            5,
            "     ",
            " r   ",
            "#####"
        );

        assertRabbitDeadAfter(
            3,
            "     ",
            " j   ",
            "#####"
        );
    }

    @Test
    public void Die_if_fall_out_of_bounds()
    {
        assertRabbitDeadAfter(
            2,
            "     ",
            " r   ",
            "     "
        );
    }

    @Test
    public void Die_if_walk_up_out_of_bounds()
    {
        assertRabbitDeadAfter(
            5,
            "   /#",
            "  /##",
            "r/###",
            "#####"
        );
    }

    private void assertRabbitDeadAfter( int stepsToDeath, String... worldLines )
    {
        World world = createWorld( worldLines );

        for ( int i = 1; i < stepsToDeath; ++i )
        {
            world.step();
            assertThat( world.num_killed, equalTo( 0 ) );
        }

        world.step();
        assertThat( world.num_killed, equalTo( 1 ) );
    }
}

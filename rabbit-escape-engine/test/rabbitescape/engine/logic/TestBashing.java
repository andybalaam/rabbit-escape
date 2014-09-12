package rabbitescape.engine.logic;

import static org.hamcrest.MatcherAssert.*;
import static rabbitescape.engine.Tools.*;
import static rabbitescape.engine.textworld.TextWorldManip.*;

import org.junit.Test;

import rabbitescape.engine.World;

public class TestBashing
{
    // TODO: slopes and bridges

    @Test
    public void Token_not_next_to_wall_makes_useless_bash()
    {
        World world = createWorld(
            " rb   bj ",
            "#########"
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "  rI Jj  ",
                "#########"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "  r> <j  ",
                "#########"
            )
        );
    }

    @Test
    public void Bash_through_single_wall()
    {
        World world = createWorld(
            " rb#     #bj ",
            "#############"
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "  rK     Wj  ",
                "#############"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "  r>     <j  ",
                "#############"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   r>   <j   ",
                "#############"
            )
        );
    }

    @Test
    public void Bash_through_longer_wall()
    {
        World world = createWorld(
            " rb###     ###bj ",
            "#################"
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "  rK##     ##Wj  ",
                "#################"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "  r>##     ##<j  ",
                "#################"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   rK#     #Wj   ",
                "#################"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   r>#     #<j   ",
                "#################"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    rK     Wj    ",
                "#################"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "    r>     <j    ",
                "#################"
            )
        );


        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "     r>   <j     ",
                "#################"
            )
        );
    }

    @Test
    public void Bashing_doesnt_last()
    {
        World world = createWorld(
                " rb# # # #bj ",
                "#############"
        );

        world.step();

        assertThat(
                renderWorld( world, true, false ),
                equalTo(
                        "  rK # # Wj  ",
                        "#############"
                )
        );

        world.step();

        assertThat(
                renderWorld( world, true, false ),
                equalTo(
                        "  r> # # <j  ",
                        "#############"
                )
        );

        world.step();

        assertThat(
                renderWorld( world, true, false ),
                equalTo(
                        "   r># #<j   ",
                        "#############"
                )
        );

        world.step();

        assertThat(
                renderWorld( world, true, false ),
                equalTo(
                        "    ?# #|    ",
                        "#############"
                )
        );

        world.step();

        assertThat(
                renderWorld( world, true, false ),
                equalTo(
                        "   <j# #r>   ",
                        "#############"
                )
        );
    }
}

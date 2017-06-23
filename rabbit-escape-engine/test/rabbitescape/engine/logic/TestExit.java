package rabbitescape.engine.logic;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static rabbitescape.engine.Tools.*;
import static rabbitescape.engine.textworld.TextWorldManip.*;
import static rabbitescape.engine.util.WorldAssertions.*;

import org.junit.Test;

import rabbitescape.engine.World;

public class TestExit
{
    @Test
    public void Rabbit_disappears_into_exit()
    {
        World world = createWorld(
            "r  O ",
            "#####"
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                " r>O ",
                "#####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "  r> ",
                "#####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   R ",  // Entering
                "#####"
            )
        );

        world.step();

        assertThat(
            renderWorld( world, true, false ),
            equalTo(
                "   O ",  // Gone
                "#####"
            )
        );
    }

    @Test
    public void World_keeps_score()
    {
        World world = createWorld(
            "Ojjjj   ",
            "########"
        );

        world.step();
        assertThat( world.num_saved, equalTo( 0 ) );
        world.step();
        assertThat( world.num_saved, equalTo( 1 ) );
        world.step();
        assertThat( world.num_saved, equalTo( 2 ) );
        world.step();
        assertThat( world.num_saved, equalTo( 3 ) );
        world.step();
        assertThat( world.num_saved, equalTo( 4 ) );
    }

    @Test
    public void Splatting_prevents_exit()
    {
        World world = createWorld(
            "r         #       ",
            "# r       #       ",
            "  # r     #       ",
            "    # r   #       ",
            "      # r #       ",
            "        # r       ",
            "          # r     ",
            "            # r   ",
            "              #   ",
            " O O O O O O O O  ",
            " # # # # # # # #  ",
            "#                #",
            "##################"
        );

        assertWorldEvolvesLike(
            world,
            10,
            new String[] {
                "          #       ",
                "#         #       ",
                "  #       #       ",
                "    #     #       ",
                "      #   #       ",
                "        #         ",
                "          #       ",
                "            #     ",
                "              #   ",
                " O O O O O O O O  ",
                " # # # # # # # #  ",
                "#                #",
                "##################"
            });

        assertThat( world.num_saved, equalTo ( 3 ) );
    }

    @Test
    public void Climb_into_exit()
    {
        // Has a trap to see if the rabbit climbed past
        World world = createWorld(
            "     ",
            " O# #",
            "  ###",
            "rc#  ",
            "###  "
        );

        assertWorldEvolvesLike(
            world,
            6,
            new String[] {
                "     ",
                " O# #",
                "  ###",
                "  #  ",
                "###  "
            });

        // The rabbit escaped
        assertThat( world.num_saved, equalTo ( 1 ) );
    }

    @Test
    public void Fall_past_exit()
    {
        // All must die
        World world = createWorld(
            "rrrrrrr",
            "O      ",
            " O     ",
            "  O    ",
            "   O   ",
            "    O  ",
            "     O ",
            "      O"
        );

        assertWorldEvolvesLike(
            world,
            5,
            new String[] {
                "       ",
                "O      ",
                " O     ",
                "  O    ",
                "   O   ",
                "    O  ",
                "     O ",
                "      O"
            });

        // None lived
        assertThat( world.num_saved, equalTo ( 0 ) );
    }

    @Test
    public void Rabbots_ignore_the_exit()
    {
        assertWorldEvolvesLike(
            "t  O y" + "\n" +
            "######",

            " t><y " + "\n" +
            "######",

            "  <>  " + "\n" +
            "######",

            " <yO> " + "\n" + // They just walked straight past!
            "######",

            "<y Ot>" + "\n" +
            "######"
        );
    }
}

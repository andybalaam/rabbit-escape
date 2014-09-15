package rabbitescape.engine.logic;

import static org.hamcrest.MatcherAssert.*;
import static rabbitescape.engine.util.Util.*;
import static rabbitescape.engine.Tools.*;
import static rabbitescape.engine.textworld.TextWorldManip.*;

import org.junit.Test;

import rabbitescape.engine.World;

public class TestBridging
{
    @Test
    public void Bridge_on_the_flat()
    {
        assertWorldEvolvesLike(
            "               " + "\n" +
            "               " + "\n" +
            "               " + "\n" +
            "               " + "\n" +
            " ri         ij " + "\n" +
            "###############",

            "               " + "\n" +
            "               " + "\n" +
            "               " + "\n" +
            "               " + "\n" +
            "  rB       Ej  " + "\n" +
            "###############",

            "               " + "\n" +
            "               " + "\n" +
            "               " + "\n" +
            "               " + "\n" +
            "  r[       ]j  " + "\n" +
            "###############",

            "               " + "\n" +
            "               " + "\n" +
            "               " + "\n" +
            "               " + "\n" +
            "  r{       }j  " + "\n" +
            "###############",

            "               " + "\n" +
            "               " + "\n" +
            "               " + "\n" +
            "               " + "\n" +
            "  r~       `j  " + "\n" +
            "###############",

            "               " + "\n" +
            "               " + "\n" +
            "               " + "\n" +
            "    B     E    " + "\n" +
            "   r       j   " + "\n" +
            "###############",

            "               " + "\n" +
            "               " + "\n" +
            "               " + "\n" +
            "    [     ]    " + "\n" +
            "   r       j   " + "\n" +
            "###############",

            "               " + "\n" +
            "               " + "\n" +
            "               " + "\n" +
            "    {     }    " + "\n" +
            "   r       j   " + "\n" +
            "###############",

            "               " + "\n" +
            "               " + "\n" +
            "               " + "\n" +
            "    $     ^    " + "\n" +
            "   r       j   " + "\n" +
            "###############",

            "               " + "\n" +
            "               " + "\n" +
            "     B   E     " + "\n" +
            "    r     j    " + "\n" +
            "   (       )   " + "\n" +
            "###############",

            "               " + "\n" +
            "               " + "\n" +
            "     [   ]     " + "\n" +
            "    r     j    " + "\n" +
            "   (       )   " + "\n" +
            "###############",

            "               " + "\n" +
            "               " + "\n" +
            "     {   }     " + "\n" +
            "    r     j    " + "\n" +
            "   (       )   " + "\n" +
            "###############",

            "               " + "\n" +
            "               " + "\n" +
            "     $   ^     " + "\n" +
            "    r     j    " + "\n" +
            "   (       )   " + "\n" +
            "###############",

            "               " + "\n" +
            "      ' !      " + "\n" +
            "     r   j     " + "\n" +
            "    (     )    " + "\n" +
            "   (       )   " + "\n" +
            "###############",

            "               " + "\n" +
            "      r j      " + "\n" +
            "     (f f)     " + "\n" +
            "    ( f f )    " + "\n" +
            "   (       )   " + "\n" +
            "###############"
        );
    }

    private void assertWorldEvolvesLike(
        String initialState, String... laterStates )
    {
        World world = createWorld( split( initialState, "\n" ) );

        for ( String state : laterStates )
        {
            world.step();

            assertThat(
                renderWorld( world, true, false ),
                equalTo( split( state, "\n" ) )
            );
        }
    }
}

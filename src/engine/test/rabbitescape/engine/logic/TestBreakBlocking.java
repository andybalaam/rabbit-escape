package rabbitescape.engine.logic;

import org.junit.Test;
import rabbitescape.engine.World;

import static org.hamcrest.MatcherAssert.assertThat;
import static rabbitescape.engine.Tools.equalTo;
import static rabbitescape.engine.textworld.TextWorldManip.createWorld;
import static rabbitescape.engine.textworld.TextWorldManip.renderWorld;

public class TestBreakBlocking {


    @Test
    public void Break_blocking_above_flat()
    {


        String[] world = {
                "############",
                "#          #",
                "#5         #",
                "##         #",
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

        World w = createWorld( world );
        w.step();

        assertThat(

                renderWorld( w, false, true ),
                equalTo( expected )
        );
    }

    @Test
    public void Break_blocking_above_diag()
    {


        String[] world = {
                "############",
                "#          #",
                "#5         #",
                "#/         #",
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

        World w = createWorld( world );
        w.step();

        w.step();
        assertThat(

                renderWorld( w, false, true ),
                equalTo( expected )
        );
    }



}

package rabbitescape.engine.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static rabbitescape.engine.Tools.equalTo;
import static rabbitescape.engine.textworld.TextWorldManip.createWorld;
import static rabbitescape.engine.textworld.TextWorldManip.renderCompleteWorld;
import static rabbitescape.engine.util.WorldAssertions.assertWorldEvolvesLike;

import org.junit.Test;

import rabbitescape.engine.World;
import rabbitescape.engine.util.WaterUtil;

public class TestWater
{
    @Test
    public void Water_goes_round_ubend()
    {
        World world = createWorld(
            " P #  ",
            "#  # #",
            "#  # #",
            "#  # #",
            "#    #",
            "######"
        );

        assertWorldEvolvesLike(
            world,
            10,
            new String[] {
                "n*n#  ",
                "#nn# #",
                "#nn# #",
                "#nn# #",
                "#nnnn#",
                "######",
                ":*=nP",
                ":n=0,0,7",
                ":n=1,0,521",
                ":n=2,0,7",
                ":n=1,1,536",
                ":n=2,1,22",
                ":n=1,2,577",
                ":n=2,2,81",
                ":n=1,3,716",
                ":n=2,3,190",
                ":n=1,4,901",
                ":n=2,4,666",
                ":n=3,4,502",
                ":n=4,4,394"
            });

        assertWorldEvolvesLike(
            world,
            20,
            new String[] {
                "n*n#  ",
                "#nn# #",
                "#nN# #",
                "#NN#n#",
                "#NNNN#",
                "######",
                ":*=nP",
                ":n=0,0,163",
                ":n=1,0,793",
                ":n=2,0,163",
                ":n=1,1,981",
                ":n=2,1,759",
                ":n=1,2,1012",
                ":n=2,2,1230",
                ":n=1,3,1109",
                ":n=2,3,1649",
                ":n=4,3,697",
                ":n=1,4,1314",
                ":n=2,4,1817",
                ":n=3,4,1281",
                ":n=4,4,1298"
            });

        assertWorldEvolvesLike(
            world,
            30,
            new String[] {
                "n*n#  ",
                "#NN# #",
                "#NN#n#",
                "#NN#N#",
                "#NNNN#",
                "######",
                ":*=NP",
                ":n=0,0,364",
                ":n=1,0,1556",
                ":n=2,0,581",
                ":n=1,1,1203",
                ":n=2,1,1170",
                ":n=1,2,1523",
                ":n=2,2,1568",
                ":n=4,2,937",
                ":n=1,3,1690",
                ":n=2,3,1634",
                ":n=4,3,1524",
                ":n=1,4,2056",
                ":n=2,4,2023",
                ":n=3,4,1855",
                ":n=4,4,1740"
            });

        assertWorldEvolvesLike(
            world,
            40,
            new String[] {
                "n*n#nn",
                "#Nn#N#",
                "#NN#N#",
                "#NN#N#",
                "#NNNN#",
                "######",
                ":*=NP",
                ":n=0,0,341",
                ":n=1,0,1577",
                ":n=2,0,729",
                ":n=4,0,87",
                ":n=5,0,39",
                ":n=1,1,1851",
                ":n=2,1,914",
                ":n=4,1,1055",
                ":n=1,2,1344",
                ":n=2,2,2246",
                ":n=4,2,1564",
                ":n=1,3,1580",
                ":n=2,3,4014",
                ":n=4,3,1895",
                ":n=1,4,2281",
                ":n=2,4,1565",
                ":n=3,4,1873",
                ":n=4,4,2054"
            });
    }

    @Test
    public void Bulkhead_flow()
    {
        World world = createWorld(
            "#   P #",
            "#     #",
            "#   # #",
            "#   # #",
            "#   # #",
            "#######"
        );

        assertWorldEvolvesLike(
            world,
            10,
            new String[] {
                "#  n*n#",
                "#  nnn#",
                "#  n#n#",
                "#  n#n#",
                "#nnn#n#",
                "#######",
                ":*=nP",
                ":n=3,0,81",
                ":n=4,0,689",
                ":n=5,0,81",
                ":n=3,1,250",
                ":n=4,1,753",
                ":n=5,1,250",
                ":n=3,2,249",
                ":n=5,2,249",
                ":n=3,3,236",
                ":n=5,3,236",
                ":n=1,4,252",
                ":n=2,4,222",
                ":n=3,4,549",
                ":n=5,4,1023"
            });

        assertWorldEvolvesLike(
            world,
            15,
            new String[] {
                "#  n*n#",
                "#  nnn#",
                "# nn#N#",
                "#nnn#N#",
                "#NNN#N#",
                "#######",
                ":*=nP",
                ":n=3,0,123",
                ":n=4,0,756",
                ":n=5,0,123",
                ":n=3,1,353",
                ":n=4,1,851",
                ":n=5,1,667",
                ":n=2,2,40",
                ":n=3,2,406",
                ":n=5,2,1076",
                ":n=1,3,408",
                ":n=2,3,519",
                ":n=3,3,785",
                ":n=5,3,1466",
                ":n=1,4,1066",
                ":n=2,4,1408",
                ":n=5,4,1729"
            });
    }

    @Test
    public void Enclosed_regions_dont_fill()
    {
        World world = createWorld(
            "#  P  #",
            "#     #",
            "#     #",
            "## ///#",
            "# / / #",
            "#######"
        );

        assertWorldEvolvesLike(
            world,
            50,
            new String[] {
                "#nN*nn#",
                "#NNNNN#",
                "#NNNNN#",
                "##N***#",
                "#N* / #",
                "#######",
                ":*=nP",
                ":*=/n",
                ":*=/n",
                ":*=/n",
                ":*=/n",
                ":n=1,0,224",
                ":n=2,0,1083",
                ":n=3,0,771",
                ":n=4,0,872",
                ":n=5,0,568",
                ":n=1,1,1246",
                ":n=2,1,1749",
                ":n=3,1,1025",
                ":n=4,1,1870",
                ":n=1,2,1286",
                ":n=2,2,2103",
                ":n=3,2,1278",
                ":n=4,2,1903",
                ":n=5,2,1362",
                ":n=2,3,1852",
                ":n=3,3,872",
                ":n=4,3,834",
                ":n=5,3,933",
                ":n=1,4,1829",
                ":n=2,4,916"
            });
    }

    /**
     * Check that standing water can be loaded, stepped and output without any
     * need for water amount description lines (lines starting ":n=").
     */
    @Test
    public void Unqualified_standing_water_steps_normally()
    {
        World world = createWorld(
            "#N#n# #",
            "#######" );

        assertWorldEvolvesLike(
            world,
            1,
            new String[] {
                "#N#n# #",
                "#######"
            } );
    }

    /**
     * Check that standing water can be loaded, stepped and output without any
     * need for water amount description lines (lines starting ":n=").
     */
    @Test
    public void Water_description_lines_are_only_output_when_needed()
    {
        World world = createWorld(
            "#N N#",
            "#####");

        // Check that after one step we happen to get an exactly full cell in
        // the middle, and it doesn't require a water description line.
        assertWorldEvolvesLike(
            world,
            1,
            new String[] {
                "#nNn#",
                "#####"
            } );

        // Check that after one more step, all cells have water description
        // lines.
        assertWorldEvolvesLike(
            world,
            1,
            new String[] {
                "#nnn#",
                "#####",
                ":n=1,0,682",
                ":n=2,0,684",
                ":n=3,0,682"
            } );
    }

    /**
     * Create some regions between ramps at the bottom and blocks at the top,
     * and fill them with various amounts of water. Check that the water
     * description isn't needed when the amount of water is exactly
     * {@link WaterUtil#MAX_CAPACITY} (as created by a cell with an N) or
     * {@link WaterUtil#HALF_CAPACITY} (as created by a cell with an n).
     */
    @Test
    public void Standard_amounts_of_water_on_ramps_shouldnt_have_descriptions()
    {
        World world = createWorld(
            "######",
            "******",
            ":*=\\N",
            ":*=/N",
            ":*=\\n",
            ":*=/n",
            ":*=\\n",
            ":*=/n",
            ":n=4,1,10",
            ":n=5,1,20" );

        // Check that this 'round trips' successfully.
        String[] lines = renderCompleteWorld( world, true );
        assertThat(
            renderCompleteWorld( createWorld( lines ), true ),
            equalTo( lines ) );

        // Check that even after a step the left regions don't need
        // descriptions.
        assertWorldEvolvesLike(
            world,
            1,
            new String[] {
                "######",
                "******",
                ":*=\\N",
                ":*=/N",
                ":*=\\n",
                ":*=/n",
                ":*=\\n",
                ":*=/n",
                ":n=4,1,15",
                ":n=5,1,15"
            } );
    }
}

package rabbitescape.engine.logic;

import static rabbitescape.engine.textworld.TextWorldManip.createWorld;
import static rabbitescape.engine.util.WorldAssertions.assertWorldEvolvesLike;

import org.junit.Test;

import rabbitescape.engine.World;

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
                ":*=nP"
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
                ":*=nP"
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
                ":*=NP"
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
                ":*=NP"
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
                ":*=nP"
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
                ":*=nP"
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
                ":*=/N",
                ":*=/N",
                ":*=/N",
                ":*=/N"
            });
    }
}

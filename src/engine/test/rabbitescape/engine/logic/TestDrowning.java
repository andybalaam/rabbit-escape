package rabbitescape.engine.logic;

import static rabbitescape.engine.util.WorldAssertions.assertWorldEvolvesLike;

import org.junit.Test;

public class TestDrowning
{
    @Test
    public void rabbits_drown_when_pipes_pump_water_on_them()
    {
        assertWorldEvolvesLike(
            "#P#" + "\n" +
            "# #" + "\n" +
            "#r#" + "\n" +
            "###",

            "#P#" + "\n" +
            "# #" + "\n" +
            "#|#" + "\n" +
            "###",

            "#P#" + "\n" +
            "#n#" + "\n" +
            "#?#" + "\n" +
            "###",

            "#P#" + "\n" +
            "#n#" + "\n" +
            "#|#" + "\n" + // Rabbits characters are displayed in preference to water.
            "###",

            "#P#" + "\n" +
            "#n#" + "\n" +
            "#R#" + "\n" + // Rabbit is drowning
            "###"
        );
    }

    @Test
    public void rabbits_drown_but_rabbots_dont()
    {
        assertWorldEvolvesLike(
            "#P#P#" + "\n" +
            "# # #" + "\n" +
            "#t#r#" + "\n" +
            "#####",

            "#P#P#" + "\n" +
            "# # #" + "\n" +
            "#|#|#" + "\n" +
            "#####",

            "#P#P#" + "\n" +
            "#n#n#" + "\n" +
            "#?#?#" + "\n" +
            "#####",

            "#P#P#" + "\n" +
            "#n#n#" + "\n" +
            "#|#|#" + "\n" + // Rabbits and Rabbots characters are displayed in preference to water.
            "#####",

            "#P#P#" + "\n" +
            "#n#n#" + "\n" +
            "#?#R#" + "\n" + // Rabbit is drowning but the Rabbot isn't
            "#####"
        );
    }
    
    @Test
    public void rabbits_drown_on_slopes()
    {
        assertWorldEvolvesLike(
            "#  P#" + "\n" +
            "# r/ " + "\n" +
            "# /  " + "\n" +
            "#/   ",

            "#  P#" + "\n" +
            "#  $ " + "\n" +
            "# r  " + "\n" +
            "#/   ",

            "#  P#" + "\n" +
            "#  ? " + "\n" +
            "# /  " + "\n" +
            "#/   ",

            "# nP#" + "\n" +
            "# nn " + "\n" +
            "# %  " + "\n" +
            "#/   ",

            "# nP#" + "\n" +
            "# nn " + "\n" +
            "# n  " + "\n" +
            "#%   ",

            "# nP#" + "\n" +
            "#nnn " + "\n" +
            "#nn  " + "\n" +
            "#[   ",

            "# nP#" + "\n" +
            "#nnn " + "\n" +
            "#n$  " + "\n" +
            "#n   ",

            "# nP#" + "\n" +
            "#nn$ " + "\n" +
            "#nn  " + "\n" +
            "#n   ",

            "# nP#" + "\n" +
            "#nnR " + "\n" +
            "#nn  " + "\n" +
            "#n   "
        );
    }

    @Test
    public void rabbits_can_wade_through_rivers()
    {
        assertWorldEvolvesLike(
            "    P#" + "\n" +
            "r    #" + "\n" +
            "######",

            "    P#" + "\n" +
            " r>  #" + "\n" +
            "######",

            "    P#" + "\n" +
            "  r>n#" + "\n" +
            "######",

            "    P#" + "\n" +
            "   n>#" + "\n" +
            "######",

            "   nP#" + "\n" +
            "  nn?#" + "\n" +
            "######",

            "   nP#" + "\n" +
            " nn<n#" + "\n" +
            "######",

            "   nP#" + "\n" +
            "nn<nn#" + "\n" +
            "######",

            "   nP#" + "\n" +
            "n<nnn#" + "\n" +
            "######",

            "   nP#" + "\n" +
            "<nnnn#" + "\n" +
            "######"
        );
    }

    @Test
    public void bashing_can_cause_drowning()
    {
        assertWorldEvolvesLike(
            "# r  #" + "\n" +
            "# ####" + "\n" +
            "# \\PP#" + "\n" +
            "# b\\ #" + "\n" +
            "######",

            "#  r>#" + "\n" +
            "# ####" + "\n" +
            "# \\PP#" + "\n" +
            "# b\\ #" + "\n" +
            "######",

            "#   ?#" + "\n" +
            "# ####" + "\n" +
            "# \\PP#" + "\n" +
            "# bnn#" + "\n" +
            "######",

            "#  <j#" + "\n" +
            "# ####" + "\n" +
            "# nPP#" + "\n" +
            "# bnN#" + "\n" +
            "######",

            "# <j #" + "\n" +
            "# ####" + "\n" +
            "# nPP#" + "\n" +
            "# bnN#" + "\n" +
            "######",

            "#<j  #" + "\n" +
            "# ####" + "\n" +
            "# nPP#" + "\n" +
            "# bnN#" + "\n" +
            "######",

            "#j   #" + "\n" +
            "#f####" + "\n" +
            "#fnPP#" + "\n" +
            "# bnN#" + "\n" +
            "######",

            "#    #" + "\n" +
            "# ####" + "\n" +
            "#jnPP#" + "\n" +
            "#fbnN#" + "\n" +
            "######",

            "#    #" + "\n" +
            "# ####" + "\n" +
            "# nPP#" + "\n" +
            "#|bNN#" + "\n" +
            "######",

            "#    #" + "\n" +
            "# ####" + "\n" +
            "# NPP#" + "\n" +
            "#r>NN#" + "\n" +
            "######",

            "#    #" + "\n" +
            "# ####" + "\n" +
            "# NPP#" + "\n" +
            "# rKN#" + "\n" +
            "######",

            "#    #" + "\n" +
            "# ####" + "\n" +
            "# NPP#" + "\n" +
            "# r>N#" + "\n" +
            "######",

            "#    #" + "\n" +
            "# ####" + "\n" +
            "# NPP#" + "\n" +
            "# nRN#" + "\n" + // The rabbit is drowning.
            "######",

            // If the world was to continue after the death of the rabbit then
            // the water would flow to fill the space.
            "#    #" + "\n" +
            "# ####" + "\n" +
            "# NPP#" + "\n" +
            "#nNNN#" + "\n" +
            "######"
        );
    }
}

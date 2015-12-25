package rabbitescape.engine.logic;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static rabbitescape.engine.Tools.*;
import static rabbitescape.engine.textworld.TextWorldManip.*;
import static rabbitescape.engine.util.WorldAssertions.*;

import org.junit.Test;

import rabbitescape.engine.World;

public class TestBrollychuting
{
    @Test
    public void Brollychute_on_flat()
    {
        assertWorldEvolvesLike(
            "r r  " + "\n" +
            "l    " + "\n" +
            "# l  " + "\n" +
            "  #  " + "\n" +
            "     " + "\n" +
            "     " + "\n" +
            "     " + "\n" +
            "    #" + "\n" +
            "#####",

            "     " + "\n" +
            "r>   " + "\n" +
            "# r> " + "\n" +
            "  #  " + "\n" +
            "     " + "\n" +
            "     " + "\n" +
            "     " + "\n" +
            "    #" + "\n" +
            "#####",
            
            "     " + "\n" +
            " r   " + "\n" +
            "#: r " + "\n" +
            "  #: " + "\n" +
            "     " + "\n" +
            "     " + "\n" +
            "     " + "\n" +
            "    #" + "\n" +
            "#####",
            
            "     " + "\n" +
            "     " + "\n" +
            "#r   " + "\n" +
            " :#r " + "\n" +
            "   : " + "\n" +
            "     " + "\n" +
            "     " + "\n" +
            "    #" + "\n" +
            "#####",
            
            "     " + "\n" +
            "     " + "\n" +
            "#    " + "\n" +
            " r#  " + "\n" +
            " : r " + "\n" +
            "   : " + "\n" +
            "     " + "\n" +
            "    #" + "\n" +
            "#####",
            
            "     " + "\n" +
            "     " + "\n" +
            "#    " + "\n" +
            "  #  " + "\n" +
            " r   " + "\n" +
            " : r " + "\n" +
            "   : " + "\n" +
            "    #" + "\n" +
            "#####",
            
            "     " + "\n" +
            "     " + "\n" +
            "#    " + "\n" +
            "  #  " + "\n" +
            "     " + "\n" +
            " r   " + "\n" +
            " : r " + "\n" +
            "   :#" + "\n" +
            "#####",
            
            "     " + "\n" +
            "     " + "\n" +
            "#    " + "\n" +
            "  #  " + "\n" +
            "     " + "\n" +
            "     " + "\n" +
            " r   " + "\n" +
            " : ?#" + "\n" +
            "#####",
            
            "     " + "\n" +
            "     " + "\n" +
            "#    " + "\n" +
            "  #  " + "\n" +
            "     " + "\n" +
            "     " + "\n" +
            "     " + "\n" +
            " r<j#" + "\n" +
            "#####",
            
            "     " + "\n" +
            "     " + "\n" +
            "#    " + "\n" +
            "  #  " + "\n" +
            "     " + "\n" +
            "     " + "\n" +
            "     " + "\n" +
            " <j>#" + "\n" +
            "#####"
        );
    }
    
    @Test
    public void Brollychute_on_slope()
    {
        assertWorldEvolvesLike(
            "r r r r  " + "\n" +
            "l l l l  " + "\n" +
            "# # # #  " + "\n" +
            "   ( \\   " + "\n" +
            " /     )#" + "\n" +
            "#########",
            
            "         " + "\n" +
            "r>r>r>r> " + "\n" +
            "# # # #  " + "\n" +
            "   ( \\   " + "\n" +
            " /     )#" + "\n" +
            "#########",

            "         " + "\n" +
            " r r r r " + "\n" +
            "#:#:#:#: " + "\n" +
            "   ( \\   " + "\n" +
            " /     )#" + "\n" +
            "#########",
    
            "         " + "\n" +
            "         " + "\n" +
            "#r#r#r#r " + "\n" +
            " : h e : " + "\n" +
            " /     )#" + "\n" +
            "#########",
           
            "         " + "\n" +
            "         " + "\n" +
            "# # # #  " + "\n" +
            " r ? r_r " + "\n" +
            " h     e#" + "\n" +
            "#########",
            
            "         " + "\n" +
            "         " + "\n" +
            "# # # #  " + "\n" +
            "  +j \\r  " + "\n" +
            " r    :]#" + "\n" +
            "#########",
            
            "         " + "\n" +
            "         " + "\n" +
            "# # # #  " + "\n" +
            "  j( \\!  " + "\n" +
            " /:   r>#" + "\n" +
            "#########",

            "         " + "\n" +
            "         " + "\n" +
            "# # # #  " + "\n" +
            "   ( \\j  " + "\n" +
            " /|>  :?#" + "\n" +
            "#########"
            );
    }
    
}

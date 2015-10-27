package rabbitescape.engine.logic;


import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.IsEqual.*;
import static org.hamcrest.core.IsNot.*;

import static rabbitescape.engine.Tools.*;
import static rabbitescape.engine.textworld.TextWorldManip.*;


import org.junit.Test;

import rabbitescape.engine.World;

public class TestBugs
{
    
    @Test
    public void Bump_head_on_slope()
    {
        String[] lines = {
            "  #  # ",
            "#r/#r(#",
            "#######"
            };
        
        World world = createWorld( lines );
        for ( int i = 0 ; i < 2; i++ )
        {
            world.step();
        }
        
        String[] resultLines=renderCompleteWorld( world, false );
        
        // The rabbits should not be able
        // to climb the slopes 
        String[] linesBad = { 
            "  #r #r",
            "# /# (#",
            "#######"
            };
        assertThat( resultLines, not( equalTo( linesBad ) ) );
    }

}

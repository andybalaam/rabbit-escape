package rabbitescape.engine.solution;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.Test;

public class TestPlayer
{
    
    @Test
    public void Steps_selection_and_placement()
    {
        Player p = new Player( SolutionParser.parse( "3;bash;(0,0);2;3;bridge&(1,1);1" ) );
        String playRecord = PlayPlayer( p );
        String expected =
            "step" + "\n" +
            "step" + "\n" +
            "step" + "\n" +
            "step:SelectAction( bash )" + "\n" +
            "step:PlaceTokenAction( 0, 0 )" + "\n" +
            "step" + "\n" +
            "step" + "\n" +
            "step" + "\n" +
            "step" + "\n" +
            "step" + "\n" +
            "step:SelectAction( bridge ):PlaceTokenAction( 1, 1 )" + "\n" +
            "step" + "\n";
        assertThat(playRecord, equalTo( expected ) );
    }

    
    public static String PlayPlayer( Player p )
    {
        String ret="";
        
        while( p.hasMoreSteps() )
        {
            ret += "step";
            if( p.stepAndCheckForActions() )
            {
                CommandAction[] actions = p.getActions();
                for( CommandAction a : actions )
                {
                    ret += ":" + a.toString();
                }
            }
            else
            {
            }
            ret += "\n";
        }
        return ret;
    }
}

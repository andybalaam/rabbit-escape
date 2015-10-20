package rabbitescape.ui.swing;

import static org.hamcrest.core.IsNot.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class TestGitHubInterface
{

    @Test
    public void TestBacktickMarkdowWorld()
    {
        // strings from github json have actual \n, not newline chars
        String issueBodyText = 
            "Some chat\\n" +
            "```\\n"+
            ":name=Bunny\\n"+
            ":description=Boiler\\n"+
            "#   r  \\n"+
            "#######\\n+"+
            "```\\n"+
            "More waffle";
        
        GitHubIssue ghi = new GitHubIssue();
        ghi.setBody( issueBodyText );
        String wrappedWorld = ghi.getWorld( 0 );
        // now newlines
        String expectedWrappedWorld =
            ":name=Bunny\n"+
            ":description=Boiler\n"+
            "#   r  \n"+
            "#######\n+";
        /// @TODO Use hamcrest here
        assertTrue( 0 == wrappedWorld.compareTo( expectedWrappedWorld ) );
    }
    
}

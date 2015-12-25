package rabbitescape.ui.swing;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

public class TestGitHubInterface
{

    @Test
    public void Strip_trailing_spaces_from_meta_lines()
    {
        // Strings from github json have actual \n, not newline chars
        String issueBodyText =
                "```\\n" +
                ":name=Bunny   \\n" +
                ":description=Boiler\\n" +
                "#   r  \\n" +
                "#######\\n" +
                "```\\n";

        GitHubIssue ghi = new GitHubIssue( 1,
            "Level: test",
            issueBodyText );
        String wrappedWorld = ghi.getWorld( 0 );
        String expectedWrappedWorld =
            ":name=Bunny\n" +
                ":description=Boiler\n" +
                "#   r  \n" +
                "#######\n";
        assertThat( wrappedWorld, equalTo( expectedWrappedWorld ) );
    }

    @Test
    public void Backtick_markdown_world()
    {
        String issueBodyText =
            "Some chat\\n" +
                "```\\n" +
                ":name=Bunny\\n" +
                ":description=Boiler\\n" +
                "#   r  \\n" +
                "#######\\n" +
                "```\\n" +
                "More waffle";

        GitHubIssue ghi = new GitHubIssue( 1,
            "Level: test",
            issueBodyText );
        String wrappedWorld = ghi.getWorld( 0 );
        String expectedWrappedWorld =
            ":name=Bunny\n" +
                ":description=Boiler\n" +
                "#   r  \n" +
                "#######\n";
        assertThat( wrappedWorld, equalTo( expectedWrappedWorld ) );
    }

    @Test
    public void Strip_trailing_white_from_meta_lines()
    {
        String issueBodyText =
            "Some chat\\n" +
                "```\\n" +
                ":name=Bunny\\n" +
                ":description=Boiler   \\n" +
                "#   r  \\n" +
                "#######\\n" +
                "```\\n" +
                "More waffle";

        GitHubIssue ghi = new GitHubIssue( 1,
            "Level: test",
            issueBodyText );
        String wrappedWorld = ghi.getWorld( 0 );
        String expectedWrappedWorld =
            ":name=Bunny\n" +
                ":description=Boiler\n" +
                "#   r  \n" +
                "#######\n";
        assertThat( wrappedWorld, equalTo( expectedWrappedWorld ) );
    }

    @Test
    public void Backtick_markdown_world_no_preamble()
    {
        String issueBodyText =
            "```\\n" +
                ":name=Bunny\\n" +
                ":description=Boiler\\n" +
                "#   r  \\n" +
                "#######\\n" +
                "```";

        GitHubIssue ghi = new GitHubIssue( 1,
            "Level: test",
            issueBodyText );
        String wrappedWorld = ghi.getWorld( 0 );
        // now newlines
        String expectedWrappedWorld =
            ":name=Bunny\n" +
                ":description=Boiler\n" +
                "#   r  \n" +
                "#######\n";
        assertThat( wrappedWorld, equalTo( expectedWrappedWorld ) );
    }

    /**
     * @brief test parsing of world from 4 space indented markdown.
     */
    @Test
    public void Indent_markdown_world()
    {
        String issueBodyText =
            "Some chat\\n" +
                "    :name=Bunny\\n" +
                "    :description=Boiler\\n" +
                "    #   r  \\n" +
                "          #\\n" +
                "    #######\\n" +
                "\\n" +
                "More waffle";
        GitHubIssue ghi = new GitHubIssue( 1,
            "Level: test",
            issueBodyText );
        String wrappedWorld = ghi.getWorld( 0 );
        String expectedWrappedWorld =
            ":name=Bunny\n" +
                ":description=Boiler\n" +
                "#   r  \n" +
                "      #\n" +
                "#######\n";
        assertThat( wrappedWorld, equalTo( expectedWrappedWorld ) );
    }

    /**
     * @brief 6-space indented. tab indented. backticked.
     */
    @Test
    public void Many_world_markdown()
    {
        String issueBodyText =
            "Dear sir,\\n" +
                "Herein are some levels\\n" +
                "      :name=Level1\\n" +
                "      # #####\\n" +
                "Here is the next\\n" +
                "      :name=Level2\\n" +
                "      ## ####\\n" +
                "The next\\n" +
                "\\t:name=Level3\\n" +
                "\\t### ###\\n" +
                "And the last\\n" +
                "```\\n" +
                ":name=Level4\\n" +
                "#### ##\\n" +
                "```\\n" +
                "Regards\\n";
        GitHubIssue ghi = new GitHubIssue( 1,
            "Level: test",
            issueBodyText );
        String wrappedWorld, expectedWrappedWorld;

        wrappedWorld = ghi.getWorld( 0 );
        expectedWrappedWorld = ":name=Level1\n" +
            "# #####\n";
        assertThat( wrappedWorld, equalTo( expectedWrappedWorld ) );

        wrappedWorld = ghi.getWorld( 1 );
        expectedWrappedWorld = ":name=Level2\n" +
            "## ####\n";
        assertThat( wrappedWorld, equalTo( expectedWrappedWorld ) );

        wrappedWorld = ghi.getWorld( 2 );
        expectedWrappedWorld = ":name=Level3\n" +
            "### ###\n";
        assertThat( wrappedWorld, equalTo( expectedWrappedWorld ) );

        wrappedWorld = ghi.getWorld( 3 );
        expectedWrappedWorld = ":name=Level4\n" +
            "#### ##\n";
        assertThat( wrappedWorld, equalTo( expectedWrappedWorld ) );

        String expectedBody = "\n*****\n" +
            "Dear sir,\n" +
            "Herein are some levels\n" +
            "-----\n" +
            "\n" +
            "Here is the next\n" +
            "-----\n" +
            "\n" +
            "The next\n" +
            "-----\n" +
            "\n" +
            "And the last\n" +
            "-----\n" +
            "\n" +
            "Regards\n";
        assertThat( ghi.getBody(), equalTo( expectedBody ) );
    }

    @Test
    public void No_infinite_loop()
    {
        String issueBodyText =
            "```\\r\\n"
                +
                ":name=Planet Paradise\\r\\n"
                +
                ":description=Don't lose your gravity and fall off the planet just get to the core.\\r\\n"
                +
                ":author_name=GamingInky\\r\\n" +
                ":author_url=https://github.com/GamingInky\\r\\n" +
                ":hint.1=You have to smash the planet.\\r\\n" +
                ":hint.2=The lower you are the better.\\r\\n" +
                ":hint.3=Bridging would be useful at the end.\\r\\n" +
                ":num_rabbits=1\\r\\n" +
                ":num_to_save=1\\r\\n" + ":bash=2\\r\\n" +
                ":bridge=2\\r\\n" +
                ":music=\\r\\n" +
                "                 Q         \\r\\n" +
                "                          #\\r\\n" +
                "                          #\\r\\n" +
                "               # #         \\r\\n" +
                "                ###        \\r\\n" +
                "               #####    #  \\r\\n" +
                "           #  #######   ## \\r\\n" +
                "          #################\\r\\n" +
                "         # #  ## O###   ## \\r\\n" +
                "        #        ###    #  \\r\\n" +
                "       #        ###        \\r\\n" +
                "     #           #         \\r\\n" +
                "     ########## # #        \\r\\n" +
                "```  \\r\\n" +
                "\\r\\n" +
                "Please somebody help me make theese levels harder";

        new GitHubIssue( 1, "Level: test", issueBodyText );
    }
}

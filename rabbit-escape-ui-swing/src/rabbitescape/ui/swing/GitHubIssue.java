package rabbitescape.ui.swing;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @brief encapsulates what has been retrieved about a github issue
 */
public class GitHubIssue
{
    private int number; /**< @brief github issue number.*/
    private boolean isLevel;
    private boolean isBug;
    private String body; /**< @brief body text excluding world text. */
    private String title;
    private ArrayList<String> wrappedWorlds; /**< @brief Worlds are []. These have \n */
    private final String replaceWorldsWith = "\n-----\n";

    public GitHubIssue()
    {
        wrappedWorlds = new ArrayList<String>();
    }

    public int getNumber()
    {
        return number;
    }

    public void setNumber( int number )
    {
        this.number = number;
    }

    public boolean isLevel()
    {
        return isLevel;
    }

    public void setLevel( boolean isLevel )
    {
        this.isLevel = isLevel;
    }

    public boolean isBug()
    {
        return isBug;
    }

    public void setBug( boolean isBug )
    {
        this.isBug = isBug;
    }
    
    /**
     * @param i  Some issues contain multiple worlds. Ask for them by index.
     * @return World string with newline characters.
     */
    public String getWorld( int i ){
        if( i >= wrappedWorlds.size() )
        {
            return null;
        }
        return wrappedWorlds.get( i );
    }

    public String getBody()
    {
        return body;
    }

    /**
     * @brief Keeps a copy of the body text and parses out the worlds.
     */
    public void setBody( String bodyIn )
    {
        body = bodyIn;
        extractBacktickWorlds(body);
        body = stripEscape(body);
        body = realNewlines(body);
    }
    
    /**
     * @brief parse out worlds from markdown contained in triple backticks
     */
    private String extractBacktickWorlds(String text)
    {
        Pattern worldPattern = Pattern.compile( "```(.*?)```" );
        Matcher worldMatcher = worldPattern.matcher( text );
        while ( worldMatcher.find() )
        {
            String worldWrapped = worldMatcher.group(1);
            worldWrapped = stripEscape(worldWrapped);
            worldWrapped = realNewlines(worldWrapped); 
            wrappedWorlds.add( fixWorld(worldWrapped) );
        }
        
        return text.replaceAll( "```(.*?)```", replaceWorldsWith );
    }

    /**
     * @brief parse out worlds from markdown contained in indent blocks
     */
    private String extractIndentWorlds(String text)
    {
        // at least 4 spaces or a tab
        Pattern firstLinePattern = Pattern.compile( "^(\\t| {4,}+)" );
        Matcher firstLineMatcher = firstLinePattern.matcher( text );
        while ( firstLineMatcher.find())
        {
            String blockPrefix = firstLineMatcher.group(1);
            Pattern subsequentLinePattern = Pattern.compile( "^"+blockPrefix+"(.*?)$" );
            Matcher subsequentLineMatcher = subsequentLinePattern.matcher( text );
            // Do I need to store the result of region back in the Matcher?
            subsequentLineMatcher.region( firstLineMatcher.regionStart(), text.length() -1 );
            String worldWrapped = "";
            int prevEndIndex = -1;
            while (subsequentLineMatcher.find())
            {
                // @TODO maybe OBi-Ones
                if (subsequentLineMatcher.regionStart() != prevEndIndex)//check for lines between matches
                {
                    break;
                }
                prevEndIndex = subsequentLineMatcher.regionEnd();
                worldWrapped = worldWrapped + subsequentLineMatcher.group(1) + "\n";
            }
            wrappedWorlds.add( fixWorld(worldWrapped) );
        }
        // @TODO return the body text with the worlds stripped
        return text;
    }
    
    /**
     * @brief Perform some automatic fixing
     * Can't be as strict as when loading from files.
     */
    private String fixWorld(String world)
    {
        String fixed = world;
        //remove blank lines
        fixed = fixed.replaceAll("\n\n","\n");
        fixed = fixed.replaceAll( "^\n", "" );
        return fixed;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle( String titleIn )
    {
        this.title = stripEscape(titleIn);
    }
    
    /**
     * @brief Tidy up escape characters.
     * - Remove \ before "
     * - Remove \r to covert Win EOL to Unix EOL
     */
    public static String stripEscape(String s1)
    {
        String s2;
        s2 = s1.replaceAll( "(\\\\r)", "" );
        /// @TODO this is removing \" instead of just \ which are followed by "
        s2 = s2.replaceAll( "(\\\\)\"", "" );
        return s2;
    }
    
    /**
     * @brief Replace \n with actual newline chars.
     */
    public static String realNewlines(String s)
    {
        return s.replaceAll( "\\\\n", "\n" );
    }
}
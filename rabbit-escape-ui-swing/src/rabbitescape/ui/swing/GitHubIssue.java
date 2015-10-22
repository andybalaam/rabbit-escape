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
        body = extractBacktickWorlds(body);
        body = extractIndentWorlds(body);
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
    
    private void p(String s)
    {
        System.out.println(s);
    }

    /**
     * @brief parse out worlds from markdown contained in indent blocks
     */
    private String extractIndentWorlds(String text)
    {
        // at least 4 spaces or a tab
        // the json has \n in, not newline char
        // after java compiler \\\\ becomes \\, after regex compile \
        Pattern firstLinePattern = Pattern.compile( "\\\\n(\\\\t| {4,}+)" );
        //Pattern firstLinePattern = Pattern.compile( "\\\\n(\\t)" );
        p("text:\n"+text);
        p("01234567890123456789012345678901234567890123456789012345678901234567890");
        p("00000000001111111111222222222233333333334444444444555555555566666666667");
        Matcher firstLineMatcher = firstLinePattern.matcher( text );
        while ( firstLineMatcher.find())
        {
            p("\n*** new block");
            p("firstLineMatcher.group(1): "+firstLineMatcher.start(1)+"-"+firstLineMatcher.end(1));
            String blockPrefix = firstLineMatcher.group(1);
            if (0 == blockPrefix.compareTo( "\\t" ))
            {
                blockPrefix = "\\\\t"; //reform so regex is char pair, not tab char.
            }
            Pattern subsequentLinePattern = Pattern.compile( "\\\\n"+blockPrefix+"(.+?)\\\\n" );
            Matcher subsequentLineMatcher = subsequentLinePattern.matcher( text );
            // Do I need to store the result of region back in the Matcher?
            subsequentLineMatcher.region( firstLineMatcher.start(), text.length() - 1 );
            String worldWrapped = "";
            int prevEndIndex = -1;
            while (subsequentLineMatcher.find())
            {
                p("subsequentLineMatcher.group(1):>"+subsequentLineMatcher.group(1)+"<"+subsequentLineMatcher.start(1)+"-"+subsequentLineMatcher.end(1));
                
                
                p(""+prevEndIndex+":"+subsequentLineMatcher.start());
                /* Check for lines between matches, note this is to the 
                   start of the whole match including the indent string.
                   First time through, let the -1 past. */
                if ( -1 != prevEndIndex &&
                    subsequentLineMatcher.start() != prevEndIndex )
                {
                    break;
                }
                worldWrapped = worldWrapped + subsequentLineMatcher.group(1) + "\n";
                p("got past break");
                prevEndIndex = subsequentLineMatcher.end() - 2;
                // the end of the group is 2 chars before the end of the whole match, so it 
                // can find the \n again to start the next line.
                subsequentLineMatcher.region( subsequentLineMatcher.end(1), text.length() );
            }
            p("set flm for next block, region: "+prevEndIndex+"-"+text.length());
            /*if ( -1 == prevEndIndex) {
                break; //TODO is this the right thing to do?
            }*/
            firstLineMatcher = firstLineMatcher.region( prevEndIndex, text.length());
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
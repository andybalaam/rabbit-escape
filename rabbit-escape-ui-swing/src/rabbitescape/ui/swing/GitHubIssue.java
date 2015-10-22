package rabbitescape.ui.swing;

import java.util.ArrayList;
import java.util.Collections;
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
        // posiitons of worlds in the body text;
        ArrayList<Integer> startIndices = new ArrayList<Integer>(); 
        ArrayList<Integer> endIndices = new ArrayList<Integer>(); 
        body = bodyIn;
        findBacktickWorlds( startIndices, endIndices );
        findIndentWorlds( startIndices, endIndices );
        ArrayList<String> newWorldOrder = new ArrayList<String>( wrappedWorlds.size() );
        for ( int i=0; i<startIndices.size(); i++)
        {
            newWorldOrder.add( "" );
        }
        // re-order worlds and remove worlds from body text
        for ( int i=0; i<startIndices.size(); i++)
        {
            int max = Collections.max( startIndices );
            int maxIndex = -1;
            for (int j=0; j< startIndices.size() ;j++)
            {
                if (max == startIndices.get( j ))
                {
                    maxIndex = j;
                    break;
                }
            }
            newWorldOrder.set( startIndices.size() - 1 - i, wrappedWorlds.get( maxIndex ) );
            body=body.substring( 0, startIndices.get(maxIndex) ) + 
                 replaceWorldsWith +
                 body.substring( endIndices.get(maxIndex) );
            startIndices.set( maxIndex, 0 ); // so it is not found again
        
        }
        wrappedWorlds = newWorldOrder;
        
        body = stripEscape(body);
        body = realNewlines(body);
    }
    
    /**
     * @brief parse out worlds from markdown contained in triple backticks
     */
    private void findBacktickWorlds( ArrayList<Integer> startIndices, 
                                        ArrayList<Integer> endIndices )
    {
        Pattern worldPattern = Pattern.compile( "(\\\\n)?```(.*?)```" );
        Matcher worldMatcher = worldPattern.matcher( body );
        while ( worldMatcher.find() )
        {
            String worldWrapped = worldMatcher.group(2);
            worldWrapped = stripEscape(worldWrapped);
            worldWrapped = realNewlines(worldWrapped);
            wrappedWorlds.add( fixWorld(worldWrapped) );
            startIndices.add( worldMatcher.start() );
            endIndices.add(  worldMatcher.end() );
        }

    }

    /**
     * @brief parse out worlds from markdown contained in indent blocks
     */
    private void findIndentWorlds( ArrayList<Integer> startIndices, 
                                      ArrayList<Integer> endIndices )
    {
        // at least 4 spaces or a tab
        // the json has \n in, not newline char
        // after java compiler \\\\ becomes \\, after regex compile \
        Pattern firstLinePattern = Pattern.compile( "\\\\n(\\\\t| {4,}+)" );
        //Pattern firstLinePattern = Pattern.compile( "\\\\n(\\t)" );

        Matcher firstLineMatcher = firstLinePattern.matcher( body );
        while ( firstLineMatcher.find())
        {
            startIndices.add( firstLineMatcher.start() );
            String blockPrefix = firstLineMatcher.group(1);
            if (0 == blockPrefix.compareTo( "\\t" ))
            {
                blockPrefix = "\\\\t"; //reform so regex is char pair, not tab char.
            }
            Pattern subsequentLinePattern = Pattern.compile( "\\\\n"+blockPrefix+"(.+?)\\\\n" );
            Matcher subsequentLineMatcher = subsequentLinePattern.matcher( body );
            // Do I need to store the result of region back in the Matcher?
            subsequentLineMatcher.region( firstLineMatcher.start(), body.length() - 1 );
            String worldWrapped = "";
            int prevEndIndex = -1;
            while (subsequentLineMatcher.find())
            {
                /* Check for lines between matches, note this is to the 
                   start of the whole match including the indent string.
                   First time through, let the -1 past. */
                if ( -1 != prevEndIndex &&
                    subsequentLineMatcher.start() != prevEndIndex )
                {
                    break;
                }
                worldWrapped = worldWrapped + subsequentLineMatcher.group(1) + "\n";
                prevEndIndex = subsequentLineMatcher.end() - 2;
                // the end of the group is 2 chars before the end of the whole match, so it 
                // can find the \n again to start the next line.
                subsequentLineMatcher.region( subsequentLineMatcher.end(1), body.length() );
            }
            endIndices.add(prevEndIndex);
            firstLineMatcher = firstLineMatcher.region( prevEndIndex, body.length());
            wrappedWorlds.add( fixWorld(worldWrapped) );
        }
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
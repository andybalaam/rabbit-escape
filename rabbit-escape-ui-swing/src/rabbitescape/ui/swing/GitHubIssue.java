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
    private static final String replaceWorldsWith = "\n-----\n";

    public GitHubIssue()
    {
        wrappedWorlds = new ArrayList<String>();
        body = "";
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

    public boolean isBug()
    {
        return isBug;
    }

    public void setLabels( String labelsJson )
    {
        Pattern labelPattern = Pattern.compile( "\"name\":\"(.*?)\"" );
        Matcher labelMatcher = labelPattern.matcher( labelsJson );
        while ( labelMatcher.find() )
        {
            if ( 0 == labelMatcher.group(1).compareTo( "bug" ) )
            {
                isBug = true;
            }
            else if ( 0 == labelMatcher.group(1).compareTo( "level" ) )
            {
                isLevel = true;
            }
        }
    }
    
    public void fetchComments(GitHubClient ghc)
    {
        ghc.fetchComments(this);
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
    public void addToBody( String bodyIn )
    {
        // Positions of worlds in the body text;
        ArrayList<Integer> startIndices = new ArrayList<Integer>(); 
        ArrayList<Integer> endIndices = new ArrayList<Integer>(); 
        String bodyAdd = ""+bodyIn;
        ArrayList<String> wrappedWorldsAdd = new ArrayList<String>();
        findBacktickWorlds( bodyAdd, wrappedWorldsAdd, startIndices, endIndices );
        findIndentWorlds( bodyAdd, wrappedWorldsAdd, startIndices, endIndices );
        ArrayList<String> newWorldOrder = new ArrayList<String>( wrappedWorldsAdd.size() );
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
            newWorldOrder.set( startIndices.size() - 1 - i, wrappedWorldsAdd.get( maxIndex ) );
            bodyAdd=bodyAdd.substring( 0, startIndices.get(maxIndex) ) + 
                    replaceWorldsWith +
                    bodyAdd.substring( endIndices.get(maxIndex) );
            startIndices.set( maxIndex, 0 ); // So it is not found again
        
        }
        wrappedWorlds.addAll( newWorldOrder );
        
        bodyAdd = stripEscape(bodyAdd);
        bodyAdd = realNewlines(bodyAdd);
        body = body + bodyAdd;
    }
    
    /**
     * @brief Add location of world in the body string to the list.
     *        Only add if the new world is not inside another.
     *        Backtick worlds are parsed first, so they win.
     */
    private static void checkAddWorldIndices( ArrayList<Integer> startIndices, 
                                       ArrayList<Integer> endIndices,
                                       int startIndex,
                                       int endIndex )
    {
        for(int i = 0; i < startIndices.size(); i++){
            if ( startIndex >= startIndices.get( i ) && startIndex < endIndices.get( i ) )
            { // It's in the range of another world, don't add.
                return;
            }
        }
        startIndices.add(startIndex);
        endIndices.add( endIndex );
    }


    /**
     * @brief parse out worlds from markdown contained in triple backticks
     */
    private static void findBacktickWorlds( String bodyAdd,
                                            ArrayList<String> braveNewWrappedWorlds,
                                            ArrayList<Integer> startIndices, 
                                            ArrayList<Integer> endIndices )
    {
        Pattern worldPattern = Pattern.compile( "(\\\\n)?```(.*?)```" );
        Matcher worldMatcher = worldPattern.matcher( bodyAdd );
        while ( worldMatcher.find() )
        {
            String worldWrapped = worldMatcher.group(2);
            braveNewWrappedWorlds.add( fixWorld(worldWrapped) );
            checkAddWorldIndices( startIndices, 
                                  endIndices, 
                                  worldMatcher.start(), 
                                  worldMatcher.end() );
        }
    }


    /**
     * @brief parse out worlds from markdown contained in indent blocks
     * Appends to the ArrayList<Integer> describing the worlds location.
     */
    private static void findIndentWorlds( String bodyAdd,
                                          ArrayList<String> braveNewWrappedWorlds,
                                          ArrayList<Integer> startIndices, 
                                          ArrayList<Integer> endIndices )
    {
        // at least 4 spaces or a tab
        // the json has \n in, not newline char
        // after java compiler \\\\ becomes \\, after regex compile \
        Pattern firstLinePattern = Pattern.compile( "\\\\n(\\\\t| {4,}+)" );
        Matcher firstLineMatcher = firstLinePattern.matcher( bodyAdd );
        int worldStart, worldEnd;
        while ( firstLineMatcher.find())
        {
            worldStart = firstLineMatcher.start() ;
            String blockPrefix = firstLineMatcher.group(1);
            if (0 == blockPrefix.compareTo( "\\t" ))
            {
                blockPrefix = "\\\\t"; // Reform so regex is char pair, not tab char.
            }
            Pattern subsequentLinePattern = Pattern.compile( "\\\\n"+blockPrefix+"(.+?)\\\\n" );
            Matcher subsequentLineMatcher = subsequentLinePattern.matcher( bodyAdd );
            // Do I need to store the result of region back in the Matcher?
            subsequentLineMatcher.region( firstLineMatcher.start(), bodyAdd.length() - 1 );
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
                subsequentLineMatcher.region( subsequentLineMatcher.end(1), bodyAdd.length() );
            }
            braveNewWrappedWorlds.add( fixWorld(worldWrapped) );
            if ( -1 == prevEndIndex ) 
            { //indent block runs to the end of the body
                worldEnd = bodyAdd.length();
                checkAddWorldIndices( startIndices, endIndices, worldStart, worldEnd );
                break;
            }
            else
            {
                worldEnd = prevEndIndex;
                firstLineMatcher = firstLineMatcher.region( prevEndIndex, bodyAdd.length());
                checkAddWorldIndices( startIndices, endIndices, worldStart, worldEnd );
            }
        }
    }
    
    /**
     * @brief Perform some automatic fixing
     * Can't be as strict as when loading from files.
     */
    private static String fixWorld(String world)
    {
        String fixed = "" + world;
        
        fixed = stripEscape(fixed);
        fixed = realNewlines(fixed);
        
        // Remove blank lines
        fixed = fixed.replaceAll("\n\n","\n");
        fixed = fixed.replaceAll( "^\n", "" );
        
        // Strip trailing spaces from meta lines
        Pattern p = Pattern.compile("^:(.*?) *?$", Pattern.MULTILINE);
        Matcher m = p.matcher(fixed);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, ":$1");
        }
        m.appendTail(sb);
        fixed = sb.toString();
        
        
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
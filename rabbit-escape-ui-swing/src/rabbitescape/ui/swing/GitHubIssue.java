package rabbitescape.ui.swing;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitHubIssue
{
    private int number; /**< @brief github issue number.*/
    private boolean isLevel;
    private boolean isBug;
    private String body; /**< @brief body text excluding world text. */
    private String title;
    private ArrayList<String> wrappedWorlds; /**< @brief Worlds are []. These have \n */

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
        Pattern worldPattern = Pattern.compile( "```(.*?)```" );
        Matcher worldMatcher = worldPattern.matcher( bodyIn );
        while ( worldMatcher.find() )
        {
            String worldWrapped = worldMatcher.group(1);
            worldWrapped = stripEscape(worldWrapped);
            worldWrapped = realNewlines(worldWrapped); 
            wrappedWorlds.add( worldWrapped );
            //System.out.println("***"+getNumber()+"\n"+worldWrapped+"\n***");
        }
        
        this.body = bodyIn.replaceAll( "```(.*?)```", "\n-----\n" );
        this.body = stripEscape(this.body);
        this.body = realNewlines(this.body);
        System.out.println("***"+this.body+"\n***");
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
        // @TODO this is removing \" instead of just \ which are followed by "
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
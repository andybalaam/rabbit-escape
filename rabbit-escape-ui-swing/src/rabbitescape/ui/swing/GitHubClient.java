package rabbitescape.ui.swing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitHubClient
{
    public final String baseURL = "https://api.github.com/repos/andybalaam/rabbit-escape/issues";
    public final String acceptHeader = "application/vnd.github.v3+json";
    private ArrayList<GitHubIssue> issues;
    
    public GitHubClient()
    {
        String jsonIssues = apiCall( "" );
        issues = parseIssues( jsonIssues );
        
    }
    
    public GitHubIssue getIssue(int index)
    {
        if( index<0 || index>= issues.size())
        {
            return null;
        }
        return issues.get( index );
    }
    
    public int getIndexOfNumber( int issueNumber)
    {
        for( int i=0; i<issues.size(); i++ )
        {
            GitHubIssue ghi = issues.get( i );
            if( ghi.getNumber() == issueNumber )
            {
                return i;
            }
        }
        return -1;
    }
    
    private ArrayList<GitHubIssue> parseIssues(String json)
    {   /// @TODO this is extremely crufty: hacking out most of the URL to split on.
        // This leaves the issue number as the first thing in the string.
        Pattern issuePattern = Pattern.compile( "\\{\"url\":\"https://api\\.github\\.com/repos/andybalaam/rabbit-escape/issues/" );
        String[] jsonIssuesStrings = issuePattern.split( json );
        ArrayList<GitHubIssue> ret= new ArrayList<GitHubIssue>();;
        Pattern numberPattern=Pattern.compile( "^([0-9]++)" );
        Pattern titlePattern = Pattern.compile( "\"title\":\"([^\"\\\\]*(?:\\\\.[^\"\\\\]*)*)\"" );
        Pattern bodyPattern = Pattern.compile( "\"body\":\"([^\"\\\\]*(?:\\\\.[^\"\\\\]*)*)\"" ); 
        for( int i = 0; i < jsonIssuesStrings.length; i++ )
        {
            GitHubIssue ghi = new GitHubIssue();
            Matcher numberMatcher = numberPattern.matcher( jsonIssuesStrings[i] );
            Matcher titleMatcher = titlePattern.matcher( jsonIssuesStrings[i] );
            Matcher bodyMatcher = bodyPattern.matcher( jsonIssuesStrings[i] );
            if (!numberMatcher.find())
            {
                continue;
            }
            titleMatcher.find();
            bodyMatcher.find();
            ghi.setNumber( Integer.parseInt( numberMatcher.group(1) ) );
            //System.out.println(jsonIssuesStrings[i]);
            ghi.setTitle( titleMatcher.group(1) );
            ghi.setBody( bodyMatcher.group(1) );
            //System.out.println("" + ghi.getNumber() + " " + ghi.getTitle());
            //System.out.println("" + ghi.getBody());
            ret.add( ghi );
        }
        return ret;
    }
    
    /// @TODO progress bar if its taking a while?
    private String apiCall( String endURL )
    {
        URL url;
        InputStream is = null;
        BufferedReader br;
        String line;
        String out="";

        try
        { /// @TODO send header requesting api V3
            url = new URL( baseURL+endURL );
            is = url.openStream();  // throws an IOException
            br = new BufferedReader( new InputStreamReader( is ) );

            while ( ( line = br.readLine() ) != null ) /// @TODO maybe read more than a line at time
            {
                out = out + line + "\n";
            }
            return out;
        }
        catch ( MalformedURLException eMU ) 
        {
             eMU.printStackTrace();
        } 
        catch ( IOException eIO )
        {
             eIO.printStackTrace();
        } 
        finally 
        {
            try 
            {
                if (is != null) is.close();
            } 
            catch ( IOException eIO )
            {
                // just an attempt at tidying. can ignore
            }
        }
        return null;
    }
}

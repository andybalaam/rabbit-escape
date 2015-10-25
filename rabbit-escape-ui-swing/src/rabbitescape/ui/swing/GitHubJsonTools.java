package rabbitescape.ui.swing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitHubJsonTools
{

    public static String getStringValue(String json, String key)
    {
        Pattern p = Pattern.compile( "\"" + key + "\":\"([^\"\\\\]*(?:\\\\.[^\"\\\\]*)*)\"" );
        Matcher m = p.matcher( json );
        m.find();
        return m.group( 1 );
    }


    public static int getIntValue(String json, String key)
    {
        Pattern p = Pattern.compile( "\"" + key + "\":([0-9]+)" );
        Matcher m = p.matcher( json );
        m.find();
        return Integer.parseInt( m.group( 1 ) );
    }
}

package rabbitescape.ui.swing;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitHubJsonTools
{

    public static String getStringValue( String json, String key )
    {
        Pattern p = Pattern.compile( "\"" + key + "\":\"([^\"\\\\]*(?:\\\\.[^\"\\\\]*)*)\"" );
        Matcher m = p.matcher( json );
        m.find();
        return m.group( 1 );
    }


    public static int getIntValue( String json, String key )
    {
        Pattern p = Pattern.compile( "\"" + key + "\":([0-9]+)" );
        Matcher m = p.matcher( json );
        m.find();
        return Integer.parseInt( m.group( 1 ) );
    }
    
    /**
     * @return The JSON string inside the []
     */
    public static String getArrayString( String json, String key )
    {
        Pattern p = Pattern.compile( "\"" + key + "\":\\[(.*?)\\]" );
        Matcher m = p.matcher( json );
        m.find();
        return m.group( 1 );
    }
    
    /**
     * @param key Something of the form array_name.member_name
     * 
     * "thing1":"stuff"
     * "thing2_array:[
     *   {
     *     "subthing1":"more stuff"
     *   }
     *   {
     *     "subthing1":"even more"
     *   }
     * ]
     * 
     * Using a key of "thing2_array.subthing1" will return [ "more stuff", "even more"].
     */
    public static String[] getStringValuesFromArrayOfObjects( String json, String key)
    {
        String[] keyParts = key.split( "\\." );
        String arrayString = getArrayString ( json, keyParts[0] );
        
        Pattern p = Pattern.compile( "\"" + keyParts[1] + "\":\"(.*?)\"" );
        Matcher m = p.matcher( arrayString );
        ArrayList<String> ret = new ArrayList<String>(); 
        while ( m.find() )
        {
            ret.add( m.group( 1 ) );
        }
        String[] values = new String[ret.size()];
        return ret.toArray(values);
    }
}

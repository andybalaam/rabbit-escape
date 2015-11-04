package rabbitescape.ui.swing;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitHubJsonTools
{

    public static String getStringValue( String json, String key )
    {
        Pattern p = Pattern.compile( "\"" + key
            + "\":\"([^\"\\\\]*(?:\\\\.[^\"\\\\]*)*)\"" );
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
     * @param key
     *            Something of the form array_name.member_name. If something of
     *            the form member_name is used, the whole string will be
     *            searched.
     *
     *            "thing1":"stuff" "thing2_array:[ { "subthing1":"more stuff" }
     *            { "subthing1":"even more" } ]
     *
     *            Using a key of "thing2_array.subthing1" will return [
     *            "more stuff", "even more"].
     */
    public static String[] getStringValuesFromArrayOfObjects(
        String json,
        String key )
    {
        String[] keyParts = key.split( "\\." );
        String memberName;
        String arrayString;
        if ( 2 == keyParts.length )
        { // array_name.member_name
            arrayString = getArrayString( json, keyParts[0] );
            memberName = keyParts[1];
        }
        else
        { // member_name only
            arrayString = json;
            memberName = key;
        }
        Pattern p = Pattern.compile( "\"" + memberName + "\":\"(.*?)\"" );
        Matcher m = p.matcher( arrayString );
        ArrayList<String> ret = new ArrayList<String>();
        while ( m.find() )
        {
            ret.add( m.group( 1 ) );
        }
        String[] values = new String[ret.size()];
        return ret.toArray( values );
    }

    /**
     * @brief Replace all matches, but preserves the first capturing group.
     *        Compare to String.replaceAll, which replaces the whole match. This
     *        is in the JSON class because JSON String vales have escaped
     *        characters to be cleaned up.
     * @param patternFlags The flags from java.util.regex.Pattern.
     */
    public static String regexReplacePreserveGroup( String s, String regex, int patternFlags )
    {
        Pattern p = Pattern.compile( regex, patternFlags );
        Matcher dsMatcher = p.matcher( s );
        StringBuffer sb = new StringBuffer();
        while ( dsMatcher.find() )
        {
            dsMatcher.appendReplacement( sb, "$1" );
        }
        dsMatcher.appendTail( sb );

        return sb.toString();
    }

    public static String regexReplacePreserveGroup( String s, String regex )
    {
        return regexReplacePreserveGroup( s, regex, 0);
    }
}

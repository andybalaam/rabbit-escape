package rabbitescape.engine.util;

import java.util.Map;
import java.util.regex.Matcher;

public class NamedFieldFormatter
{
    /**
     * Format a string containing named substitution parameters by substituting
     * in values from params.
     *
     * For example:
     *
     * Map<String, Object> params = new HashMap<String, Object>();
     * params.put( "thing", "life" );
     * params.put( "answer", 42 );
     * String x = format( "The meaning of ${thing} is ${answer}.", params );
     * System.out.println( x );
     *
     * Will print "The meaning of life is 42."
     *
     * @param format the format string containing parameters surrounded by ${}
     * @param params a map of parameter names to their values
     * @return the format string with parameters substituted wherever their
     *         names (surrounded by ${}) are found.
     */
    public static String format( String format, Map<String, Object> params )
    {
        String ans = format;

        for ( Map.Entry<String, Object> param : params.entrySet() )
        {
            ans = ans.replaceAll(
                "\\$\\{" + param.getKey() + "\\}",
                Matcher.quoteReplacement( String.valueOf( param.getValue() ) )
            );
        }

        return ans;
    }

}

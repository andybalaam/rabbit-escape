package rabbitescape.ui.swing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpTools
{
    /**
     * @param requestProperty
     *            Something like "Accept: application/xml", May be null.
     */
    public static String get( String urlString, String requestProperty )
        throws MalformedURLException, IOException
    {
        URL url;
        InputStream is = null;
        BufferedReader br;
        String line;
        String out = "";

        try
        {
            url = new URL( urlString );
            HttpURLConnection connection = (HttpURLConnection)url
                .openConnection();
            if ( null != requestProperty )
            {
                String[] keyValue = requestProperty.split( ": ?" );
                connection.setRequestProperty( keyValue[0], keyValue[1] );
            }
            is = connection.getInputStream(); // Throws an IOException
            br = new BufferedReader( new InputStreamReader( is ) );

            /*
             * github (?always) returns the response on one line, so this while
             * loop is not slow.
             */
            while ( ( line = br.readLine() ) != null )
            {
                out = out + line + "\n";
            }
            return out;
        }
        catch ( Exception e )
        {
            try
            {
                if ( is != null )
                    is.close();
            }
            catch ( IOException eIO )
            {
                // Just an attempt at tidying. can ignore
            }
            throw e; // Rethrow
        }
    }
}

package rabbitescape.engine.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RealFileSystem implements FileSystem
{
    @Override
    public boolean exists( String fileName )
    {
        return new File( fileName ).exists();
    }

    @Override
    public String[] readLines( String fileName )
        throws FileNotFoundException, IOException
    {
        List<String> ret = new ArrayList<String>();

        BufferedReader reader = new BufferedReader(
            new FileReader( new File( fileName ) ) );

        try
        {
            String line = reader.readLine();

            while( line != null )
            {
                ret.add( line );
                line = reader.readLine();
            }

            return ret.toArray( new String[] {} );
        }
        finally
        {
            reader.close();
        }
    }
}

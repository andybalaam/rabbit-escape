package rabbitescape.ui.text;

import static rabbitescape.engine.util.Util.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rabbitescape.engine.TextWorldManip;
import rabbitescape.engine.World;

public class Main
{
    public static void main( String[] args ) throws FileNotFoundException
    {
        new Main().run( args );
    }

    private void run( String[] args ) throws FileNotFoundException
    {
        reAssert( args.length == 1 );

        String[] lines = readWorldFromFile( args[0] );

        World world = TextWorldManip.createWorld( lines );

        GameLoop gameLoop = new GameLoop( world );

        gameLoop.run();

        // Parse args
        // for each world
        // Load file
        // create world
        // run world
    }

    private static String[] readWorldFromFile( String fileName )
        throws FileNotFoundException
    {
        List<String> ret = new ArrayList<String>();

        File f = new File( fileName );

        reAssert( f.exists() );

        BufferedReader reader = new BufferedReader( new FileReader( f ) );
        try
        {
            String line = reader.readLine();

            while( line != null )
            {
                ret.add( line.trim() );
                line = reader.readLine();
            }

            return ret.toArray( new String[] {} );
        }
        catch ( IOException e )
        {
            e.printStackTrace(); // TODO
            return null;
        }
        finally
        {
            try
            {
                reader.close();
            }
            catch ( IOException e )
            {
                e.printStackTrace(); // TODO
            }
        }
    }
}

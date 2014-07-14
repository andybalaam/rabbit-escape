package rabbitescape.ui.text;

import static rabbitescape.engine.util.Util.*;

import java.io.PrintStream;
import java.util.Locale;

import rabbitescape.engine.LoadWorldFile;
import rabbitescape.engine.World;
import rabbitescape.engine.util.FileSystem;
import rabbitescape.engine.util.RealFileSystem;

public class Main
{
    private static final int SUCCESS             = 0;
    private static final int FAILED_TO_LOAD_FILE = 1;

    private final FileSystem fs;
    private final PrintStream out;
    private final Locale locale;

    public Main( FileSystem fs, PrintStream out, Locale locale )
    {
        this.fs = fs;
        this.out = out;
        this.locale = locale;
    }

    public static void main( String[] args )
    {
        Main m = new Main(
            new RealFileSystem(), System.out, Locale.getDefault() );

        int status = m.run( args );

        System.exit( status );
    }

    public int run( String[] args )
    {
        reAssert( args.length == 1 );

        try
        {
            World world = new LoadWorldFile( fs ).load( args[0] );

            GameLoop gameLoop = new GameLoop( world );

            gameLoop.run();
        }
        catch( LoadWorldFile.Failed e )
        {
            out.println( e.translate( locale ) );
            return FAILED_TO_LOAD_FILE;
        }

        return SUCCESS;
    }
}

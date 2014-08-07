package rabbitescape.render;

import static rabbitescape.engine.util.Util.*;

import java.io.PrintStream;
import java.util.Locale;

import rabbitescape.engine.LoadWorldFile;
import rabbitescape.engine.World;
import rabbitescape.engine.util.FileSystem;

public abstract class Main
{
    public abstract GameLoop createGameLoop( World world );

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

    public void run( String[] args )
    {
        int status = launchGame( args );
        System.exit( status );
    }

    public int launchGame( String[] args )
    {
        reAssert( args.length >= 1 );

        try
        {
            World world = new LoadWorldFile( fs ).load( args[0] );

            GameLoop gameLoop = createGameLoop( world );

            gameLoop.run( args );

            gameLoop.showResult();
        }
        catch( LoadWorldFile.Failed e )
        {
            out.println( e.translate( locale ) );
            return FAILED_TO_LOAD_FILE;
        }

        return SUCCESS;
    }
}

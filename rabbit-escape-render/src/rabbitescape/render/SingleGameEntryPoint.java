package rabbitescape.render;

import static rabbitescape.engine.util.Util.*;

import java.io.PrintStream;
import java.util.Locale;

import rabbitescape.engine.IgnoreLevelWinListener;
import rabbitescape.engine.IgnoreWorldStatsListener;
import rabbitescape.engine.LevelWinListener;
import rabbitescape.engine.LoadWorldFile;
import rabbitescape.engine.World;
import rabbitescape.engine.util.FileSystem;

public abstract class SingleGameEntryPoint
{
    public abstract GameLaunch createGameLaunch(
        World world, LevelWinListener winListener );

    private static final int SUCCESS             = 0;
    private static final int FAILED_TO_LOAD_FILE = 1;
    private static final int UNKNOWN_ERROR       = 42;

    private final FileSystem fs;
    public final PrintStream out;
    private final Locale locale;

    public SingleGameEntryPoint( FileSystem fs, PrintStream out, Locale locale )
    {
        this.fs = fs;
        this.out = out;
        this.locale = locale;
    }

    public void run( String[] args )
    {
        int status = launchGame( args, new IgnoreLevelWinListener() );
        System.exit( status );
    }

    public int launchGame( String[] args, LevelWinListener winListener )
    {
        reAssert( args.length >= 1 );

        try
        {
            String levelName = args[0];

            World world = new LoadWorldFile( fs ).load(
                new IgnoreWorldStatsListener(), levelName );

            GameLaunch gameLaunch = createGameLaunch( world, winListener );

            gameLaunch.run( args );

            gameLaunch.showResult();
        }
        catch( LoadWorldFile.Failed e )
        {
            out.println( e.translate( locale ) );
            return FAILED_TO_LOAD_FILE;
        }
        catch( Throwable e )
        {
            e.printStackTrace();
            return UNKNOWN_ERROR;
        }

        return SUCCESS;
    }
}

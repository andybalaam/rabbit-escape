package rabbitescape.ui.text;

import rabbitescape.engine.IgnoreWorldStatsListener;
import rabbitescape.engine.LoadWorldFile;
import rabbitescape.engine.World;
import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.TapTimer;
import static rabbitescape.engine.i18n.Translation.*;
import rabbitescape.engine.i18n.Translation;
import rabbitescape.engine.solution.SolutionDemo;
import rabbitescape.engine.solution.SolutionRunner;
import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.engine.util.CommandLineOption;
import rabbitescape.engine.util.CommandLineOptionSet;
import rabbitescape.engine.util.FileSystem;
import rabbitescape.engine.util.RealFileSystem;
import rabbitescape.engine.util.Util;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;

public class TextMain
{
    private final TextMenu textMenu;

    public TextMain( FileSystem fs, Terminal terminal, Config config )
    {
        this.textMenu = new TextMenu( fs, terminal, config );
    }

    public static void main( String[] args )
    {
        if ( args.length == 1 && args[0].endsWith( ".rel" ) )
        { // Default is to play a single game interactively
            TextSingleGameEntryPoint.entryPoint( args );
            return;
        }

        CommandLineOption level =
            new CommandLineOption( "--level",        true );
        CommandLineOption solution =
            new CommandLineOption( "--solution",     true );
        CommandLineOption encode =
            new CommandLineOption( "--encode",       true );
        CommandLineOption decode =
            new CommandLineOption( "--decode",       true );
        CommandLineOption help =
            new CommandLineOption( "--help",         false );
        CommandLineOption noinput =
            new CommandLineOption( "--noinput",      true );
        CommandLineOption placeholders =
            new CommandLineOption( "--placeholders", true );
        CommandLineOption template =
            new CommandLineOption( "--template",     true );
        CommandLineOption gentest =
            new CommandLineOption( "--gentest",      false );
        CommandLineOption rellist =
            new CommandLineOption( "--rellist",      false );
        CommandLineOption mars =
            new CommandLineOption( "--mars",         false );
        try
        {
            CommandLineOptionSet.parse( args,
                                        level, solution, encode, decode,
                                        help, noinput, placeholders,
                                        template, gentest, rellist, mars );
            if ( mars.isPresent() )
            {
                TapTimer.matched = true;
            }
            if ( help.isPresent() )
            {
                usageMessage();
                System.exit( 0 );
            }
            if ( noinput.isPresent() )
            {
                TextSingleGameEntryPoint.entryPoint(
                    new String[] {noinput.getValue(), "noinput"} );
            }
            if ( rellist.isPresent() )
            {
                listRel();
                System.exit( 0 );
            }
            if ( gentest.isPresent() )
            {
                demo( level.getValue(), solution.getValue(), true );
                System.exit( 0 );
            }
            if ( solution.isPresent() )
            {
                demo( level.getValue(), solution.getValue(), false );
                System.exit( 0 );
            }
            if ( level.isPresent() )
            {
                TextSingleGameEntryPoint.entryPoint(
                    new String[] {level.getValue()} );
                System.exit( 0 );
            }
            if ( encode.isPresent() )
            {
                MegaCoderCLI.codec( encode );
                System.exit( 0 );
            }
            if ( decode.isPresent() )
            {
                MegaCoderCLI.codec( decode );
                System.exit( 0 );
            }
            if ( placeholders.isPresent() )
            {
                placeholders( placeholders.getValue() );
                System.exit( 0 );
            }
            if ( template.isPresent() )
            {
                template( template.getValue() );
                System.exit( 0 );
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
            System.exit( 1 );
        }

        Locale locale = Locale.getDefault();
        Translation.init( locale );

        TextMain m = new TextMain(
            new RealFileSystem(),
            new Terminal( System.in, System.out, locale ),
            TextConfigSetup.createConfig()
        );

        m.run( args );
    }

    private static void demo( 
        String relPath, 
        String solnCmdLine,  
        boolean genTest 
    )
    {

        World world = new LoadWorldFile(
            new RealFileSystem() ).load(
                new IgnoreWorldStatsListener(), relPath );

        SolutionDemo demo = new SolutionDemo( solnCmdLine, world );
        boolean solved = SolutionRunner.runSolution(
            demo.solution, world, System.out, genTest );
        printResult(solved, System.out);
    }

    private static void printResult(
        boolean solved,
        PrintStream out
    )
    {
        if ( solved )
        {
            out.println( t( "You won!" ) );
        }
        else
        {
            out.println( t( "You lost." ) );
        }
    }

    private static void listRel()
    {
        final String d = File.separator;
        String dir =
            "rabbit-escape-engine" + d + "bin" + d +
            "rabbitescape" + d + "levels";
        RealFileSystem fs = new RealFileSystem();
        for ( String s : fs.ls( dir, true ) )
        {
            if ( s.endsWith( ".rel" ) )
            {
                System.out.println( s );
            }
        }

    }

    public static void placeholders( String fileName ) throws IOException
    {
        RealFileSystem fs = new RealFileSystem();
        // Decoded while parsing
        World world = new LoadWorldFile( fs ).load(
            new IgnoreWorldStatsListener(), fileName );
        world = ensureHints(world);
        String[] lines =
            TextWorldManip.renderCompleteWorld( world, true, false );
        fs.write( fileName, Util.join( "\n", lines ) + "\n" );
    }

    private static World ensureHints( World world )
    {
        if ( world.hints.length >= 3 )
        {
            return world;
        }
        else
        {
            return new World(
                world.size,
                world.blockTable,
                world.rabbits,
                world.things,
                world.waterTable,
                world.abilities,
                world.name,
                world.description,
                world.author_name,
                world.author_url,
                threeHints(world.hints),
                world.solutions,
                world.num_rabbits,
                world.num_to_save,
                world.rabbit_delay,
                world.music,
                world.num_saved,
                world.num_killed,
                world.num_waiting,
                0,
                world.paused,
                world.comments,
                new IgnoreWorldStatsListener(),
                world.voidStyle
            );
        }
    }

    private static String[] threeHints( String[] oldHints )
    {
        String[] ret = new String[3];

        for (int i = 0; i < 3; ++i)
        {
            ret[i] = ( oldHints.length > i ) ? oldHints[i] : "";
        }

        return ret;
    }

    public static void template( String fileName ) throws IOException
    {
        RealFileSystem fs = new RealFileSystem();
        String[] world = {
            "      ", "      ", "      ",
            "      ", "      ", "      "
        };
        String[] lines = TextWorldManip.renderCompleteWorld(
            TextWorldManip.createWorld( world ), true, false );
        fs.write( fileName, Util.join( "\n", lines ) + "\n" );
    }

    private void run( String[] args )
    {
        textMenu.run();
    }

    public static void usageMessage()
    {
        // Note cheat mode option for text mode ( --mars ) is not listed:
        // It's a secret
        for ( String s : Util.resourceLines( "/rabbitescape/cli-help.txt" ) )
        {
            System.out.println( t( s ) );
        }
    }

}

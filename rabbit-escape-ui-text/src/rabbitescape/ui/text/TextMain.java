package rabbitescape.ui.text;

import rabbitescape.engine.IgnoreWorldStatsListener;
import rabbitescape.engine.LoadWorldFile;
import rabbitescape.engine.World;
import rabbitescape.engine.config.Config;
import static rabbitescape.engine.i18n.Translation.*;
import rabbitescape.engine.i18n.Translation;
import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.engine.util.CommandLineOption;
import rabbitescape.engine.util.CommandLineOptionSet;
import rabbitescape.engine.util.FileSystem;
import rabbitescape.engine.util.RealFileSystem;
import rabbitescape.engine.util.Util;

import java.io.IOException;
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
        
        CommandLineOption level =        new CommandLineOption( "--level",        true );
        CommandLineOption solution =     new CommandLineOption( "--solution",     true );
        CommandLineOption encode =       new CommandLineOption( "--encode",       true );
        CommandLineOption decode =       new CommandLineOption( "--decode",       true );
        CommandLineOption help =         new CommandLineOption( "--help",         false );
        CommandLineOption noinput =      new CommandLineOption( "--noinput",      true );
        CommandLineOption placeholders = new CommandLineOption( "--placeholders", true );
        
        try 
        {
            CommandLineOptionSet.parse( args,
                                        level, solution, encode, decode, help, 
                                        noinput, placeholders );
            if ( help.isPresent() )
            {
                usageMessage();
                System.exit( 0 );
            }
            if ( noinput.isPresent() )
            {
                TextSingleGameEntryPoint.entryPoint( new String[] {noinput.getValue(), "noinput"} );
            }
            if ( solution.isPresent() )
            {
                new SolutionDemo( level.getValue(), solution.getInt() );
                System.exit( 0 );
            }
            if ( level.isPresent() )
            {
                TextSingleGameEntryPoint.entryPoint( args );
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
    

    public static void placeholders( String fileName ) throws IOException
    {
        RealFileSystem fs = new RealFileSystem();
        // Decoded while parsing
        World world = new LoadWorldFile( fs ).load( 
            new IgnoreWorldStatsListener(), fileName );
        String[] lines = TextWorldManip.renderCompleteWorld( world, true, true );
        fs.write( fileName, Util.join( "\n", lines ) );
    }
    
    private void run( String[] args )
    {
        textMenu.run();
    }
    
    public static void usageMessage()
    {
        //                                                          Eighty character limit >|
        System.out.println( t(
            "Any option can be abbreviated: '--help' becomes '-h'.\n" +
            "runrabbit --help                        (This) message and exit.\n" +
            "runrabbit                               Play using the swing GUI menus.\n" +
            "runrabbit swing                         Play using the swing GUI menus.\n" +
            "runrabbit swing level.rel               Play a single level using the swing GUI.\n" +
            "runrabbit text                          Play using the text UI.\n" +
            "runrabbit text level.rel                Play a single level using the text UI.\n" +
            "runrabbit text --level level.rel        Play a single level using the text UI.\n" +
            "runrabbit text --noinput level.rel      Level plays out with no token drops.\n" +
            "runrabbit text --level <file> --solution <n>   Print world steps.\n" +
            "runrabbit text --encode <string>        Obfuscate a string, for hints etc\n" +
            "runrabbit text --decode <string>        Deobfuscate.\n" +
            "runrabbit text --encode <level.rel>     Obfuscate hints and solutions.\n" +
            "runrabbit text --decode <level.code.rel>   Deobfuscate.\n" +
            "runrabbot text --placeholders <level.rel>  Rewrite file, inserting blank meta\n" +
            "                                           and re-ordering meta. Decodes.\n\n" +
            "When used with rel files the de/encode options will leave the source file\n" +
            "untouched, but may overwrite another file without further warning\n" +
            " foo.rel -> foo.code.rel          foo.code.rel -> foo.uncode.rel\n" +
            " foo.uncode.rel -> foo.code.rel   foo.rel -> foo.uncode.rel\n"
        ) );
    }
}

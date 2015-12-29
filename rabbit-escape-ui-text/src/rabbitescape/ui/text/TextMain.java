package rabbitescape.ui.text;

import rabbitescape.engine.config.Config;
import rabbitescape.engine.i18n.Translation;
import rabbitescape.engine.util.CommandLineOption;
import rabbitescape.engine.util.CommandLineOptionSet;
import rabbitescape.engine.util.FileSystem;
import rabbitescape.engine.util.MegaCoder;
import rabbitescape.engine.util.RealFileSystem;

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
        if ( args.length == 1 )
        {
            TextSingleGameEntryPoint.entryPoint( args );
            return;
        }
        
        CommandLineOption level =    new CommandLineOption( "--level",    true );
        CommandLineOption solution = new CommandLineOption( "--solution", true );
        CommandLineOption encode =   new CommandLineOption( "--encode", true );
        CommandLineOption decode =   new CommandLineOption( "--decode", true );
        
        try 
        {
            CommandLineOptionSet.parse( args,
                                        level, solution, encode, decode );
            if ( solution.isPresent() )
            {
                new SolutionDemo( level.getValue(), solution.getInt() );
                System.exit( 0 );
            }
            if ( encode.isPresent() )
            {
                System.out.println( MegaCoder.encode( encode.getValue() ) );
                System.exit( 0 );
            }
            if ( decode.isPresent() )
            {
                System.out.println( MegaCoder.encode( decode.getValue() ) );
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

    private void run( String[] args )
    {
        textMenu.run();
    }
}
